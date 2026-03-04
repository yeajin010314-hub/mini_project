<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="dao.ArticleDAO"%>
<%@ page import="dto.ArticleDTO"%>

<%
    String memberId = (String)session.getAttribute("memberId");

    if(memberId == null){
%>
        <script>
        alert("로그인이 필요합니다.");
        history.go(-1);
        </script>
<%
        return;
    }

    ArticleDAO dao = new ArticleDAO();
    List<ArticleDTO> list = dao.getTempList(memberId);
%>  

<!DOCTYPE html>
<html>
<head>
<title>임시저장 목록</title>

<link rel="stylesheet"
href="<%=request.getContextPath()%>/resources/css/tempList.css">

<script>
// 임시저장 불러오기
function selectTemp(no){
    window.opener.location = "writeForm.jsp?temp_no=" + no + "&loaded=1";
    window.close();
}

// 임시저장 삭제
function deleteTemp(no){
    if(confirm("임시저장 글을 삭제하시겠습니까?")){
        location.href = "tempDeletePro.jsp?articleNo=" + no;
    }
}
</script>

</head>
<body>

<h1>임시저장 목록</h1>

<table border="1">
    <tr>
        <th>번호</th>
        <th>제목</th>
        <th>저장일</th>
        <th>불러오기</th>
        <th>삭제</th>
    </tr>

<%
    if(list == null || list.isEmpty()){
%>
    <tr>
        <td colspan="5" align="center">임시저장 글이 없습니다.</td>
    </tr>
<%
    } else {
        for(ArticleDTO dto : list){
%>
    <tr>
        <td><%= dto.getArticleNo() %></td>
        <td><%= dto.getSubject() %></td>
        <td><%= dto.getRegDt().toString().substring(0, 16) %></td>
        <td>
            <button onclick="selectTemp(<%= dto.getArticleNo() %>)">
                불러오기
            </button>
        </td>
        <td>
            <button onclick="deleteTemp(<%= dto.getArticleNo() %>)">
                삭제
            </button>
        </td>
    </tr>
<%
        }
    }
%>

</table>

</body>
</html>
