<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="dao.ArticleDAO" %>
<%@ page import="dao.CommentDAO" %>
<%@ page import="dao.CategoryDAO" %>
<%@ page import="dao.ArticleDivDAO" %>
<%@ page import="dao.ArticleListDAO" %>
<%@ page import="dao.ArticleDtlDAO" %>
<%@ page import="dao.MypageDAO" %>

<%@ page import="dto.ArticleDTO" %>
<%@ page import="dto.ArticleDtlDTO" %>
<%@ page import="dto.CommentDTO" %>

<%@ page import="java.io.File" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="javax.servlet.jsp.JspWriter" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.text.SimpleDateFormat"%>

<%
    String memberId = (String)session.getAttribute("memberId");
    int articleNo = Integer.parseInt(request.getParameter("articleNo"));
    
    ArticleDAO dao = new ArticleDAO();
    ArticleDivDAO addao = new ArticleDivDAO();
    ArticleListDAO aldao = new ArticleListDAO();
    ArticleDtlDAO dtdao = new ArticleDtlDAO();  
    CategoryDAO cgdao = new CategoryDAO();
    CommentDAO cdao = new CommentDAO();
    MypageDAO mdao = MypageDAO.getInstance();       
    ArticleDTO dto = dao.getArticle(articleNo);
    ArticleDtlDTO dtl = dtdao.getArticleDtl(articleNo);
    
    String logout = "/member/logout";    
    
    String memberStatCd = null;
    if(memberId != null){
        memberStatCd = mdao.getMemberStatCd(memberId);
    }
            
    // 현재 세션에서 본 게시글 번호 목록 가져오기
    Set<Integer> viewedArticles = (Set<Integer>)session.getAttribute("viewedArticles");
    
    // 처음 보는 경우 Set 객체 생성 후 세션에 저장
    if(viewedArticles == null){
        viewedArticles = new HashSet<>();
        session.setAttribute("viewedArticles", viewedArticles);
    }
    
    // 아직 조회하지 않은 글일 경우에만 조회수 증가 
    if(!viewedArticles.contains(articleNo)){
        dao.increaseCnt(articleNo);
        viewedArticles.add(articleNo);
    }	

    String uploadPath = application.getRealPath("/resources/upload");
    String pageNum = request.getParameter("pageNum");
    if(pageNum == null || pageNum.equals("")) pageNum = "1";
    
    String relatedPageNumStr = request.getParameter("relatedPageNum");
    int relatedPage = (relatedPageNumStr == null) ? 1 : Integer.parseInt(relatedPageNumStr);

    int pageSize = 10;
    int pageBlock = 10;

    int startRow = (relatedPage - 1) * pageSize + 1;
    int endRow = relatedPage * pageSize;
    
 	// 공지글 목록 조회
    List<ArticleDTO> noticeList = aldao.getNoticeList();
    
    int relatedTotalCount = aldao.getTotalArticleCount();
    List<ArticleDTO> relatedList = aldao.getRecentArticlesPaging(startRow, endRow);

    int pageCount = (int)Math.ceil((double)relatedTotalCount / pageSize);
    int startPage = ((relatedPage - 1) / pageBlock) * pageBlock + 1;
    int endPage = startPage + pageBlock - 1;
    if(endPage > pageCount) endPage = pageCount;
%>

<script>

// 현재 게시글 URL을 클립보드로 복사 
function copyUrl() {
    const url = "<%= request.getRequestURL() %>?articleNo=<%= articleNo %>";
    const tempInput = document.createElement("textarea");
    tempInput.value = url;
    document.body.appendChild(tempInput);
    tempInput.select();
    document.execCommand("copy");
    document.body.removeChild(tempInput);
    alert("URL이 복사되었습니다!");
}

// 댓글 수정 또는 답글 작성 폼을 동적으로 보여주는 함수 
function showReplyForm(commentId, mode, content){
    let container = document.getElementById('replyBox_' + commentId);
    
    // 이미 열려있으면 닫기 
    if(container.dataset.open === "true"){
        container.innerHTML = "";
        container.dataset.open = "false";
        return;
    }
    container.dataset.open = "true";
    let isEdit = (mode === 'edit');
    
    // 수정인지 답글인지에 따라 폼 내용 구성
    let formHtml =
        '<form method="post" action="commentPro.jsp">' +
        '<input type="hidden" name="articleNo" value="<%= articleNo %>">' +
        '<input type="hidden" name="memberId" value="<%= memberId %>">' +
        (isEdit
            ? '<input type="hidden" name="comment_id" value="' + commentId + '">'
            : '<input type="hidden" name="up_comment_id" value="' + commentId + '">') +
        '<input type="hidden" name="mode" value="' + mode + '">' +
        '<textarea name="comment_content" rows="2" cols="50">' + (content ? content : '') + '</textarea><br>' +
        '<input type="submit" value="' + (isEdit ? '수정 완료' : '답글 작성') + '">' +
        '</form>';
    container.innerHTML = formHtml;
}

// 게시글 삭제 확인
function confirmDelete(articleNo) {
    if(confirm("정말 삭제하시겠습니까?")) {
        location.href = "deletePro.jsp?articleNo=" + articleNo;
    }
}

// 댓글 삭제 확인
function commentDelete(comment_id, articleNo){
    if(confirm("댓글을 삭제하시겠습니까?")){
        location.href =
            "commentDeletePro.jsp?comment_id=" + comment_id
            + "&articleNo=" + articleNo;
    }
}

// 게시글 좋아요 처리
function articleLike(){
    location.href = "articleLikePro.jsp?articleNo=<%= articleNo %>";
}

// 게시글 북마크 처리 
function articleBookmark(){
    location.href = "articleBookmarkPro.jsp?articleNo=<%= articleNo %>";
}
</script>

<%!
// 댓글을 계층 구조로 출력하는 재귀 함수 
void printComments(List<CommentDTO> list, String parentId, int level, String memberId, JspWriter out, ArticleDAO dao) throws IOException {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	 
    for(CommentDTO c : list){

        boolean isRoot = (parentId == null && (c.getUpCommentId() == null || c.getUpCommentId().equals("")));
        boolean isChild = (parentId != null && parentId.equals(c.getUpCommentId()));

        if(isRoot || isChild){

            if(level == 0){
                out.println("<div style='border:1px solid #ccc; margin:10px 0; padding:8px;'>");
            }

            if(level > 0){
                out.println("<div style='display:flex;'>");
                out.println("<div style='width:" + (level * 3) + "px;'></div>");
                out.println("<div>↳</div>");
                out.println("<div style='margin-left:6px;'>");
            }
			
            String regDtStr = (c.getRegDt() != null) ? sdf.format(c.getRegDt()) : "";
            
            out.println("<b>" + c.getNickNm() + "</b> <small>(" + regDtStr + ")</small><br>");
            out.println(c.getCommentContent() + "<br>");

            CommentDAO cdao = new CommentDAO();
            int goodCount = cdao.getCommentGoodCount(c.getCommentId());

            boolean liked = false;
            if(memberId != null){
                liked = cdao.isCommentLiked(c.getCommentId(), memberId);
            }

            out.println("<button onclick=\"location.href='commentLikePro.jsp?comment_id="
                        + c.getCommentId() + "&articleNo=" + c.getArticleNo() + "'\" "
                        + "style='color:" + (liked ? "red" : "black") + "'>"
                        + "좋아요 (" + goodCount + ")</button>");
            
            boolean isAdmin = false;

            if(memberId != null){
            	MypageDAO mdao = MypageDAO.getInstance();
                String stat = mdao.getMemberStatCd(memberId);
                isAdmin = "3".equals(stat);
            }
            
            if(memberId != null && (memberId.equals(c.getMemberId()) || isAdmin)){
                out.println("<button onclick=\"showReplyForm('"
                            + c.getCommentId() + "', 'edit', `"
                            + c.getCommentContent().replace("`","\\`").replace("\n","\\n")
                            + "`)\">수정</button>");
                out.println("<button onclick=\"commentDelete('"
                            + c.getCommentId() + "', " + c.getArticleNo() + ")\">삭제</button>");
            }

            if(memberId != null){
                out.println("<button onclick=\"showReplyForm('"
                            + c.getCommentId() + "', 'reply')\">답글</button>");
            }

            out.println("<div id='replyBox_" + c.getCommentId() + "'></div>");

            printComments(list, c.getCommentId(), level + 1, memberId, out, dao);

            if(level > 0){
                out.println("</div></div>");
            }
            if(level == 0){
                out.println("</div>");
            }
        }
    }
}
%>

<!DOCTYPE html>
<html>
<head>
    <title>기사 상세</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/resources/css/articleView.css">
</head>

<!-- 상단바 -->
<div class="header-bar">
    <!-- 왼쪽: 로고 -->
    <a href="<%=request.getContextPath()%>/main" class="logo">
        TRAVEL+
    </a>
    
    <!-- 오른쪽: 메뉴/버튼 영역 -->
    <div class="menu">
    <% if(memberId == null) { %>
        <input type="button" value="로그인"
               onclick="location.href='<%=request.getContextPath()%>/member/login/loginForm2.jsp'">
        <input type="button" value="회원가입"
               onclick="location.href='<%=request.getContextPath()%>/member/join/joinForm2.jsp'">
    <% } else { %>
        <input type="button" value="마이페이지"
               class="mypage-btn"
               onclick="location.href='<%=request.getContextPath()%>/member/mypage/mypageForm2.jsp'">
        <input type="button" value="로그아웃"
               class="logout-btn"
               onclick="location.href='${pageContext.request.contextPath}<%= logout%>'">
    <% } %> 
</div>
</div>


<!-- 상단바 때문에 콘텐츠 밀림 방지용 여백 -->
<div style="height:50px;"></div>

<div class="main-wrap">
<h1>제목: <%= dto.getSubject() %></h1>
<p>카테고리: <%= cgdao.getCategoryName(dto.getCategoryId()) %></p>
<p>작성자: <%= dto.getNickNm() %></p>
<% if(dto.getHashTag() != null && !dto.getHashTag().trim().equals("")) { %>
    <p>태그: <%= dto.getHashTag() %></p>
<% } %>

<hr>
<%
    String[] imgs = { dtl.getArticleImg1(), dtl.getArticleImg2(), dtl.getArticleImg3() };
    for(String img : imgs){
        if(img != null){
            File f = new File(uploadPath + "/" + img);
            if(f.exists()){
%>
<img src="<%= request.getContextPath() %>/resources/upload/<%= img %>" width="300">
<%
            }
        }
    }
%>
<br />
<div><%= dtl.getArticleContent().replaceAll("\r\n|\n", "<br>") %> </div>

<% if(dtl.getLinkUrl() != null && !dtl.getLinkUrl().equals("")) { %>
<a href="<%= dtl.getLinkUrl() %>" target="_blank"><%= dtl.getLinkText() %></a>
<% } %>

<hr>

<%
	// 좋아요/북마크 여부 체크
    boolean liked = false;
    if(memberId != null){
        liked = addao.isArticleLiked(articleNo, memberId);
    }

    boolean bookmarked = false;
    if(memberId != null){
        bookmarked = addao.isArticleBookmarked(articleNo, memberId);
    }
%>

<div style="margin-top:15px; display:flex; justify-content:space-between; align-items:center;">

    <!-- 왼쪽 : 수정 / 삭제 -->
    <div>
    <%
        if(memberId != null && (memberId.equals(dto.getMemberId()) || "3".equals(memberStatCd))){
    %>
        <input type="button"
               value="수정"
               onclick="location.href='/mini_project/articleboard/editor/writeForm.jsp?articleNo=<%= dto.getArticleNo() %>&loaded=1'" />

        <input type="button"
               value="삭제"
               onclick="confirmDelete(<%= dto.getArticleNo() %>)" />
    <%
        }
    %>
    </div>

    <!-- 오른쪽 : 좋아요 / 북마크 / 공유 -->
    <div>
        <button onclick="articleLike()"
                style="color:<%= liked ? "red" : "black" %>">
            좋아요 (<%= dto.getGood() %>)
        </button>

        <button onclick="articleBookmark()"
                style="color:<%= bookmarked ? "blue" : "black" %>">
            북마크
        </button>

        <input type="button"
               value="공유하기"
               onclick="copyUrl()" />
    </div>

</div>
<hr>

<h3>댓글 작성</h3>
<%
    if(memberId == null){
%>
<p>댓글을 작성하려면 로그인하세요.</p>
<%
    } else {
%>
<form method="post" action="commentPro.jsp">
    <input type="hidden" name="articleNo" value="<%= articleNo %>">
    <input type="hidden" name="memberId" value="<%= memberId %>">
    <input type="hidden" name="up_comment_id" value="">
    <textarea name="comment_content" rows="3" cols="50" placeholder="댓글을 작성하세요"></textarea><br>
    <input type="submit" value="댓글 작성">
</form>
<% } %>

<hr>
<%
	// 댓글 목록 조회
    List<CommentDTO> commentList = cdao.getCommentList(articleNo);
%>
<h3>댓글 목록 (<%= commentList == null ? 0 : commentList.size() %>)</h3>
<%
    if(commentList == null || commentList.isEmpty()){
%>
<p>등록된 댓글이 없습니다.</p>
<%
    } else {
    	// 재귀 함수 호출하여 댓글 출력 
        printComments(commentList, null, 0, memberId, out, dao);
    }
%>

<hr>
<div style="display:flex; justify-content:space-between; align-items:center;">
    <h3 style="margin:0;">게시판 목록</h3>

    <input type="button"
           value="게시판"
           onclick="location.href='<%=request.getContextPath()%>/articleboard/board/boardList.jsp?pageNum=<%= pageNum %>'">
</div>

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
if(noticeList != null){
    for(ArticleDTO n : noticeList){
        ArticleDtlDTO nd = dtdao.getArticleDtl(n.getArticleNo());
        String thumb = (nd != null) ? nd.getArticleImg1() : null;
%>
<tr style="background:#fff7e6; font-weight:bold; cursor:pointer;"
    onclick="location.href='articleView.jsp?articleNo=<%= n.getArticleNo() %>&pageNum=<%= pageNum %>'">
    
    <td><%= n.getArticleNo() %></td>

    <td align="center">
        <img src="<%= (thumb == null || thumb.equals("")) 
            ? request.getContextPath() + "/resources/image/noimage.png"
            : request.getContextPath() + "/resources/upload/" + thumb %>" 
            width="80">
    </td>
    
    <td><%= n.getCategoryNm() %></td>
    <td><%= n.getSubject() %></td>
    <td><%= n.getNickNm() %></td>
    <td><%= n.getGood() %></td>
    <td><%= n.getCnt() %></td>
    <td><%= n.getRegDt().toString().substring(0, 16) %></td>
</tr>

<%
    }
}
%>

<!-- 일반 게시글 -->
<%
if(relatedList == null || relatedList.isEmpty()){
%>
<tr>
    <td colspan="8" align="center">표시할 기사가 없습니다.</td>
</tr>
<%
} else {
    for(ArticleDTO r : relatedList){

        // ✅ 공지와 동일하게 상세테이블에서 이미지 가져오기
        ArticleDtlDTO rd = dtdao.getArticleDtl(r.getArticleNo());
        String thumb = (rd != null) ? rd.getArticleImg1() : null;
%>

<tr style="cursor:pointer;"
    onclick="location.href='articleView.jsp?articleNo=<%= r.getArticleNo() %>&pageNum=<%= pageNum %>'">

    <td><%= r.getArticleNo() %></td>

    <td align="center">
        <img src="<%= (thumb == null || thumb.equals("")) 
            ? request.getContextPath() + "/resources/image/noimage.png"
            : request.getContextPath() + "/resources/upload/" + thumb %>" 
            width="80">
    </td>

    <td><%= r.getCategoryNm() %></td>
    <td><%= r.getSubject() %></td>
    <td><%= r.getNickNm() %></td>
    <td><%= r.getGood() %></td>
    <td><%= r.getCnt() %></td>
    <td><%= r.getRegDt().toString().substring(0, 16) %></td>
</tr>

<%
    }
}
%>

</table>


<div class="paging">
<%
    if(startPage > 1){
%>
<a href="articleView.jsp?articleNo=<%= articleNo %>&relatedPageNum=<%= startPage-1 %>">[이전]</a>
<%
    }

    for(int i=startPage;i<=endPage;i++){
        if(i == relatedPage){
%>
<b>[<%=i%>]</b>
<%
        } else {
%>
<a href="articleView.jsp?articleNo=<%= articleNo %>&relatedPageNum=<%=i%>">[<%=i%>]</a>
<%
        }
    }

    if(endPage < pageCount){
%>
<a href="articleView.jsp?articleNo=<%= articleNo %>&relatedPageNum=<%= endPage+1 %>">[다음]</a>
<%
    }
%>
</div>

</div>
</body>
</html>