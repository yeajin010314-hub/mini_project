<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.ArticleDAO" %>
<%@ page import="dao.ArticleDtlDAO" %>
<%@ page import="java.io.File" %>
<%@ page import="dao.MypageDAO" %>

<%
    String memberId = (String)session.getAttribute("memberId");
    int articleNo = Integer.parseInt(request.getParameter("articleNo"));
    
    ArticleDAO dao = new ArticleDAO();
    ArticleDtlDAO dtdao = new ArticleDtlDAO();
    String writerId = dao.getArticle(articleNo).getMemberId();
    
    MypageDAO mdao = MypageDAO.getInstance();
    String memberStatCd = mdao.getMemberStatCd(memberId);
    boolean isAdmin = "3".equals(memberStatCd);
    
    if(memberId == null ||
    	(!memberId.equals(writerId) && !isAdmin)) {
%>
		<script>
		alert("삭제 권한이 없습니다.");
		history.go(-1);	
		</script>
<%
		return;
    }

    // 게시글에 첨부된 이미지 파일 삭제 처리
    // 서버에 저장된 업로드 폴더 실제 경로 가져오기
    String uploadPath = application.getRealPath("/resources/upload");
 	// 게시글 상세 정보에서 이미지 파일명 3개 가져오기
    String[] images = dtdao.getArticleDtl(articleNo) != null ? new String[]{
        dtdao.getArticleDtl(articleNo).getArticleImg1(),
        dtdao.getArticleDtl(articleNo).getArticleImg2(),
        dtdao.getArticleDtl(articleNo).getArticleImg3()
    } : new String[]{};
 	
 	// 실제 서버에 존재하는 이미지 파일 삭제
    for(String img : images){
    	// 파일명이 존재하는 경우에만 처리
        if(img != null && !img.equals("")){
        	
        	// 실제 파일 경로 생성
            File file = new File(uploadPath + "/" + img);
        	
        	// 파일이 실제로 존재하면 삭제 
            if(file.exists()){
                file.delete();
            }
        }
    }

    // DB에서 게시글 데이터 삭제
    int result = dao.deleteArticle(articleNo);
    
    if(result > 0){
        out.println("<script>alert('글이 삭제되었습니다.'); location.href='/mini_project/articleboard/board/boardList.jsp';</script>");
    } else {
%>
		<script>
		alert("삭제에 실패했습니다.");
		history.go(-1);
		</script>
<%
    }
%>
