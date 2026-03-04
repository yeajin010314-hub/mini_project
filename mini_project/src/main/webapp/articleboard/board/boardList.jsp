<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="dao.ArticleDAO" %>
<%@ page import="dto.ArticleDTO" %>
<%@ page import="dao.ArticleListDAO" %>
<%@ page import="dao.ArticleStatsDAO" %>
<%@ page import="dao.ArticleDivDAO" %>
<%@ page import="dao.MypageDAO" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.io.File" %>

<%
    ArticleDAO dao = new ArticleDAO();
	MypageDAO mdao = MypageDAO.getInstance();
    ArticleDivDAO addao = new ArticleDivDAO();
    ArticleListDAO aldao = new ArticleListDAO();
    ArticleStatsDAO asdao = new ArticleStatsDAO();
    
    String memberId = (String)session.getAttribute("memberId");
    String memberStatCd = null;
    if(memberId != null){
        memberStatCd = mdao.getMemberStatCd(memberId);
    }
    
    String logout = "/member/logout";
    
    // 정렬 조건 처리
    String sort = request.getParameter("sort");
    
    // 기본 정렬은 최신순
    if(sort == null || sort.equals("")){
        sort = "recent"; 
    }

    // 페이징  처리 설정 
        
    int pageSize  = 10; // 한 페이지에 보여줄 글 수
    int pageBlock = 10; // 한 번에 보여줄 페이지 번호 개수 

    String pageNumStr = request.getParameter("pageNum");
    // 현재 페이지 번호 (기본값 1)
    int currentPage = (pageNumStr == null) ? 1 : Integer.parseInt(pageNumStr);
    int startRow = (currentPage - 1) * pageSize + 1;
    int endRow   = currentPage * pageSize;
    
    // 검색 조건 처리
    String searchType = request.getParameter("searchType");
    String keyword = request.getParameter("keyword");
    
    // 데이터 조회 (일반 목록 / 북마크 목록 분기 )
    int totalCount = aldao.getArticleCount(keyword, searchType);
    List<ArticleDTO> list;
    
    // 공지 데이터 조회
    List<ArticleDTO> noticeList = aldao.getNoticeList(); 
    
    // myBookmark 파라미터가 1이면 -> 북마크한 글만 조회
    if("1".equals(request.getParameter("myBookmark")) && memberId != null){
        totalCount = addao.getBookmarkedArticleCount(memberId, keyword, searchType);
        list = addao.getBookmarkedArticles(memberId, startRow, endRow, keyword, searchType, sort);
    } else {
    	// 일반 게시글 목록 조회 
        list = aldao.getArticleList(startRow, endRow, keyword, searchType, sort);
    }
    
    // 페이징 계산
    int pageCount = (int)Math.ceil((double)totalCount / pageSize);
    int startPage = ((currentPage - 1) / pageBlock) * pageBlock + 1;
    int endPage   = startPage + pageBlock - 1;
    if(endPage > pageCount) endPage = pageCount;
    
    // 파라미터 유지 문자열 만들기
    String param = "";

    if(keyword != null && !keyword.equals("")) {
        param += "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
    }

    if(searchType != null && !searchType.equals("")) {
        param += "&searchType=" + searchType;
    }

    if(sort != null && !sort.equals("")) {
        param += "&sort=" + sort;
    }

    if("1".equals(request.getParameter("myBookmark"))) {
        param += "&myBookmark=1";
    }
    
 // TOP 게시글 조회 
    List<ArticleDTO> topViewsList = aldao.getArticleList(1, 1, null, null, "popular");       
    List<ArticleDTO> topGoodList  = aldao.getArticleList(1, 1, null, null, "recommended");   
    List<ArticleDTO> topBookmarkList = asdao.getTopByBookmark(1);                             
    List<ArticleDTO> topCommentList = asdao.getTopByComment(1);                               

    List<ArticleDTO> topList = new ArrayList<>();
    List<String> topLabelList = new ArrayList<>();

    if(topViewsList.size() > 0) {
        topList.add(topViewsList.get(0));
        topLabelList.add("조회수 TOP");
    }

    if(topGoodList.size() > 0) {
        topList.add(topGoodList.get(0));
        topLabelList.add("추천 TOP");
    }

    if(topBookmarkList.size() > 0) {
        topList.add(topBookmarkList.get(0));
        topLabelList.add("북마크 TOP");
    }

    if(topCommentList.size() > 0) {
        topList.add(topCommentList.get(0));
        topLabelList.add("댓글 TOP");
    }

    // 랜덤 게시글 3개 조회
    List<ArticleDTO> randomList = asdao.getRandomArticles(3);
%>

<head>
<link rel="stylesheet"href="<%=request.getContextPath()%>/resources/css/boardList.css">
<meta charset="UTF-8">
<title>TRAVEL+ | 게시판</title>
<meta name = "viewport" content="width=device-width, initial-scale=1.0"/>
</head>

<body>
<!-- 상단바 -->
<div style="width:100%; height:50px; background-color:#fff; display:flex; justify-content:space-between; align-items:center; padding:0 20px; box-sizing:border-box; position:fixed; top:0; left:0; z-index:1000; border-bottom:1px solid #ccc;">
    
    <!-- 왼쪽: 로고 -->
    <a href="<%=request.getContextPath()%>/main" 
       style="color:#000; font-size:20px; font-weight:bold; text-decoration:none;">
        TRAVEL+
    </a>
    
    <!-- 오른쪽: 메뉴/버튼 영역 -->
    <div>
        <% if(memberId == null) { %>
            <!-- 로그인 안 된 상태 -->
            <input type="button" value="로그인" 
                   onclick="location.href='<%=request.getContextPath()%>/member/login/loginForm2.jsp'">
            <input type="button" value="회원가입" 
                   onclick="location.href='<%=request.getContextPath()%>/member/join/joinForm2.jsp'">
        <% } else { %>
            <!-- 로그인 된 상태: 글쓰기 없음 -->
            <input type="button" value="마이페이지" 
                   onclick="location.href='<%=request.getContextPath()%>/member/mypage/mypageForm2.jsp'">
            <input type="button" value="로그아웃" 
                   onclick="location.href='${pageContext.request.contextPath}<%= logout%>'">
        <% } %>
    </div>
</div>

<!-- 상단바 때문에 콘텐츠 밀림 방지용 여백 -->
<div style="height:50px;"></div>

<!-- TOP 4 출력 영역 -->
<div style="max-width:1100px; margin:0 auto; padding:10px;">
<h3>TOP 게시글</h3>

<div style="
    display:grid; 
    grid-template-columns: repeat(4, 250px); 
    gap:20px;
    justify-content:center;
">

<%
    for(int i=0; i<topList.size(); i++){
        ArticleDTO dto = topList.get(i);
        String label = topLabelList.get(i);
        
     	// 썸네일 파일 존재 여부 확인
        String thumb = dto.getThumbnail();
        String imgPath;

        if(thumb == null || thumb.trim().equals("")) {
            imgPath = "/mini_project/resources/image/noimage.png";
        } else {
            String realPath = application.getRealPath("/resources/upload/" + thumb);
            File file = new File(realPath);

            if(file.exists()) {
                imgPath = "/mini_project/resources/upload/" + thumb;
            } else {
                imgPath = "/mini_project/resources/image/noimage.png";
            }
        }
%>
    <div style="border:1px solid #ccc; width:250px; border-radius:10px; box-shadow:0 2px 5px rgba(0,0,0,0.1); overflow:hidden;">
        <a href="/mini_project/articleboard/article/articleView.jsp?articleNo=<%= dto.getArticleNo() %>&pageNum=<%= currentPage %>">
            <div style="height:150px; text-align:center; background:#f8f8f8; position:relative;">
                <img src="<%= imgPath %>" style="max-width:100%; max-height:100%;">
                <div style="position:absolute; top:5px; left:5px; background:#ff6b6b; color:#fff; font-size:12px; padding:2px 5px; border-radius:5px;">
                    <%= label %>
                </div>
            </div>

            <div style="padding:10px;">
                <h3 style="font-size:16px; margin:5px 0;"><%= dto.getSubject() %></h3>
                <p style="font-size:12px; color:#666;">작성자: <%= dto.getNickNm() %></p>
                <p style="font-size:12px; color:#666;">카테고리: <%= dto.getCategoryNm() %></p>
                <p style="font-size:12px; color:#666;">추천: <%= dto.getGood() %> | 조회: <%= dto.getCnt() %></p>
                <p style="font-size:12px; color:#666;">등록일: <%= dto.getRegDt().toString().substring(0, 16) %></p>
            </div>
        </a>
    </div>

<%
    }
%>

</div>


<!-- 랜덤 3개 출력 영역 -->
<h3 style="margin-top:30px;">랜덤 게시글</h3>

<div style="
    display:grid; 
    grid-template-columns: repeat(3, 250px); 
    gap:20px;
    justify-content:center;
">

<%
    for(int i=0; i<randomList.size(); i++){
        ArticleDTO dto = randomList.get(i);
        
        String thumb = dto.getThumbnail();
        String imgPath;

        if(thumb == null || thumb.trim().equals("")) {
            imgPath = "/mini_project/resources/image/noimage.png";
        } else {
            String realPath = application.getRealPath("/resources/upload/" + thumb);
            File file = new File(realPath);

            if(file.exists()) {
                imgPath = "/mini_project/resources/upload/" + thumb;
            } else {
                imgPath = "/mini_project/resources/image/noimage.png";
            }
        }
%>

    <div style="border:1px solid #ccc; width:250px; border-radius:10px; box-shadow:0 2px 5px rgba(0,0,0,0.1); overflow:hidden;">
        <a href="/mini_project/articleboard/article/articleView.jsp?articleNo=<%= dto.getArticleNo() %>&pageNum=<%= currentPage %>">
            <div style="height:150px; text-align:center; background:#f8f8f8; position:relative;">
                <img src="<%= imgPath %>" style="max-width:100%; max-height:100%;">
                <div style="position:absolute; top:5px; left:5px; background:#4dabf7; color:#fff; font-size:12px; padding:2px 5px; border-radius:5px;">
                    랜덤
                </div>
            </div>

            <div style="padding:10px;">
                <h3 style="font-size:16px; margin:5px 0;"><%= dto.getSubject() %></h3>
                <p style="font-size:12px; color:#666;">작성자: <%= dto.getNickNm() %></p>
                <p style="font-size:12px; color:#666;">카테고리: <%= dto.getCategoryNm() %></p>
                <p style="font-size:12px; color:#666;">추천: <%= dto.getGood() %> | 조회: <%= dto.getCnt() %></p>
                <p style="font-size:12px; color:#666;">등록일: <%= dto.getRegDt().toString().substring(0, 16) %></p>
            </div>
        </a>
    </div>

<%
    }
%>

</div>

<!-- 정렬 버튼 영역 -->
<div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
    
    <!-- 왼쪽: 정렬 버튼 -->
    <div>
        <input type="button" value="최신순"
            onclick="location.href='boardList.jsp?sort=recent<%= param.replace("&sort=" + sort, "") %>'">
        <input type="button" value="인기순"
            onclick="location.href='boardList.jsp?sort=popular<%= param.replace("&sort=" + sort, "") %>'">
        <input type="button" value="추천순"
            onclick="location.href='boardList.jsp?sort=recommended<%= param.replace("&sort=" + sort, "") %>'">
    </div>

    <!-- 오른쪽: 글쓰기 버튼 -->
    <div>
        <% if("2".equals(memberStatCd) || "3".equals(memberStatCd)) { %>
            <input type="button" value="글쓰기"
                   onclick="location.href='/mini_project/articleboard/editor/writeForm.jsp'">
        <% } %>
    </div>
    
</div>

<!-- 일반 목록 테이블 -->

<table border="1" width="100%" style="text-align:center;">
<tr>
    <th>번호</th>
    <th>썸네일</th>
    <th>카테고리</th>
    <th>제목</th>
    <th>작성자</th>
    <th>추천수</th>
    <th>조회수</th>
    <th>등록일</th>
</tr>

<!-- 공지 게시글 -->
<%
if(noticeList != null && noticeList.size() > 0){
    for(ArticleDTO dto : noticeList){
    	String thumb = dto.getThumbnail();
        String imgPath;

        if(thumb == null || thumb.trim().equals("")) {
            imgPath = "/mini_project/resources/image/noimage.png";
        } else {
            String realPath = application.getRealPath("/resources/upload/" + thumb);
            File file = new File(realPath);

            if(file.exists()) {
                imgPath = "/mini_project/resources/upload/" + thumb;
            } else {
                imgPath = "/mini_project/resources/image/noimage.png";
            }
        }
%>
<tr style="background-color:#fff8e1; font-weight:bold; cursor:pointer;"
    onclick="location.href='<%=request.getContextPath()%>/articleboard/article/articleView.jsp?articleNo=<%= dto.getArticleNo() %>&pageNum=<%= currentPage %>'">
    
    <td><%= dto.getArticleNo() %></td>
    <td align="center"><img src="<%= imgPath %>" width="80"></td>
    <td><%= dto.getCategoryNm() %></td>
    <td><%= dto.getSubject() %></td>
    <td><%= dto.getNickNm() %></td>
    <td><%= dto.getGood() %></td>
    <td><%= dto.getCnt() %></td>
    <td><%= dto.getRegDt().toString().substring(0, 16) %></td>
</tr>

<%
    }
}
%>

<!-- 일반 게시글 -->
<%
    if(list == null || list.size() == 0){
%>
<tr>
    <td colspan="8" align="center">등록된 글이 없습니다.</td>
</tr>
<%
    } else {
        for(ArticleDTO dto : list){
        	String thumb = dto.getThumbnail();
            String imgPath;

            if(thumb == null || thumb.trim().equals("")) {
                imgPath = "/mini_project/resources/image/noimage.png";
            } else {
                String realPath = application.getRealPath("/resources/upload/" + thumb);
                File file = new File(realPath);

                if(file.exists()) {
                    imgPath = "/mini_project/resources/upload/" + thumb;
                } else {
                    imgPath = "/mini_project/resources/image/noimage.png";
                }
            }
%>
<tr style="cursor:pointer;"
    onclick="location.href='<%=request.getContextPath()%>/articleboard/article/articleView.jsp?articleNo=<%= dto.getArticleNo() %>&pageNum=<%= currentPage %>'">
    
    <td><%= dto.getArticleNo() %></td>
    <td align="center"><img src="<%= imgPath %>" width="80" height="60"></td>
    <td><%= dto.getCategoryNm() %></td>
    <td><%= dto.getSubject() %></td>
    <td onclick="event.stopPropagation();">
        <a href="boardList.jsp?keyword=<%= dto.getNickNm() %>&searchType=nickNm">
            <%= dto.getNickNm() %>
        </a>
    </td>
    <td><%= dto.getGood() %></td>
    <td><%= dto.getCnt() %></td>
    <td><%= dto.getRegDt().toString().substring(0, 16) %></td>
</tr>

<%
        }
    }
%>
</table>

<!-- 검색 폼 영역 -->
<form method="get" action="boardList.jsp">
    <select name="searchType">
        <option value="" <%= (request.getParameter("searchType") == null || request.getParameter("searchType").equals("")) ? "selected" : "" %>>-- 검색조건 --</option>
        <option value="subject" <%= "subject".equals(request.getParameter("searchType")) ? "selected" : "" %>>제목</option>
        <option value="article_content" <%= "article_content".equals(request.getParameter("searchType")) ? "selected" : "" %>>내용</option>
        <option value="nickNm" <%= "nickNm".equals(request.getParameter("searchType")) ? "selected" : "" %>>작성자</option>
        <option value="category" <%= "category".equals(request.getParameter("searchType")) ? "selected" : "" %>>카테고리</option>
    </select>

    <input type="text" name="keyword"
           placeholder="검색어 입력"
           value="<%= request.getParameter("keyword") != null ? request.getParameter("keyword") : "" %>">

    <input type="submit" value="검색">
    
    
    <input type="button" value="전체목록"
        onclick="location.href='boardList.jsp'"
        style="float:right; margin-left:10px;">    
<%
    if(memberId != null){
%>
    <input type="button" value="내 북마크"
        onclick="location.href='boardList.jsp?myBookmark=1'"
        style="float:right; margin-left:10px;">
<%
    }
%>
</form>


<!-- 페이징 -->
<div style="width:100%; text-align:center; margin:15px 0;">
<%
    if(startPage > 1){
%>
    <a href="boardList.jsp?pageNum=<%= startPage-1 %><%= param %>" 
       style="display:inline-block; min-width:30px; padding:4px 8px; margin:0 4px; border:1px solid #ccc; border-radius:4px; text-decoration:none; color:#333;">[이전]</a>
<%
    }

    for(int i=startPage;i<=endPage;i++){
        if(i==currentPage){
%>
    <b style="display:inline-block; min-width:30px; padding:4px 8px; margin:0 4px; border-radius:4px; background-color:#007bff; color:#fff; border:1px solid #007bff;">[<%=i%>]</b>
<%
        } else {
%>
    <a href="boardList.jsp?pageNum=<%=i%><%= param %>" 
       style="display:inline-block; min-width:30px; padding:4px 8px; margin:0 4px; border:1px solid #ccc; border-radius:4px; text-decoration:none; color:#333;">[<%=i%>]</a>
<%
        }
    }

    if(endPage < pageCount){
%>
    <a href="boardList.jsp?pageNum=<%= endPage+1 %><%= param %>" 
       style="display:inline-block; min-width:30px; padding:4px 8px; margin:0 4px; border:1px solid #ccc; border-radius:4px; text-decoration:none; color:#333;">[다음]</a>
<%
    }
%>
</div>

