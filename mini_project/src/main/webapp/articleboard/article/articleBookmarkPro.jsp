<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dao.ArticleDivDAO" %>

<%
    String memberId = (String)session.getAttribute("memberId");
    int articleNo = Integer.parseInt(request.getParameter("articleNo"));

    if(memberId == null){
%>
	    <script>
		alert("로그인이 필요합니다.");
		history.go(-1);
		</script>
<%
		return;
	}

    ArticleDivDAO dao = new ArticleDivDAO();
    
    // 현재 게시글이 이미 북마크 되어 있는지 확인
    // true -> 이미 북마크 상태
    // false -> 아직 북마크 안된 상태 
    boolean bookmarked = dao.isArticleBookmarked(articleNo, memberId);
    
    // 토글 방식 처리
    // 북마크 안 되어 있으면 -> 추가
    // 이미 되어있으면 -> 삭제
    if(!bookmarked){
        dao.insertArticleBookmark(articleNo, memberId);
    } else {
        dao.deleteArticleBookmark(articleNo, memberId);
    }

    response.sendRedirect("articleView.jsp?articleNo=" + articleNo);
%>
