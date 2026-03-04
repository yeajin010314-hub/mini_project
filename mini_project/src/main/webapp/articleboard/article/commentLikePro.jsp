<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="dao.CommentDAO" %>
<%
    String memberId = (String)session.getAttribute("memberId");
	String comment_id = request.getParameter("comment_id");
	String articleNo = request.getParameter("articleNo");
	
	CommentDAO dao = new CommentDAO();
	
    if(memberId == null){
%>
		<script>
		alert("로그인 후 이용하세요.");
		history.go(-1);
		</script>
<%  
        return;
    }  
    if(comment_id == null || articleNo == null){
%>
	    <script>
		alert("잘못된 접근입니다.");
		history.go(-1);
		</script>   
<%  
        return;
    }
    // 현재 사용자가 해당 댓글에 이미 좋아요 눌렀는지 확인
    // true  -> 이미 좋아요 누른 상태
    // false -> 아직 좋아요 안 누른 상태
    boolean liked = dao.isCommentLiked(comment_id, memberId);
    
    // 좋아요 토글 처리
    if(liked){
    	// 이미 좋아요가 눌려있는 상태라면 (취소 처리)
    	// 댓글 좋아요 테이블에서 해당 데이터 삭제
        dao.deleteCommentLike(comment_id, memberId, Integer.parseInt(articleNo));
    	 // 댓글의 좋아요 개수 1 감소
        dao.updateCommentGood(comment_id, -1);
    } else {
    	// 아직 좋아요를 누르지 않은 상태라면 (추가 처리)
    	// 댓글 좋아요 테이블에 데이터 추가
        dao.insertCommentLike(comment_id, memberId, Integer.parseInt(articleNo));
    	// 댓글의 좋아요 개수 1 증가 
        dao.updateCommentGood(comment_id, 1);
    }

    // 글 상세 페이지로 이동
    response.sendRedirect("articleView.jsp?articleNo=" + articleNo);
%>
