<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="dao.ArticleDAO" %>

<%
    String memberId = (String)session.getAttribute("memberId");

    if(memberId == null){
%>
    <script>
        alert("로그인이 필요합니다.");
        history.back();
    </script>
<%
        return;
    }

    int articleNo = Integer.parseInt(request.getParameter("articleNo"));

    ArticleDAO dao = new ArticleDAO();

    // 본인 글인지 확인 후 삭제
    boolean result = dao.deleteTempArticle(articleNo, memberId);

    if(result){
%>
    <script>
        alert("삭제되었습니다.");
        location.href="tempList.jsp";
    </script>
<%
    } else {
%>
    <script>
        alert("삭제 실패 또는 권한이 없습니다.");
        history.back();
    </script>
<%
    }
%>
