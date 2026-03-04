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
    
 	// 현재 사용자가 해당 게시글에 이미 좋아요를 눌렀는지 확인
    // true  -> 이미 좋아요 상태
    // false -> 아직 좋아요 안 한 상태
    boolean isLiked = dao.isArticleLiked(articleNo, memberId);
    
    // 토글 방식으로 좋아요 처리 
    if(!isLiked){
    	// 아직 좋아요를 누르지 않은 상태라면 데이터 추가
        dao.insertArticleLike(articleNo, memberId);
    	// 해당 게시글의 좋아요 개수를 1 증가 
        dao.increaseArticleGood(articleNo);
    } else {
    	// 이미 좋아요를 누른 상태라면 데이터 삭제
        dao.deleteArticleLike(articleNo, memberId);
    	// 해당 게시글의 좋아요 개수를 1 감소 
        dao.decreaseArticleGood(articleNo);
    }

    response.sendRedirect("articleView.jsp?articleNo=" + articleNo);
%>
