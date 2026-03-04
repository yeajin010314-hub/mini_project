<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%
    String savedId = (String) session.getAttribute("savedId");
    Integer autoCheck = (Integer) session.getAttribute("autoCheck");
    boolean isChecked = (savedId != null) && (autoCheck != null && autoCheck == 1);

	String restored = request.getParameter("restored");
	
	String err = request.getParameter("err");
	String msg = request.getParameter("msg");

	if ("needLogin".equals(msg) && (err == null || err.trim().isEmpty())) {
	%>
	<script>
	  alert("로그인 후 이용해주세요.");
	</script>
	<%
	}
%>


<!-- loginForm.jsp 
1)화면기능
-로그인 입력(아이디, 비밀번호)
-아이디 저장 체크박스
-에러 상황 메시지 출력(로그인 실패/빈값)
-회원가입 페이지 이동
-아이디 찾기 모달(이름/생년월일/이메일 입력)
-비밀번호 재설정 모달(아이디/이메일/새 비번/확인)

2)서버 연동
-로그인은 /member/loginPro 서블릿(또는 JSP)로 POST 전송
-아이디/비번 찾기/재설정은 JS에서 별도 요청할 가능성이 큼(코드는 loginForm.js에 있음) -->


<!DOCTYPE html>
<html lang="ko">
<head>
<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/loginForm.css">
<meta charset="UTF-8">
<title>TRAVEL+ | 로그인</title>
<meta name = "viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body onload="begin()">

<div class="header">
    <div class="logo">
        <a href="<%=request.getContextPath()%>/main">TRAVEL+</a>
    </div>
</div>

<div class="container">
<div class="login-card">
    <h2>로그인</h2>

<%-- ================= 메시지 영역(통합본) ================= --%>
	   <% if ("1".equals(restored)) { %>
	  <div class="msg ok">계정이 복구되었습니다. 다시 로그인해주세요.</div>
	<% } %>
	
	<% if ("empty".equals(err)) { %>
	  <div class="msg">아이디/비밀번호를 입력하세요.</div>
	<% } else if ("1".equals(err)) { %>
	  <div class="msg">아이디 또는 비밀번호가 올바르지 않습니다.</div>
	<% } else if ("leaved_expired".equals(err)) { %>
	  <div class="msg">탈퇴 후 3개월이 지나 계정 복구가 불가능합니다.</div>
	<% } else if ("restore_fail".equals(err)) { %>
	  <div class="msg">계정 복구 처리 중 오류가 발생했습니다.</div>
	<% } else if ("restore_no_session".equals(err)) { %>
	  <div class="msg">복구 요청 정보가 만료되었습니다. 다시 로그인 시도해주세요.</div>
	<% } %>
    <%-- ======================================================= --%>

	
    <form action="<%=request.getContextPath()%>/member/loginPro"
      name="myForm"
      method="post"
      onsubmit="return checkIt()">

        <div class="input-group">
            <label>아이디</label>
            <input type="text" name="memberId" id="loginId"
                   value="<%= savedId != null ? savedId : "" %>">
        </div>

        <div class="input-group">
            <label>비밀번호</label>
            <input type="password" name="passwd" id="loginPw">
        </div>

        <div class="saved-id">
            <label>
                <input type="checkbox" name="autoid" value="1"
                       <%= isChecked ? "checked" : "" %>>
                아이디 저장
            </label>
        </div>

        <input type="submit" value="로그인" class="login-btn">
    </form>

    <div class="find-links">
        <a href="javascript:void(0)" onclick="openFindModal('id')">아이디 찾기</a>
        <span class="bar">|</span>
        <a href="javascript:void(0)" onclick="openFindModal('pw')">비밀번호 찾기</a>
        <span class="bar">|</span>
        <a href="<%=request.getContextPath()%>/member/join/joinForm2.jsp">회원가입</a>
   		
    </div>
</div>
</div>
<div id="findModal" class="modal" style="display:none;">
  <div class="modal-backdrop" onclick="closeFindModal()"></div>

  <div class="modal-card">
    <div class="modal-head">
      <div class="modal-title" id="findModalTitle">아이디 찾기</div>
      <button type="button" class="x-btn" onclick="closeFindModal()">✕</button>
    </div>

    <div class="tabs">
      <button type="button" class="tab active" id="tab_find_id" onclick="switchFindTab('id')">아이디 찾기</button>
      <button type="button" class="tab" id="tab_find_pw" onclick="switchFindTab('pw')">비밀번호 재설정</button>
    </div>

    <!-- ================== 아이디 찾기 ================== -->
    <div id="panel_find_id" class="panel">
      <div class="field">
        <label class="label">이름 <span class="req">*</span></label>
        <input class="input" id="fi_name" placeholder="예) 홍길동">
      </div>

      <div class="field">
        <label class="label">생년월일 <span class="req">*</span></label>
        <input class="input" id="fi_birth" type="date" >
      </div>

      <div class="field">
        <label class="label">이메일 <span class="req">*</span></label>
        <input class="input" id="fi_email" placeholder="예) abcd123@naver.com">
      </div>

      <div class="btnrow">
        <button type="button" class="btn primary" onclick="findId()">아이디 찾기</button>
        <button type="button" class="btn" onclick="resetFindId()">초기화</button>
      </div>

      <div id="fi_msg" class="msg"></div>
    </div>

    <!-- ================== 비밀번호 재설정 ================== -->
    <div id="panel_find_pw" class="panel" style="display:none;">
      <div class="field">
        <label class="label">아이디 <span class="req">*</span></label>
        <input class="input" id="rp_id" placeholder="아이디 입력">
      </div>

      <div class="field">
        <label class="label">이메일 <span class="req">*</span></label>
        <input class="input" id="rp_email" placeholder="예) abcd123@naver.com">
      </div>

      <div class="field">
        <label class="label">새 비밀번호 <span class="req">*</span></label>
        <input class="input" type="password" id="rp_pw" placeholder="4~20자" autocomplete="new-password">
      </div>

      <div class="field">
        <label class="label">새 비밀번호 확인 <span class="req">*</span></label>
        <input class="input" type="password" id="rp_pw2" placeholder="확인" autocomplete="new-password">
      </div>

      <div class="btnrow">
        <button type="button" class="btn primary" onclick="resetPassword()">비밀번호 변경</button>
        <button type="button" class="btn" onclick="resetFindPw()">초기화</button>
      </div>

      <div id="rp_msg" class="msg"></div>
    </div>

  </div>
</div>

<script>
const ctx = "<%=request.getContextPath()%>";
</script>	
	
<script src="<%=request.getContextPath()%>/resources/js/loginForm.js"></script>

</body>
</html>
