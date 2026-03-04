<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.MypageDAO"%>
<%@ page import="dto.MemberBasDTO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>


<%
// session에 저장된 전체 목록 확인 (디버깅용)
java.util.Enumeration<String> names = session.getAttributeNames();
System.out.println("=========== SESSION 정보확인 START ===========");
while(names.hasMoreElements()){
String name = names.nextElement();
Object value = session.getAttribute(name);
System.out.println(name + " : " + value);
}
System.out.println("=========== SESSION 정보확인 END ===========");
%>
<%
/* mypageForm.jsp
마이페이지 화면 + 서버측 로그인 체크 + 회원정보 조회 + UI(프로필/닉/이메일/비번변경/탈퇴/기자신청)
 */

 /*
마이페이지 화면을 렌더링하는 jsp
1. 로그인 여부를 세션(loginUser)으로 확인
2. 비로그인이면 redirectAfterLogin세션에 원래 가려던 주소를 저장 후 로그인 페이지 보냄
3.로그인 상태면 DAO로 DB에서 최신 회원정보를 조회하여 화면에 뿌림
*/

//1) 접근 제어
MemberBasDTO loginUser = (MemberBasDTO) session.getAttribute("loginUser");
	if ( loginUser == null) {
		
		String target = request.getRequestURI();
	    String qs = request.getQueryString();
	    if (qs != null) target += "?" + qs;
	    session.setAttribute("redirectAfterLogin", target);
	    response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?msg=needLogin");
	      return;
	}
	
	MypageDAO dao = MypageDAO.getInstance();
	MemberBasDTO info = dao.getMemberInfo(loginUser.getMemberId());
	if (info == null) {
		request.getRequestDispatcher("/main")
	       .forward(request, response);
		return;
	}
	
	   String raw = (info.getProfileImgUrl() == null) ? "" : info.getProfileImgUrl().trim();

	   String profilePath;
	   if (raw.isEmpty()) {
	       profilePath = request.getContextPath() + "/resources/image/profile.images.webp.jpeg";
	   } else {
	       profilePath = request.getContextPath() + raw;
	   }
	
	//2) 화면 표시값
	String memberNm = info.getMemberNm();
	String nickNm = info.getNickNm();
	String email = info.getEmail();

	 
	String birthRaw = (info.getBirth() == null) ? "" : info.getBirth().trim();
	String birthFmt = birthRaw;
	try{
		Date d = null;
		if (birthRaw.matches("\\d{4}-\\d{2}-\\d{2}")){
			d = new SimpleDateFormat("yyyy-MM-dd").parse(birthRaw);
		}else if (birthRaw.matches("\\d{8}")){
			d = new SimpleDateFormat("yyyyMMdd").parse(birthRaw);
		}else if (birthRaw.matches("\\d{4}\\.\\d{2}\\.\\d{2}")){
			d = new SimpleDateFormat("yyyy.MM.dd").parse(birthRaw);
		}
		if (d != null) birthFmt = new SimpleDateFormat("yyyy-MM-dd").format(d);
	} catch(Exception e) {
		birthFmt = birthRaw;
	}
	
		String journalCd = (info.getJournalReasonCd() == null) ? "0" : info.getJournalReasonCd().trim();
		String journalDesc = (info.getJournalReasonDesc() == null) ? "" : info.getJournalReasonDesc().trim();

		/* ✅ 관리자 처리 사유(반려/보류/승인 코멘트) */
		String procReason = "";
		try {
		    procReason = (info.getProcReason() == null) ? "" : info.getProcReason().trim();
		} catch (Exception e) {
		    procReason = "";
		}
%>

<!DOCTYPE html>
<html lang = "ko">
<head>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/mypage.css">
<meta charset="UTF-8">
<title>TRAVEL+ | 마이페이지</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body>
<div class="topbar">
  <a href="<%=request.getContextPath()%>/main" class="logo">TRAVEL+</a>
  <div class="logout" onclick="logout()">로그아웃</div>
</div>

<div class="main">
  <div class="content">

    <!-- 프로필 -->
    <div class="profile">
      <div class="profile-img-wrap">
        <img src="<%= profilePath %>" id="profileImg" class="profile-img" alt="profile">
        <div class="edit-icon" onclick="openProfilePicker()">✏️</div>
      </div>
      
       <!-- 실제 업로드용 input(숨김) -->
        <form id="profileForm" enctype="multipart/form-data" style="display:none;">
            <input type="file" id="profileFile" name="profileFile" accept="image/*">
        </form>

      <div class="nickname">
        <span id="nicknameView"><%= nickNm %></span>
        <span onclick="startNickEdit()">✏️</span>
      </div>
    </div>
    
    <div class="nick-edit" id="nickEditArea" style="display:none; justify-content:center; margin-top:10px;">
            <input type="text" id="nickInput" placeholder="새 닉네임">
            <button type="button" class="small-btn" onclick="saveNick()">저장</button>
            <button type="button" class="small-btn" onclick="cancelNickEdit()">취소</button>
        </div>
    </div>
    

    <!-- 기록 -->
    <div class="section">
      <div class="section-title">기록</div>
      <div class="list-item" id="btnLiked">❤️ 내가 좋아요 한 곳</div>
	 <div class="list-item" id="btnMyArticles">✍️ 내가 작성한 글 확인</div>
    </div>

    <!-- 내정보 -->
    <div class="section">
      <div class="section-title">내정보</div>
      
      <div class="info-row">
        <div>이름</div>
        <div><%= memberNm %></div>
      </div>

      <div class="info-row">
        <div>이메일</div>
        <div class="email-area" >
          <span id="emailView"><%= email %></span>
          <button type="button" class="soft-btn" onclick="startEmailEdit()">수정</button>

          <div class="email-edit" id="emailEditArea">
            <input type="text" id="emailId" placeholder="이메일아이디">
            <span>@</span>
            <select id="emailDomain" onchange="toggleDomainInput()">
              <option value="naver.com">naver.com</option>
              <option value="daum.net">daum.net</option>
              <option value="nate.com">nate.com</option>
              <option value="gmail.com">gmail.com</option>
              <option value="outlook.com">outlook.com</option>
              <option value="custom">직접입력</option>
            </select>
            <input type="text" id="customDomain" placeholder="직접입력" style="display:none;">
            <button type="button" class="small-btn" onclick="saveEmail()">저장</button>
            <button type="button" class="small-btn" onclick="cancelEmailEdit()">취소</button>
          </div>
        </div>
      </div>

      <div class="info-row">
        <div>생년월일</div>
        <div><%= birthFmt %></div>
      </div>

    <!-- 내정보 row 안에 넣기 -->
<div class="info-row">
  <div class="info-label">비밀번호 변경</div>
  <div class="info-value">
    <button type="button" class="soft-btn" onclick="openPwModal()">변경</button>
  </div>
</div>

<!-- ✅ 비밀번호 변경 모달 -->
<div id="pwModal" class="modal" style="display:none;">
  <div class="modal-backdrop" onclick="closePwModal()"></div>

  <div class="modal-card">
    <div class="modal-head">
      <div class="modal-title">비밀번호 변경</div>
      <button type="button" class="x-btn" onclick="closePwModal()">✕</button>
    </div>

    <!-- 1단계: 현재 비번 확인 -->
    <div id="pwStep1" class="modal-body">
      <div class="hint">현재 비밀번호를 입력하세요.</div>

      <label class="f-label">현재 비밀번호 <span class="req">*</span></label>
      <input type="password" id="curPw" class="f-input" placeholder="현재 비밀번호" autocomplete="current-password" />

      <div class="btn-row">
        <button type="button" class="btn primary" onclick="verifyCurrentPw()">확인</button>
        <button type="button" class="btn" onclick="closePwModal()">취소</button>
      </div>
      <div id="pwMsg1" class="msg"></div>
    </div>

    <!-- 2단계: 새 비번 변경 -->
    <div id="pwStep2" class="modal-body" style="display:none;">
      <div class="hint">새 비밀번호를 설정하세요.</div>

      <label class="f-label">새 비밀번호 <span class="req">*</span></label>
      <input type="password" id="newPw" class="f-input" placeholder="새 비밀번호" autocomplete="new-password" />

      <label class="f-label">새 비밀번호 확인 <span class="req">*</span></label>
      <input type="password" id="newPw2" class="f-input" placeholder="새 비밀번호 확인" autocomplete="new-password" />

      <div class="btn-row">
        <button type="button" class="btn primary" onclick="changePassword()">변경</button>
        <button type="button" class="btn" onclick="backToStep1()">뒤로</button>
      </div>
      <div id="pwMsg2" class="msg"></div>
    </div>
  </div>
</div>
    <!-- 회원탈퇴 -->
    <div class="info-row">
  <div class="withdraw" onclick="openWithdrawFlow()">회원 탈퇴</div>
</div>

<!-- 모달은 페이지 어딘가(보통 </body> 위) 한 번만 둬 -->
<div id="withdrawModal" class="modal" style="display:none;">
  <div class="modal-backdrop" onclick="closeWithdrawModal()"></div>

  <div class="modal-card" style="width:420px; margin:180px auto 0;">
    <div class="modal-head">
      <div class="modal-title">회원 탈퇴</div>
      <button type="button" class="x-btn" onclick="closeWithdrawModal()">✕</button>
    </div>

    <div class="field">
      <label class="label">비밀번호 확인 <span class="req">*</span></label>
      <input class="input" type="password" id="withdrawPw"
             placeholder="비밀번호 입력" autocomplete="current-password">
    </div>

    <div id="withdrawMsg" class="msg" style="display:none;"></div>

    <div class="btnrow">
      <button type="button" class="btn" onclick="closeWithdrawModal()">취소</button>
      <button type="button" class="btn primary" onclick="withdrawConfirm()">탈퇴하기</button>
    </div>
  </div>
</div>


    <!-- 기자 등급 신청 -->
    <div class="section">
      <div class="section-title">회원 → 기자 등급 변경 신청</div>

      <div class="apply-box">
        <div class="notice">※ 가입 이후 7일이 경과되어야 신청이 가능합니다.</div>

        <% if ("1".equals(journalCd)) { %>
          <div class="pending apply-msg">
신청이 접수되었습니다.
3~5일 정도 소요됩니다.
기자 등급 심사중...
          </div>
          
          <button type="button" class="soft-btn" onclick="cancelJournal()">신청 취소</button>

        <% } else if ("2".equals(journalCd)) { %>
          <div class="pending">이미 기자 회원입니다.</div>

        <% } else if ("3".equals(journalCd)) { %>
        	<div class="pending apply-msg">
            신청이 반려되었습니다.<br>
            <% if (!procReason.isEmpty()) { %>
              반려 사유: <%= procReason %><br>
            <% } %>
            (기존 사유: <%= journalDesc %>)
          </div>
       
          <div class="pending apply-msg">
신청이 반려되었습니다.
사유를 수정해서 다시 신청해주시길 바랍니다.
(기존 사유: <%= journalDesc %>)
          </div>

          <textarea id="reason" placeholder="수정된 신청 사유를 입력해주세요."></textarea>
          <button type="button" class="soft-btn" onclick="applyJournal()">재신청</button>
          <div id="applyResult" class="pending apply-msg"></div>
		
		<%-- 4) 보류(4) --%>
        <% } else if ("4".equals(journalCd)) { %>
          <div class="pending apply-msg">
            현재 신청이 보류되었습니다.<br>
            <% if (!procReason.isEmpty()) { %>
              보류 사유: <%= procReason %>
            <% } else { %>
              관리자 확인 중입니다.
            <% } %>
          </div>
		
		
        <% } else { %>
          <textarea id="reason" placeholder="변경 신청 사유를 입력해주세요.
ex) 아름다운 여행지를 추천하고 싶습니다. 세상의 맛집을 소개합니다."></textarea>

          <button type="button" class="soft-btn" onclick="applyJournal()">신청</button>
          <div id="applyResult" class="pending apply-msg"></div>
        <% } %>
      </div>
    </div>

  </div> <!-- /.content -->
</div>   <!-- /.main -->

<footer>
	<button type="button">저작권</button>
    <button type="button">개인정보처리방침</button>
    <button type="button">이용약관</button>
    <button type="button">문의</button>
</footer>
<script>
const ctx = "<%=request.getContextPath()%>";
const memberStatCd = "<%=(info.getMemberStatCd()==null?"":info.getMemberStatCd().trim())%>";
</script>

<script src="<%=request.getContextPath()%>/resources/js/mypage.js"></script>
<!-- ✅ 좋아요 목록 모달 -->
<div id="likedModal" class="modal" style="display:none;">
  <div class="modal-backdrop" onclick="closeLikedModal()"></div>

  <div class="modal-card modal-wide">
    <div class="modal-head">
      <div class="modal-title">내가 좋아요 한 게시물</div>
      <button type="button" class="x-btn" onclick="closeLikedModal()">✕</button>
    </div>

    <div class="modal-body">
      <table style="width:100%;">
        <thead>
          <tr>
            <th class="col-article">기사번호</th>
            <th class="col-writer">작성자ID</th>
            <th>제목</th>
            <th class="col-date">작성일</th>
          </tr>
        </thead>
        <tbody id="likedTbody"></tbody>
      </table>

      <div id="likedEmpty" style="display:none; padding:16px; text-align:center; color:#888;">
        좋아요 한 게시물이 없습니다.
      </div>

      <div id="likedPager" style="display:flex; gap:6px; justify-content:center; margin-top:12px; flex-wrap:wrap;"></div>
    </div>
  </div>
</div>


<!-- ✅ 내가 작성한 글 모달 -->
<div id="myArticlesModal" class="modal" style="display:none;">
  <div class="modal-backdrop" onclick="closeMyArticlesModal()"></div>

<div class="modal-card modal-wide">

    <div class="modal-head">
      <div class="modal-title">내가 작성한 글</div>
      <button type="button" class="x-btn" onclick="closeMyArticlesModal()">✕</button>
    </div>

    <div class="modal-body">
      <table style="width:100%;">
        <thead>
          <tr>
            <th class="col-article">기사번호</th>
            <th>제목</th>
            <th class="col-date">작성일</th>
            <th class="col-cnt">조회수</th>
          </tr>
        </thead>
        <tbody id="myArticlesTbody"></tbody>
      </table>

      <div id="myArticlesEmpty" style="display:none; padding:16px; text-align:center; color:#888;">
        작성한 글이 없습니다.
      </div>

      <div id="myArticlesPager" style="display:flex; gap:6px; justify-content:center; margin-top:12px; flex-wrap:wrap;"></div>
    </div>
  </div>
</div>

</body>

</html>



