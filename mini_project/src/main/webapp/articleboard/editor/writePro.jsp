<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.oreilly.servlet.MultipartRequest" %>
<%@ page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy" %>

<%@ page import="dao.ArticleDAO"%>
<%@ page import="dao.ArticleDtlDAO"%>

<%@ page import="dto.ArticleDTO"%>
<%@ page import="dto.ArticleDtlDTO"%>

<%@ page import="java.io.File" %>

<%
    String memberId = (String)session.getAttribute("memberId");

    String path = request.getRealPath("resources/upload");
    int max = 1024 * 1024 * 100;
    String enc = "UTF-8";
    DefaultFileRenamePolicy dp = new DefaultFileRenamePolicy();

    MultipartRequest mr = new MultipartRequest(request, path, max, enc, dp);

    String category = mr.getParameter("category_id");
    String subject = mr.getParameter("subject");
    String content = mr.getParameter("article_content");
    String tag = mr.getParameter("hash_tag");
    String link_text = mr.getParameter("link_text");
    String link_url = mr.getParameter("link_url");
    String contentRole = "0";
    if ("00005".equals(category)){
    	contentRole = "1";
    }
    
    // 서버단 입력값 검증
    // 카테고리 필수 체크
    if(category == null || category.trim().equals("")){
        out.println("<script>alert('카테고리를 선택하세요'); history.back();</script>");
        return;
    }
    // 제목 길이 체크
    if(subject == null || subject.trim().length() < 5 || subject.trim().length() > 100){
        out.println("<script>alert('제목은 5~100자 사이로 입력하세요'); history.back();</script>");
        return;
    }
    // 내용 최소 길이 체크
    if(content == null || content.trim().length() < 10){
        out.println("<script>alert('본문은 최소 50자 이상 입력하세요'); history.back();</script>");
        return;
    }
    // 태그 개수 검증 (최대 10개)
    if(tag != null && !tag.trim().equals("")){
        String[] tags = tag.split("#");
        int count = 0;
        for(String t : tags){
            if(t.trim().length() > 0) count++;
        }
        if(count > 10){
            out.println("<script>alert('태그는 최대 10개까지 입력 가능합니다'); history.back();</script>");
            return;
        }
    }
    
    if(memberId == null){
        out.println("<script>alert('로그인이 필요합니다'); location.href='/mini_project/articleboard/login.jsp';</script>");
        return;
    }
    
    // 이미지 파일 업로드 처리(최대 3개)
    String filename1 = mr.getFilesystemName("article_img1");
    String fileType1 = mr.getContentType("article_img1");
    File file1 = mr.getFile("article_img1");
    boolean uploadOk1 = true;

    if(fileType1 != null && file1 != null){
        String[] type = fileType1.split("/");
        if(!type[0].equals("image")){
            file1.delete();
            uploadOk1 = false;
        }
    } else {
        uploadOk1 = false;
    }
    if(!uploadOk1){
        filename1 = null;
    }

    String filename2 = mr.getFilesystemName("article_img2");
    String fileType2 = mr.getContentType("article_img2");
    File file2 = mr.getFile("article_img2");
    boolean uploadOk2 = true;
    if(fileType2 != null && file2 != null){
        String[] type = fileType2.split("/");
        if(!type[0].equals("image")){
            file2.delete();
            uploadOk2 = false;
        }
    } else {
        uploadOk2 = false;
    }
    if(!uploadOk2){
        filename2 = null;
    }

    String filename3 = mr.getFilesystemName("article_img3");
    String fileType3 = mr.getContentType("article_img3");
    File file3 = mr.getFile("article_img3");
    boolean uploadOk3 = true;
    if(fileType3 != null && file3 != null){
        String[] type = fileType3.split("/");
        if(!type[0].equals("image")){
            file3.delete();
            uploadOk3 = false;
        }
    } else {
        uploadOk3 = false;
    }
    if(!uploadOk3){
        filename3 = null;
    }
    
    // articleDTO (게시글 기본 정보)
    ArticleDTO dto = new ArticleDTO();
    dto.setCategoryId(category);
    dto.setSubject(subject);
    dto.setHashTag(tag);
    
    // article_stat : 0 = 임시저장, 1 = 등록
    dto.setArticleStat(Integer.parseInt(mr.getParameter("article_stat")));
    dto.setMemberId(memberId);
    
    // content_role : 0 = 일반 게시글, 1 = 공지 
    dto.setContent_role(contentRole);
    
    // ArticleDtlDTO (게시글 상세 정보)
    ArticleDtlDTO dtldto = new ArticleDtlDTO();
    dtldto.setArticleContent(content);
    dtldto.setArticleImg1(filename1);
    dtldto.setArticleImg2(filename2);
    dtldto.setArticleImg3(filename3);
    dtldto.setLinkText(link_text);
    dtldto.setLinkUrl(link_url);
    
    // DB 처리 (등록 or 수정))
    ArticleDAO dao = new ArticleDAO();
    ArticleDtlDAO dtdao = new ArticleDtlDAO();
    
    String articleNoStr = mr.getParameter("articleNo");
    int articleNo = 0;
    
    // 기존 글 수정 처리 
    if(articleNoStr != null && !articleNoStr.equals("")){
        articleNo = Integer.parseInt(articleNoStr);
        dto.setArticleNo(articleNo);
        dtldto.setArticleNo(articleNo);

        dao.updateArticle(dto);
        dtdao.updateArticleDtl(dtldto);
    } else {
        articleNo = dao.insertArticle(dto, dtldto);
    }
    
    // 처리 후 화면 이동 
    int stat = Integer.parseInt(mr.getParameter("article_stat"));

    if(stat == 0){
        // 임시저장인 경우 -> 다시 글쓰기 화면으로 이동
        response.sendRedirect("writeForm.jsp?temp_no=" + articleNo);
    } else {
        // 등록인 경우 -> 게시글 상세보기 화면으로 이동
        response.sendRedirect("/mini_project/articleboard/article/articleView.jsp?articleNo=" + articleNo);
    }
%>
