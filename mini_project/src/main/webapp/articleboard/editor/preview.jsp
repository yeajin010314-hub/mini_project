<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>
<%@ page import="java.io.File" %>
<%@ page import="dao.CategoryDAO" %>

<%
	CategoryDAO cdao = new CategoryDAO();
    String uploadPath = application.getRealPath("/resources/upload");
    int maxSize = 1024 * 1024 * 100;
    String enc = "UTF-8";

    MultipartRequest mr = new MultipartRequest(
        request,
        uploadPath,
        maxSize,
        enc,
        new DefaultFileRenamePolicy()
    );

    String category = mr.getParameter("category_id");
    String subject  = mr.getParameter("subject");
    String content  = mr.getParameter("article_content");
    String tag      = mr.getParameter("hash_tag");

    String img1 = mr.getFilesystemName("article_img1");
    String img2 = mr.getFilesystemName("article_img2");
    String img3 = mr.getFilesystemName("article_img3");
    String link_text = mr.getParameter("link_text");
    String link_url  = mr.getParameter("link_url");
%>

<!DOCTYPE html>
<html>
<head>
<title>미리보기</title>

<link rel="stylesheet"
href="<%=request.getContextPath()%>/resources/css/preview.css">

</head>
<body>

<!-- 상단바 때문에 콘텐츠 밀림 방지용 여백 -->
<div style="height:50px;"></div>

<div class="preview-wrap">

<h1><%= subject %></h1>

<p><strong>카테고리 :</strong> 
<%= cdao.getCategoryName(category) %>
</p>


<hr>

<%
if(img1 != null){
%>
<img class="preview-img"
 src="<%= request.getContextPath() %>/resources/upload/<%= img1 %>">
<%
}
if(img2 != null){
%>
<img class="preview-img"
 src="<%= request.getContextPath() %>/resources/upload/<%= img2 %>">
<%
}
if(img3 != null){
%>
<img class="preview-img"
 src="<%= request.getContextPath() %>/resources/upload/<%= img3 %>">
<%
}
%>

<div class="content">
    <%= content.replace("\n", "<br>") %>
</div>

<hr>

<%
if(tag != null && !tag.trim().equals("")){
%>
<hr>
<p class="tags">태그 : <%= tag %></p>
<%
}
%>

<%
if(link_url != null && !link_url.trim().equals("")
 && link_text != null && !link_text.trim().equals("")){
%>
<div class="preview-link">
    <strong>관련 링크:</strong>
    <a href="<%= link_url %>" target="_blank">
        <%= link_text %>
    </a>
</div>
<% } %>

</div>

</body>
</html>

