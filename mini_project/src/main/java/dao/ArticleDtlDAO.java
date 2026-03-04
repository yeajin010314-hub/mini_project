package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.ArticleDtlDTO;

/*
작성자 : 박재운
작성일 : 2026-02-10
게시글 상세 내용 데이터베이스 처리를 담당하는 DAO 클래스
게시글 상세 내용 수정 조회 기능 제공 
*/
public class ArticleDtlDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
	// 게시글 상세 내용 수정 
    public int updateArticleDtl(ArticleDtlDTO dtl) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = "update article_dtl set article_content=?, article_img1=?, article_img2=?, article_img3=?, link_text=?, link_url=? where article_no=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dtl.getArticleContent());
            pstmt.setString(2, dtl.getArticleImg1());
            pstmt.setString(3, dtl.getArticleImg2());
            pstmt.setString(4, dtl.getArticleImg3());
            pstmt.setString(5, dtl.getLinkText());
            pstmt.setString(6, dtl.getLinkUrl());
            pstmt.setInt(7, dtl.getArticleNo());
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result;
    }
    
    // 게시글 상세 정보 조회
    public ArticleDtlDTO getArticleDtl(int articleNo) {
        ArticleDtlDTO dtl = null;
        try {
            conn = OraConn.getConnection();
            String sql = "select * from article_dtl where article_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, articleNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dtl = new ArticleDtlDTO();
                dtl.setArticleNo(rs.getInt("article_no"));
                dtl.setArticleContent(rs.getString("article_content"));
                dtl.setArticleImg1(rs.getString("article_img1"));
                dtl.setArticleImg2(rs.getString("article_img2"));
                dtl.setArticleImg3(rs.getString("article_img3"));
                dtl.setLinkText(rs.getString("link_text"));
                dtl.setLinkUrl(rs.getString("link_url"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return dtl;
    }
}
