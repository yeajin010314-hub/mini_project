<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.CommentDAO" %>
<%
	String memberId = (String)session.getAttribute("memberId");
    String articleNoStr = request.getParameter("articleNo");
    String up_comment_id = request.getParameter("up_comment_id"); // 대댓글이면 값 존재
    String comment_content = request.getParameter("comment_content");
    int articleNo = Integer.parseInt(articleNoStr);
    CommentDAO dao = new CommentDAO();
    
    if(memberId == null || memberId.equals("") || 
    	articleNoStr == null || articleNoStr.equals("") || 
    	comment_content == null || comment_content.equals("")){
%>
       	<script>
		alert("댓글 작성에 필요한 정보가 없습니다.");
		history.go(-1);
		</script> 
<%
        return;
    }

    // 일반 댓글이면 up_comment_id는 null
    // 대댓글이면 up_comment_id에 부모 댓글 ID 전달
    boolean result = dao.insertComment(memberId, articleNo, up_comment_id, comment_content);
    
    if(result){
        out.println("<script>location.href='articleView.jsp?articleNo=" + articleNo + "';</script>");
    } else {
%>
		<script>
		alert("댓글 등록 실패");
		history.go(-1);
		</script>
<%
    }
%>
