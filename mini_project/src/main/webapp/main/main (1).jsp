<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dto.MemberBasDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.CategoryDTO" %>
<%@ page import="dto.ArticleDetailViewDTO" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>여행 & 맛집 추천</title>

    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css">
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
	System.out.println("============= main.jsp =============");
	// Session 가져오기
	MemberBasDTO user = (MemberBasDTO) session.getAttribute("loginUser");
	System.out.println("user : [" + user + "]");
	
	// memberId
	String memberId = null;
// 	String memberId = (String)session.getAttribute("memberId");
	memberId = (user != null) ? user.getMemberId() : null;
	System.out.println("memberId : [" + memberId + "]");

	
	// 카테고리 Tree 목록
	List<CategoryDTO> categoryTreeList = (List<CategoryDTO>) request.getAttribute("categoryTreeList");

	// Main 카드 Session 여행추천 1개
	ArticleDetailViewDTO articleDetailViewTravelView = (ArticleDetailViewDTO) request.getAttribute("articleDetailViewTravelView");
	System.out.println("=========== ssss ================");
	System.out.println( (articleDetailViewTravelView != null) ? articleDetailViewTravelView : null);
	System.out.println( (articleDetailViewTravelView.getArticle() != null) ? articleDetailViewTravelView.getArticle() : null);
	System.out.println("=========== sssskkkkkkkkkk ================");
	
	System.out.println("getArticleNo : [" + articleDetailViewTravelView.getArticle().getArticleNo() + "]");
	System.out.println("subject : [" + articleDetailViewTravelView.getArticle().getSubject() + "]");

	// Main 카드 Session 맛집추천 1개
	ArticleDetailViewDTO articleDetailViewFoodView = (ArticleDetailViewDTO) request.getAttribute("articleDetailViewFoodView");

	// URL
	String img = "/resources/image/ti122a19103_01.jpg";
	String imgPath = "/resources/upload/";
	String noImg = "/resources/image/이미지없음.png";
	
	// 경로
	String loginForm = "/member/login/loginForm2.jsp";			// 로그인
	String memberJoinForm = "/member/join/joinForm2.jsp";		// 회원가입
	String mypageForm = "/member/mypage/mypageForm2.jsp";		// 마이페이지
	String boardForm = "/articleboard/board/boardList.jsp";		// 게시판
	String adminDashboardForm = "/memberBasStats";				// 관리자 대시보드
	String articleListView = "/articleboard/article/articleView.jsp"; // 기사
// 	String logout = "/logout";
	String logout = "/member/logout";
	
%>


<header class="header">
    <div class="logo">TRAVEL+</div>

<!--     <div class="search-box"> -->
<!--         <input type="text" placeholder="검색어를 입력하세요"> -->
<!--     </div> -->

	<div class="menu">
<% 
 		if (user == null) {
//		if (memberId == null) {
%>
		    <a href="${pageContext.request.contextPath}<%= loginForm%>">로그인</a>
		    <a href="${pageContext.request.contextPath}<%= memberJoinForm%>">회원가입</a>
	        <a href="${pageContext.request.contextPath}<%= boardForm%>" id="board-btn">게시판</a>
<% 
		} else { 
%>
		    <h4><span><Strong style="color:blue;"><%= user.getMemberNm() %></Strong>님 환영합니다!</span></h4>
		    <a href="${pageContext.request.contextPath}<%= mypageForm%>" id="board-btn">마이페이지</a>
	        <a href="${pageContext.request.contextPath}<%= boardForm%>" id="board-btn">게시판</a>
<%
			// 관리자일때만 표시
			if ("3".equals(user.getMemberStatCd())) {
%>
				<a href="${pageContext.request.contextPath}<%= adminDashboardForm%>" >관리자 대시보드</a>	
<% 
			}
%>
	        
		    <a href="${pageContext.request.contextPath}<%= logout%>">로그아웃</a>
<% 
		} 
%>
	</div>
</header>

<div class="container">

    <!-- 메인 카드 2개 -->
    <section class="main-topics">
        <div class="topic-card">
            <div class="topic-img">
<%
				img = (    articleDetailViewTravelView != null 
						&& articleDetailViewTravelView.getArticleDtl() != null
						&& articleDetailViewTravelView.getArticleDtl().getArticleImg1() != null)
						?  imgPath + articleDetailViewTravelView.getArticleDtl().getArticleImg1()
						: noImg;
%>
<%
				String img1 = (    articleDetailViewTravelView != null 
								&& articleDetailViewTravelView.getArticleDtl() != null
								&& articleDetailViewTravelView.getArticleDtl().getArticleImg1() != null)
								? articleDetailViewTravelView.getArticleDtl().getArticleImg1()
								: noImg;
%>
                <a href="${pageContext.request.contextPath}<%= articleListView%>?articleNo=<%=articleDetailViewTravelView.getArticle().getArticleNo() %>">
                	<img src="${pageContext.request.contextPath}<%= img%>">
                </a> 
            </div>
            <div class="topic-content">
                <div class="topic-text">
                	<div class="title-group">
	                	<img class="icon-title" src="${pageContext.request.contextPath}/resources/image/제목.png">
                    	<h2><%=articleDetailViewTravelView.getArticle().getSubject() %></h2>  				<!-- ARTICLE.SUBJECT  -->
                    </div>
                    <div class="content-group">
	                    <img class="icon-content" src="${pageContext.request.contextPath}/resources/image/본문01.png">
                    	<p><%=articleDetailViewTravelView.getArticleDtl().getArticleContent() %></p>		<!-- ARTICLE_DTL.ARTICLE_CONTENT 자릿수 제약있음.  -->
                    </div>
<!--                     <h2>여행 추천</h2>  				ARTICLE.SUBJECT  -->
<!--                     <p>지금 가장 인기 있는 여행지</p>		ARTICLE_DTL.ARTICLE_CONTENT 자릿수 제약있음.  -->
                </div>
                <div class="topic-info">
                	<img class="icon-like" src="${pageContext.request.contextPath}/resources/image/좋아요.png">
                    <span><%=articleDetailViewTravelView.getArticle().getGood() %></span>			<!-- ARTICLE.GOOD  -->
                    <img class="icon-like" src="${pageContext.request.contextPath}/resources/image/기자.png">
                    <span><%=articleDetailViewTravelView.getArticle().getMemberNm() %></span>			<!-- ARTICLE.MEMBER_ID  -->
                    <img class="icon-like" src="${pageContext.request.contextPath}/resources/image/해시태그.png">
                    <span><%=articleDetailViewTravelView.getArticle().getHashTag() %></span>			<!-- ARTICLE.HASH_TAG  -->
<!--                     <span>지역 120+</span>			ARTICLE.GOOD  -->
<!--                     <span>TOP 여행지</span>			ARTICLE.MEMBER_ID  -->
<!--                     <span>TOP 여행지</span>			ARTICLE.HASH_TAG  -->
                </div>
            </div>
        </div>

        <div class="topic-card">
            <div class="topic-img">
<%
				img = (    articleDetailViewFoodView != null 
						&& articleDetailViewFoodView.getArticleDtl() != null
						&& articleDetailViewFoodView.getArticleDtl().getArticleImg1() != null)
						?  imgPath + articleDetailViewFoodView.getArticleDtl().getArticleImg1()
						: noImg;
				System.out.println("이미지 : [" + articleDetailViewFoodView.getArticleDtl().getArticleImg1() + "]");
%>

<%
				String img2 = (    articleDetailViewFoodView != null 
								&& articleDetailViewFoodView.getArticleDtl() != null
								&& articleDetailViewFoodView.getArticleDtl().getArticleImg1() != null)
								? articleDetailViewFoodView.getArticleDtl().getArticleImg1()
								: noImg;
%>
                <a href="${pageContext.request.contextPath}<%= articleListView%>?articleNo=<%=articleDetailViewFoodView.getArticle().getArticleNo() %>">
                	<img src="${pageContext.request.contextPath}<%= img%>">
                </a>
<%--                 <img src="${pageContext.request.contextPath}<%= img2%>"> --%>
            </div>
            <div class="topic-content">
                <div class="topic-text">
                	<div class="title-group">
	                	<img class="icon-title" src="${pageContext.request.contextPath}/resources/image/제목.png">
	                    <h2 class="title-01" ><%=articleDetailViewFoodView.getArticle().getSubject() %></h2>  				<!-- ARTICLE.SUBJECT  -->
                    </div>
                	<div class="content-group">
	                    <img class="icon-content" src="${pageContext.request.contextPath}/resources/image/본문01.png">
	                    <p><%=articleDetailViewFoodView.getArticleDtl().getArticleContent() %></p>		<!-- ARTICLE_DTL.ARTICLE_CONTENT 자릿수 제약있음.  -->
                    </div>

<!--                     <h2>맛집 추천</h2> -->
<!--                     <p>리뷰 기반 검증 맛집</p> -->
                </div>
                <div class="topic-info">
                	<img class="icon-like" src="${pageContext.request.contextPath}/resources/image/좋아요.png">
                    <span><%=articleDetailViewFoodView.getArticle().getGood() %></span>			<!-- ARTICLE.GOOD  -->
                    <img class="icon-like" src="${pageContext.request.contextPath}/resources/image/기자.png">
                    <span><%=articleDetailViewFoodView.getArticle().getMemberNm() %></span>			<!-- ARTICLE.MEMBER_ID  -->
                    <img class="icon-like" src="${pageContext.request.contextPath}/resources/image/해시태그.png">
                    <span><%=articleDetailViewFoodView.getArticle().getHashTag() %></span>			<!-- ARTICLE.HASH_TAG  -->
<!--                     <span>맛집 3,000+</span> -->
<!--                     <span>현지인 추천</span> -->
<!--                     <span>TOP 여행지</span>			ARTICLE.HASH_TAG  -->
                </div>
            </div>
        </div>
    </section>

    <!-- 카드 3개 -->
    <section class="card-section">
<%
	List<ArticleDetailViewDTO> articleSubView = (List<ArticleDetailViewDTO>) request.getAttribute("articleSubView");

	if (articleSubView != null) {
		for(ArticleDetailViewDTO articleView : articleSubView) {
%>
        <div class="card">
            <div class="card-img">
<%
				img = (    articleView != null 
						&& articleView.getArticle() != null
						&& articleView.getArticleDtl().getArticleImg1() != null)
						?  imgPath + articleView.getArticleDtl().getArticleImg1()
						: noImg;
%>
                <a href="${pageContext.request.contextPath}<%= articleListView%>?articleNo=<%=articleView.getArticle().getArticleNo() %>">
	                <img src="${pageContext.request.contextPath}<%= img%>">
	            </a>
            </div>
            <div class="card-content">
            	<div class="title-group">
	            	<img class="icon-title" src="${pageContext.request.contextPath}/resources/image/제목.png">
					<p><%=articleView.getArticle().getSubject() %></p>
<!-- 				<p>서울여행</p> -->
				</div>
            </div>
            <!--  <div class="card-content">서울 여행</div>-->
        </div>
<%
		}
	}
%>
    </section>

    <!-- 메가 카테고리 -->
    <section class="mega-category">
        <div class="mega-grid">

<!-- 여기에 카테고리 가져와서 처리해야함.  -->
<% 
		if (categoryTreeList != null) {
		    for (CategoryDTO parent : categoryTreeList) {
		    	
%>
			<div class="mega-section">
				<h3>
 	            		<a href="${pageContext.request.contextPath}/main?categoryId=<%= parent.getCategoryId() %>&rowLimit=4">
						<%=parent.getCategoryNm() %>
					</a>
                </h3>
                <hr>
<% 
			if (parent.getChild() != null) {
%>
				<ul>
<% 
			    for (CategoryDTO child : parent.getChild()) {
%>
                	<li>
	            		<a href="${pageContext.request.contextPath}/main?categoryId=<%= child.getCategoryId() %>&rowLimit=4">
	                		<%=child.getCategoryNm() %>
	                	</a>
                	</li>
<%
			    }
%>
                </ul>
<% 
			}
%>
            </div>
<% 
		    }
		}
%>
        </div>
    </section>

    <!-- 카테고리 하단 카드 4개 -->
    <section class="category-card-section">
<%
	List<ArticleDetailViewDTO> articleList = (List<ArticleDetailViewDTO>) request.getAttribute("articleView");
	if (articleList != null) {
		for(ArticleDetailViewDTO articleView : articleList) {
			System.out.println("articleNo : [" + articleView.getArticle().getArticleNo() + "]");
%>
        <div class="card">
            <div class="card-img">
<%
				img = (    articleView != null 
						&& articleView.getArticleDtl() != null
						&& articleView.getArticleDtl().getArticleImg1() != null)
						?  imgPath + articleView.getArticleDtl().getArticleImg1()
						: noImg;
%>
                <a href="${pageContext.request.contextPath}<%= articleListView%>?articleNo=<%=articleView.getArticle().getArticleNo() %>">
	                <img src="${pageContext.request.contextPath}<%= img%>">
	            </a>
            </div>
           	<div class="card-content">
            		<div>
                <h4>기사번호 : <%=articleView.getArticle().getArticleNo() %></h4>
                <p>제목 : <%=articleView.getArticle().getSubject() %></p>
                </div>
            </div>
        </div>
<%			
		}
	}
%>
    </section>

</div>

<!-- JS -->
<script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
</body>
</html>
