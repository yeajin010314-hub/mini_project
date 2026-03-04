<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="dto.AdminMemberStatsDTO" %>
<%@ page import="dto.MemberBasDTO" %>
<%@ page import="dto.CategoryDTO" %>

<%@ page import="java.util.List" %>
<%@ page import="dto.ArticleDetailViewDTO" %>
<%@ page import="dto.MemberBasDTO" %>


<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<title>관리자용 대시보드</title>
	<link href="https://fonts.googleapis.com/css2?family=Roboto&display=swap" rel="stylesheet">
	<link href="${pageContext.request.contextPath}/resources/css/dashBoard.css" rel="stylesheet">
	<script src="${pageContext.request.contextPath}/resources/js/dashBoard.js"></script>
</head>

<body>
<%
	// session에 저장된 전체 목록 확인 (디버깅용)
	java.util.Enumeration<String> names = session.getAttributeNames();
	System.out.println("=========== SESSION 정보확인 START ===========");
	
	while(names.hasMoreElements()){
	    String name = names.nextElement();
	    Object value = session.getAttribute(name);
	    System.out.println(name + " : " + value);
	}
    System.out.println("=========== SESSION 정보확인  END  ===========");
%>

<%
	System.out.println("============= dashboard.jsp =============");
	// Session 가져오기
	MemberBasDTO user = (MemberBasDTO) session.getAttribute("loginUser");
	System.out.println("user : [" + user + "]");
	
	// 경로
	String ctx = request.getContextPath();
	
	String loginForm = "/member/login/loginForm2.jsp";			// 로그인
	String memberJoinForm = "/member/join";						// 회원가입
	String mypageForm = "/member/mypage/mypageForm2.jsp";		// 마이페이지
	String boardForm = "/articleboard/board/boardList.jsp";		// 게시판
	String adminDashboardForm = "/memberBasStats";				// 관리자 대시보드
	String mainPage = "/main";									// 메인 페이지
	String logout = "/logout";
	
%>

<header class="header">
    <a href="<%=request.getContextPath()%>/main" 
       style="color:#000; font-size:20px; font-weight:bold; text-decoration:none;">
        TRAVEL+
    </a>

	<div class="menu">
<% 
 		if (user == null) {
%>
		    <a href="${pageContext.request.contextPath}<%= loginForm%>">로그인</a>
		    <a href="${pageContext.request.contextPath}<%= memberJoinForm%>">회원가입</a>
	        <a href="${pageContext.request.contextPath}<%= boardForm%>" id="board-btn">게시판</a>
<% 
		} else { 
%>
		    <h4><span><Strong style="color:blue;"><%= (user.getNickNm() != null) ? user.getNickNm() : null %></Strong>님 환영합니다!</span></h4>
<%
			// 관리자일때만 표시
			if ("3".equals(user.getMemberStatCd())) {
%>
				<a href="${pageContext.request.contextPath}<%= mainPage%>" >메인 페이지</a>	
<% 
			}
%>
		    <a href="${pageContext.request.contextPath}<%= mypageForm%>" id="board-btn">마이페이지</a>
	        <a href="${pageContext.request.contextPath}<%= boardForm%>" id="board-btn">게시판</a>
	        
		    <a href="${pageContext.request.contextPath}<%= logout%>">로그아웃</a>
<% 
		} 
%>
	</div>
</header>

<div class="container">

    <h2>관리자 대시보드</h2>

    <!-- 요약 카드 -->
    <div class="flex">
        <div class="card" id="memberSummary">
            <h3>회원 현황</h3>

<%-- statsList : List<AdminMemberStatsDTO> --%>
<%
	String cdnm = "";	// 코드명
	int cnt = 0;		// 건수

	int countMember = 0;		// 일반회원
    int countReporter = 0;		// 기자
    int countAdmin = 0;			// 관리자
    int countWithdraw = 0;		// 탈퇴
    
    if(request.getAttribute("statsList") != null) {
        List<AdminMemberStatsDTO> statsList = (List<AdminMemberStatsDTO>) request.getAttribute("statsList");
        
		if(statsList != null) {
		    for(AdminMemberStatsDTO stat : statsList) {
		        cdnm = stat.cd_nm();
		        cnt = stat.cnt();
		
		        if("회원".equals(cdnm)) {
		            countMember = cnt;
		        } else if("기자".equals(cdnm)) {
		            countReporter = cnt;
		        } else if("관리자".equals(cdnm)) {
		            countAdmin = cnt;
		        } else if("탈퇴".equals(cdnm)) {
		            countWithdraw = cnt;
		        }
		    }
		}

    }
%>
			<p>회원: <span id="countMember"><%= countMember %></span></p>
			<p>기자: <span id="countReporter"><%= countReporter %></span></p>
			<p>관리자: <span id="countAdmin"><%= countAdmin %></span></p>
			<p>탈퇴: <span id="countWithdraw"><%= countWithdraw %></span></p>
        </div>
        
        <div class="card" id="requestSummary">
            <h3>등록 요청</h3>
<%-- reqReportStat : List<AdminMemberStatsDTO> --%>
<% 
	int countApply = 0;		// 승인
	int countReject = 0;	// 반려
	int countHold = 0;		// 보류
	int countPending = 0;	// 대기

if(request.getAttribute("reqReportStat") != null) {
    List<AdminMemberStatsDTO> reqReportStat = (List<AdminMemberStatsDTO>) request.getAttribute("reqReportStat");
    
	if(reqReportStat != null) {
	    for(AdminMemberStatsDTO reqReport : reqReportStat) {
	        cdnm = reqReport.cd_nm();
	        cnt = reqReport.cnt();
	
	        if("승인".equals(cdnm)) {
	        	countApply = cnt;
	        } else if("반려".equals(cdnm)) {
	        	countReject = cnt;
	        } else if("보류".equals(cdnm)) {
	        	countHold = cnt;
	        } else if("대기".equals(cdnm)) {
	        	countPending = cnt;
	        }
	    }
	}
}

%>
            <p>승인: <span id="countApproved"><%= countApply %></span></p>
            <p>반려: <span id="countRejected"><%= countReject %></span></p>
            <p>보류: <span id="countHold"><%= countHold %></span></p>
            <p>대기: <span id="countPending"><%= countPending %></span></p>
        </div>
        
        <div class="card" id="categorySummary">
            <h3>카테고리</h3>
<%-- categoryStat : List<AdminMemberStatsDTO> --%>
<% 
	int countRoot = 0;		// 상위카테고리
	int countLeaf = 0;	// 하위카테고리

if(request.getAttribute("categoryStat") != null) {
    List<AdminMemberStatsDTO> categoryStat = (List<AdminMemberStatsDTO>) request.getAttribute("categoryStat");
    
	if(categoryStat != null) {
	    for(AdminMemberStatsDTO category : categoryStat) {
	        cdnm = category.cd_nm();
	        cnt = category.cnt();
	
	        if("상위".equals(cdnm)) {
	        	countRoot = cnt;
	        } else if("하위".equals(cdnm)) {
	        	countLeaf = cnt;
	        }
	    }
	}
}
%>
            <p>상위 카테고리 : <span id="countCategory"><%=countRoot %></span></p>
            <p>하위 카테고리 : <span id="countCategory"><%=countLeaf %></span></p>

<!--             <p>총: <span id="countCategory">0</span></p> -->
        </div>
    </div>

<%
	String memberPageNum = request.getParameter("memberPageNum");
	String categoryPageNum = request.getParameter("categoryPageNum");
	String journalPageNum = request.getParameter("journalPageNum");

	System.out.println("memberPageNum : [" + memberPageNum + "]");
	System.out.println("categoryPageNum : [" + categoryPageNum + "]");
	System.out.println("journalPageNum : [" + journalPageNum + "]");

	// 페이지번호가 null 또는 빈문자열(?pageNum=)일때 처리
	if (memberPageNum == null || memberPageNum.trim().isEmpty())
		memberPageNum = "1";
	if (categoryPageNum == null || categoryPageNum.trim().isEmpty())
		categoryPageNum = "1";
	if (journalPageNum == null || journalPageNum.trim().isEmpty())
		journalPageNum = "1";
	
	// 회원관리 페이지 번호 가져오기
    int memberCurrentPage = request.getAttribute("memberCurrentPage") != null
            ? (Integer) request.getAttribute("memberCurrentPage") : 1;

    int memberPageCount = request.getAttribute("memberPageCount") != null
            ? (Integer) request.getAttribute("memberPageCount") : 1;
    
	int memberStartPage = request.getAttribute("memberStartPage") != null
	        ? (Integer) request.getAttribute("memberStartPage") : 1;

	int memberEndPage = request.getAttribute("memberEndPage") != null
      			? (Integer) request.getAttribute("memberEndPage") : 1;

 	// 카테고리 페이지 번호 가져오기
    int categoryCurrentPage = request.getAttribute("categoryCurrentPage") != null
            ? (Integer) request.getAttribute("categoryCurrentPage") : 1;

    int categoryPageCount = request.getAttribute("categoryPageCount") != null
            ? (Integer) request.getAttribute("categoryPageCount") : 1;
    
	int categoryStartPage = request.getAttribute("categoryStartPage") != null
	        ? (Integer) request.getAttribute("categoryStartPage") : 1;

	int categoryEndPage = request.getAttribute("categoryEndPage") != null
      			? (Integer) request.getAttribute("categoryEndPage") : 1;

 	// 기자등록 요청 페이지 번호 가져오기
    int journalCurrentPage = request.getAttribute("journalCurrentPage") != null
            ? (Integer) request.getAttribute("journalCurrentPage") : 1;

    int journalPageCount = request.getAttribute("journalPageCount") != null
            ? (Integer) request.getAttribute("journalPageCount") : 1;
    
	int journalStartPage = request.getAttribute("journalStartPage") != null
	        ? (Integer) request.getAttribute("journalStartPage") : 1;

	int journalEndPage = request.getAttribute("journalEndPage") != null
      			? (Integer) request.getAttribute("journalEndPage") : 1;
%>
	<!-- 관리자전체의 페이지 번호  End  -->

    <!-- 회원 관리 -->
    <div class="section">
        <div class="section-header" onclick="toggleSection(this)">
            <span class="arrow">▼</span> 회원 관리
        </div>
        <div class="section-content">
            <div class="search-box">
                <input type="text" id="userSearch" placeholder="ID / 이름 / 이메일" oninput="filterUsers()">
            </div>

            <form method="post" action="${pageContext.request.contextPath}/memberBasUpdate">
	            <table>
	                <thead>
	                    <tr>
	                        <th><input type="checkbox" onclick="toggleAll(this,'user')"></th>
	                        <th>ID</th>
	                        <th>이름</th>
	                        <th>닉네임</th>
	                        <th>생일</th>
	                        <th>이메일</th>
	                        <th>전화번호</th>
	                        <th>기자신청명</th>
<!-- 	                        <th>등록일자</th> -->
	                        <th>가입일</th>
	                        <th>탈퇴일</th>
	                        <th>역할</th>
	                    </tr>
	                </thead>
	                <tbody id="userTable">
<%-- memberList : List<MemberBasDTO> --%>
<%
	   				List<MemberBasDTO> memberList = (List<MemberBasDTO>) request.getAttribute("memberList");
	
				    if(memberList != null && !memberList.isEmpty()) {
				        for(MemberBasDTO m : memberList) {
%>
					    <tr>
					        <td>
					        	<input type="checkbox"
				                       class="user-check"
				                       name="memberId"
				                       value="<%= m.getMemberId() %>"
				                       onchange="toggleUserSelect(this)">
					        </td>
					        <td><%= m.getMemberId() %></td>
					        <td><%= m.getMemberNm() %></td>
					        <td><%= m.getNickNm() %></td>
					        <td><%= m.getBirth() %></td>
					        <td><%= m.getEmail() %></td>
					        <td><%= m.getTelNo() %></td>
					        <td><%= m.getJournalReasonNm() %></td>
<%-- 					        <td><%= m.getRegDt() %></td> --%>
					        <td><%= m.getJoinDt() %></td>
					        <td><%= m.getLeaveDt() %></td>
<%--  					        <td class="member-role role-<%= m.getMemberStatNm() %>"> --%>
<%--  					        	<%= m.getMemberStatNm() %> --%>
<!--  					        </td> -->
							<td>
<%--     							<select class="member-role role-<%= m.getMemberStatNm() %>" name="memberStatCd_<%= m.getMemberId() %>" > --%>
    							<select name="memberStatCd"
    									class="member-select %>" 
    									disabled >
<%
								List<AdminMemberStatsDTO> memberStatCdList = (List<AdminMemberStatsDTO>) request.getAttribute("memberStatCd");
								
								if(memberStatCdList != null){
									for(AdminMemberStatsDTO code : memberStatCdList){
								        String selected = code.cd_val().equals(m.getMemberStatCd()) ? "selected" : "";
%>
								        <option value="<%= code.cd_val() %>" <%= selected %>>
								            <%= code.cd_nm() %>
								        </option>
<%
									}
							    }
%>
    							</select>
							</td>
<!-- 							역할 -->
<!-- 				            <td> -->
<!-- 				                기본 텍스트 -->
<!-- 				                <span class="role-text"> -->
<%-- 				                    <%= m.getMemberStatNm() %> --%>
<!-- 				                </span> -->
				
<!-- 				                체크 시 활성화될 select -->
<%-- 				                <select name="memberStatCd_<%= m.getMemberId() %>" --%>
<!-- 				                        class="role-select" -->
<!-- 				                        disabled> -->
<%-- 				                    <option value="1" <%= "회원".equals(m.getMemberStatNm()) ? "selected" : "" %>>회원</option> --%>
<%-- 				                    <option value="2" <%= "기자".equals(m.getMemberStatNm()) ? "selected" : "" %>>기자</option> --%>
<%-- 				                    <option value="3" <%= "관리자".equals(m.getMemberStatNm()) ? "selected" : "" %>>관리자</option> --%>
<%-- 				                    <option value="0" <%= "탈퇴".equals(m.getMemberStatNm()) ? "selected" : "" %>>탈퇴</option> --%>
<!-- 				                </select> -->
<!-- 				            </td> -->
<!-- 				            역할 -->
					    </tr>
<%
				        }
				    } else {
%>
					    <tr>
					        <td colspan="5">회원 데이터가 없습니다.</td>
					    </tr>
<%
	    			}
%>
	                </tbody>
	            </table>

				<!-- 변경할 권한 선택 -->
<!-- 				<select class="role-buttons" name="memberStatCd" required onchange="updateRolesImmediately(this)"> -->
<!-- 				    <option value="">-- 권한 선택 --</option> -->
<%
// 	List<AdminMemberStatsDTO> statCd = (List<AdminMemberStatsDTO>) request.getAttribute("memberStatCd");

//     if(statCd != null){
//         for(AdminMemberStatsDTO code : statCd){
%>
<%--             <option value="<%= code.cd_val() %>"> --%>
<%--                 <%= code.cd_nm() %> --%>
<!--             </option> -->
<%
//         }
//     }
%>
<!-- 				</select> -->

	            <div class="role-buttons">
	            	<button class="btn btn-delete" type="submit">권한 변경</button>
	            	
	            	 <!-- 신규회원등록 버튼 -->
				    <button type="button"
				            class="btn btn-add"
				            onclick="location.href='${pageContext.request.contextPath}/member/join/joinForm2.jsp'">
				        	신규회원등록
				    </button>

				</div>
            </form>

            <%-- 처리 결과 출력 --%>
<%
		    // request 객체에서 message 가져오기
		    String message = (String) session.getAttribute("message");
			System.out.println("======== message ======== : [" + message + "]");

			// not empty 조건 체크
		    if (message != null && !message.isEmpty()) {
%>
		        <p class="result-msg"><strong>결과: </strong><%= message %></p>
<%
				session.removeAttribute("message");
    		}
%>
			            

<%-- 10단위 블록 페이지네이션 계산 --%>            
			<!-- 관리자전체의 페이지 번호 Start -->
						
			<div class="pagination">
			    <%-- 처음 페이지 --%>
			    <% if(memberCurrentPage > 1) { %>
			        <a href="?memberPageNum=1&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%=journalPageNum%>" class="page-btn">처음</a>
			    <% } %>

			    <%-- 이전 블록 버튼 --%>
			    <% if(memberStartPage > 1) { %>
			        <a href="?memberPageNum=<%= memberStartPage - 1 %>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%=journalPageNum%>" class="page-btn">이전</a>
			    <% } %>
			
				<%-- 페이지 번호 (10개 단위) --%>
			    <% for(int i = memberStartPage; i <= memberEndPage; i++) { %>
			        <% if(i == memberCurrentPage) { %>
			            <span class="page-btn active"><%= i %></span>
			        <% } else { %>
			            <a href="?memberPageNum=<%= i %>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%=journalPageNum%>" class="page-btn"><%= i %></a>
			        <% } %>
			    <% } %>
				
			    <%-- 다음 블록 버튼 --%>
			    <% if(memberEndPage < memberPageCount) { %>
			        <a href="?memberPageNum=<%= memberEndPage + 1 %>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%=journalPageNum%>" class="page-btn">다음</a>
			    <% } %>

			    <%-- 마지막 페이지 --%>
			    <% if(memberCurrentPage < memberPageCount) { %>
			        <a href="?memberPageNum=<%= memberPageCount %>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%=journalPageNum%>" class="page-btn">끝</a>
			    <% } %>
			</div>
			<!-- 회원정보 페이지 번호  END  -->

<!-- 			신규회원 추가 -->
<!-- 			<div class="add-member-box"> -->
<!-- 			    <input type="text"  id="newUserName" 		class="input-sm" placeholder="이름"> -->
<!-- 			    <input type="text"  id="newNickName" 		class="input-sm" placeholder="닉네임"> -->
<!-- 			    <input type="text"  id="newBirth" 			class="input-sm" placeholder="생일(YYYYMMDD)"> -->
<!-- 			    <input type="email" id="newUserEmail" 		class="input-lg" placeholder="이메일"> -->
<!-- 			    <input type="text"  id="newTelNo" 			class="input-md" placeholder="전화번호"> -->
<!-- 			    <input type="text"  id="newJournalReasonCd" class="input-md" placeholder="기자등록 사유"> -->
<!-- 			    <input type="text"  id="newMemberStatCd" 	class="input-sm" placeholder="회원상태"> -->
<!-- 			    <button type="button" class="btn btn-add" onclick="addUser()">신규회원 추가</button> -->
<!-- 			</div> -->
        </div>
    </div>

    <!-- 카테고리 관리 -->
    <div class="section">
        <div class="section-header" onclick="toggleSection(this)">
            <span class="arrow">▼</span> 카테고리 관리
        </div>
        <div class="section-content">
            <div class="search-box">
                <input type="text" id="categorySearch" placeholder="카테고리명" oninput="filterCategories()">
            </div>
<!--             <div class="add-category-box"> -->
<!--                 <input type="text" id="newCategory" placeholder="카테고리명"> -->
<!--                 <button class="btn btn-add" >추가</button> -->
<!--             </div> -->
            
            <form method="post" action="${pageContext.request.contextPath}/categoryUpdate">
            
	            <table>
	                <thead>
	                    <tr>
<!-- 	                        <th><input type="checkbox" onclick="toggleAll(this,'category')"></th> -->
	                        <th>
							    <input type="checkbox"
							           onclick="toggleAll(this, 'category')">
							</th>
	                        <th>카테고리 Id</th>
	                        <th>카테고리 명</th>
	                        <th>상위 카테고리명</th>
	                        <th>카테고리경로명</th>
	                        <th>레벨</th>
	                        <th>등록일자</th>
	                        <th>삭제</th>
	                    </tr>
	                </thead>
	                
	                <tbody id="categoryTable">

<%-- categoryList : List<CategoryDTO> --%>
<%
	   				List<CategoryDTO> categoryList = (List<CategoryDTO>) request.getAttribute("categoryList");
	
				    if(categoryList != null && !categoryList.isEmpty()) {
				        for(CategoryDTO m : categoryList) {
%>
					    <tr>
					        <td>
					        	<!-- 체크 시 삭제 가능 -->
								<input type="checkbox"
								       class="category-check"
								       onchange="toggleCategoryRow(this)">
								
					        </td>
					        
					        <td>
					        	<input name="categoryId" value="<%= m.getCategoryId() %>" disabled readonly>
					        </td>
					        <td>
					        		<input name="categoryNm" value="<%= m.getCategoryNm() %>" disabled
					        			oninput="validateCategoryNameInput(this)"><br />
					        		<span class="error-msg"></span>
					        </td>
					        <td>
					        	<input name="upCategoryId" value="<%= m.getUpCategoryId() %>" disabled>
					        </td>
					        
					        <td><%= m.getPathCategoryNm() %></td>
					        <td><%= m.getDepthLevel() %></td>
					        <%
					        	java.time.format.DateTimeFormatter fmt =
					        	java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

					    		String regDt = m.getRegDt().format(fmt);
							%>
							<td><%= regDt %></td>

					        <td>
					        		<input type="checkbox" 
					        				class="category-delete" 
					        				name="deleteCategoryIds" 
					        				value="Y" disabled>
					        </td>
					    </tr>
<%
				        }
				    } else {
%>
					    <tr>
					        <td colspan="5">카테고리 데이터가 없습니다.</td>
					    </tr>
<%
	    			}
%>
	                </tbody>
	            </table>
	            
            <%-- 처리 결과 출력 --%>
<%
		    // request 객체에서 message 가져오기
		    String messageCategory = (String) session.getAttribute("messageCategory");
			System.out.println("======== messageCategory ======== : [" + messageCategory + "]");

			// not empty 조건 체크
		    if (messageCategory != null && !messageCategory.isEmpty()) {
%>
		        <p class="result-msg"><strong>결과: </strong><%= messageCategory %></p>
<%
 				session.removeAttribute("messageCategory");
    		}
%>
			
	            <!-- 카테고리 수정 버튼 -->
	            <div class="category-buttons">
	            	<button class="btn btn-edit" type="submit">카테고리 수정</button>
	            </div>

				<!-- 카테고리 버튼 -->
<!-- 	            <div class="category-buttons"> -->
<!-- 	                <button type="button" class="btn btn-edit" onclick="editCategory()">수정</button> -->
<!-- 	                <button type="button" class="btn btn-delete" onclick="deleteCategorySelected()">삭제</button> -->
<!-- 	            </div> -->

            </form>
            
<%-- 10단위 블록 페이지네이션 계산 --%>            
			<!-- 카테고리관리 페이지 번호 Start -->
			<div class="pagination">
			    <%-- 처음 페이지 --%>
			    <% if(categoryCurrentPage > 1) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=1&journalPageNum=<%=journalPageNum%>" class="page-btn">처음</a>
			    <% } %>

			    <%-- 이전 블록 버튼 --%>
			    <% if(categoryStartPage > 1) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%= categoryStartPage - 1 %>&journalPageNum=<%=journalPageNum%>" class="page-btn">이전</a>
			    <% } %>
			
				<%-- 페이지 번호 (10개 단위) --%>
			    <% for(int i = categoryStartPage; i <= categoryEndPage; i++) { %>
			        <% if(i == categoryCurrentPage) { %>
			            <span class="page-btn active"><%= i %></span>
			        <% } else { %>
			            <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%= i %>&journalPageNum=<%=journalPageNum%>" class="page-btn"><%= i %></a>
			        <% } %>
			    <% } %>
				
			    <%-- 다음 블록 버튼 --%>
			    <% if(categoryEndPage < categoryPageCount) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%= categoryEndPage + 1 %>&journalPageNum=<%=journalPageNum%>" class="page-btn">다음</a>
			    <% } %>

			    <%-- 마지막 페이지 --%>
			    <% if(categoryCurrentPage < categoryPageCount) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%= categoryPageCount %>&journalPageNum=<%=journalPageNum%>" class="page-btn">끝</a>
			    <% } %>
			</div>
			<!-- 카테고리관리 페이지 번호  END  -->

        </div>
    </div>

    <!-- 등록 요청 관리 -->
    <div class="section">
        <div class="section-header" onclick="toggleSection(this)">
            <span class="arrow">▼</span> 기자등록 요청 관리
        </div>
        <div class="section-content">
            <div class="search-box">
                <input type="text" id="requestSearch" placeholder="요청자 이름" oninput="filterRequests()">
            </div>
            
            <form method="post" action="${pageContext.request.contextPath}/jourReasonCdUpdate">
	            <table>
	                <thead>
	                    <tr>
	                        <th><input type="checkbox" onclick="toggleAll(this,'request')"></th>
	                        <th>요청자</th>
	                        <th>요청자명</th>
	                        <th>닉네임</th>
	                        <th>현재상태</th>
	                        <th>신청상태</th>
	                        <th>신청내용</th>
	                        <th>등록일자</th>
	                        <th>처리내용</th>
	                        <th>변경상태</th>
	                    </tr>
	                </thead>
	                <tbody id="requestTable">
                
<%-- journalRegReqList : List<MemberBasDTO> --%>
<%
	   				List<MemberBasDTO> jourRegReqList = (List<MemberBasDTO>) request.getAttribute("journalRegReqList");
	
				    if(jourRegReqList != null && !jourRegReqList.isEmpty()) {
				        for(MemberBasDTO m : jourRegReqList) {
%>
					    <tr>
					        <td>
<%-- 				        	<input type="checkbox" class="request-check" value="<%= m.getJournalReasonNm() %>"> --%>
						        <input type="checkbox"
							           class="request-check"
							           name="journalMemberId"
							           value="<%= m.getMemberId() %>"
							           onchange="toggleRequestSelect(this)">
						    </td>
					        <td><%= m.getMemberId() %></td>
					        <td><%= m.getMemberNm() %></td>
					        <td><%= m.getNickNm() %></td>
					        <td><%= m.getJournalReasonCd() %></td>
					        <td class="request-status status-대기"><%= m.getJournalReasonNm() %></td>
					        <td><%= m.getJournalReasonDesc() %></td>
					        <td><%= m.getRegDt().toString().substring(0, 16) %></td>
					        <td>
					        	<input name="procReason" class="proc-text" value="<%= m.getProcReason() %>" disabled>
					        </td>
							<!-- 체크 시 활성화될 select -->
							<td>
<%-- 				                <select name="journalReasonCd_<%= m.getJournalReasonCd() %>"  --%>
					                <select name="journalReasonCd" 
					                		class="request-select"
					                		disabled>
					                    <option value="0" <%= "0".equals(m.getJournalReasonCd()) ? "selected" : "" %>>미신청</option>
					                    <option value="1" <%= "1".equals(m.getJournalReasonCd()) ? "selected" : "" %>>신청</option>
					                    <option value="2" <%= "2".equals(m.getJournalReasonCd()) ? "selected" : "" %>>승인</option>
					                    <option value="3" <%= "3".equals(m.getJournalReasonCd()) ? "selected" : "" %>>반려</option>
					                    <option value="4" <%= "4".equals(m.getJournalReasonCd()) ? "selected" : "" %>>보류</option>
					                </select>
					        </td>
	
					    </tr>
<%
				        }
				    } else {
%>
					    <tr>
					        <td colspan="5">기자등록 요청 데이터가 없습니다.</td>
					    </tr>
<%
	    			}
%>
	                </tbody>
	            </table>
            
            	<%-- 처리 결과 출력 --%>
<%
			    // request 객체에서 message 가져오기
			    String messageJournal = (String) session.getAttribute("messageJournal");
				System.out.println("======== messageJournal ======== : [" + messageJournal + "]");
	
				// not empty 조건 체크
			    if (messageJournal != null && !messageJournal.isEmpty()) {
%>
			        <p class="result-msg"><strong>결과: </strong><%= messageJournal %></p>
<%
	 				session.removeAttribute("messageJournal");
	    		}
%>
	            <!-- 권한 변경 버튼 -->
	            <div class="request-buttons">
	            	<button class="btn btn-approve" type="submit">권한 변경</button>
	            </div>

            </form>

<%-- 10단위 블록 페이지네이션 계산 --%>            
			<!-- 카테고리관리 페이지 번호 Start -->
			<div class="pagination">
			    <%-- 처음 페이지 --%>
			    <% if(journalCurrentPage > 1) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=1" class="page-btn">처음</a>
			    <% } %>

			    <%-- 이전 블록 버튼 --%>
			    <% if(journalStartPage > 1) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%= journalStartPage - 1 %>" class="page-btn">이전</a>
			    <% } %>
			
				<%-- 페이지 번호 (10개 단위) --%>
			    <% for(int i = journalStartPage; i <= journalEndPage; i++) { %>
			        <% if(i == journalCurrentPage) { %>
			            <span class="page-btn active"><%= i %></span>
			        <% } else { %>
			            <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%= i %>" class="page-btn"><%= i %></a>
			        <% } %>
			    <% } %>
				
			    <%-- 다음 블록 버튼 --%>
			    <% if(journalEndPage < journalPageCount) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%= journalEndPage + 1 %>" class="page-btn">다음</a>
			    <% } %>

			    <%-- 마지막 페이지 --%>
			    <% if(journalCurrentPage < journalPageCount) { %>
			        <a href="?memberPageNum=<%=memberPageNum%>&categoryPageNum=<%=categoryPageNum%>&journalPageNum=<%= journalPageCount %>" class="page-btn">끝</a>
			    <% } %>
			</div>
			<!-- 카테고리관리 페이지 번호  END  -->

        </div>
    </div>

</div>


</body>
</html>
