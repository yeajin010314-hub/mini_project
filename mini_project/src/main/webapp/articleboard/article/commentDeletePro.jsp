<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.CommentDAO" %>
<%@ page import="dao.MypageDAO"%>

<%
    String memberId = (String)session.getAttribute("memberId");
	String comment_id = request.getParameter("comment_id");
	
	MypageDAO mdao = MypageDAO.getInstance();
	CommentDAO dao = new CommentDAO();
	
	String memberStatCd = mdao.getMemberStatCd(memberId);
	boolean isAdmin = "3".equals(memberStatCd);
    
    // 해당 댓글을 작성한 사용자 ID를 DB에서 조회
    String commentWriter = dao.getCommentWriter(comment_id);
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
    
    // 댓글 ID가 없거나 잘못 전달된 경우 처리 중단
    if(comment_id == null || comment_id.trim().equals("")){
%>
        <script>
        alert("잘못된 접근입니다.");
        history.go(-1);        
        </script>
<%  
        return;
    }

    // 삭제 권한 체크
    // 댓글 작성자가 존재하지 않거나 현재 로그인 사용자와 댓글 작성자가 다른 경우 삭제 권한 없음
    if(commentWriter == null || 
    	(!memberId.equals(commentWriter) && !isAdmin)){
%>
        <script>
        alert("삭제 할 권한이 없습니다.");
        history.go(-1);
        </script>
<%        
		return;
    }
    
    // 댓글 삭제 처리
    // 단일 댓글을 삭제하거나 대댓글까지 함께 삭제
    dao.deleteComment(comment_id); 
    
    out.println("<script>");
	out.println("alert('댓글이 삭제되었습니다.');");
	out.println("location.href='articleView.jsp?articleNo=" + articleNo + "';");
	out.println("</script>");
%>
