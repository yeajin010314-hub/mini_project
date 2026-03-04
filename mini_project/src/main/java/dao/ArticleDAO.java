package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.ArticleDTO;
import dto.ArticleDtlDTO;

/*
작성자 : 박재운
작성일 : 2026-02-04
게시글 관련 데이터베이스 처리를 담당하는 DAO 클래스
게시글 등록, 수정, 삭제, 조회 기능 제공 
*/
public class ArticleDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // 게시글과 게시글 상세 내용을 함께 등록
    public int insertArticle(ArticleDTO dto, ArticleDtlDTO dtldto) {
        int articleNo = 0;

        try {
            conn = OraConn.getConnection();

            String sql1 =
                "insert into article(article_no, category_id, subject, member_id, hash_tag, article_stat, content_role, good, cnt, reg_dt) " +
                "values(article_seq.nextval,?,?,?,?,?,?,0,0,sysdate)";
            pstmt = conn.prepareStatement(sql1);
            pstmt.setString(1, dto.getCategoryId());
            pstmt.setString(2, dto.getSubject()); 
            pstmt.setString(3, dto.getMemberId());
            pstmt.setString(4, dto.getHashTag());
            pstmt.setInt(5, dto.getArticleStat());
            pstmt.setString(6, dto.getContent_role());
            pstmt.executeUpdate();

            String sqlSeq = "select article_seq.currval from dual";
            pstmt = conn.prepareStatement(sqlSeq);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                articleNo = rs.getInt(1);
            }

            String sql2 =
                "insert into article_dtl(article_no, article_content, article_img1, article_img2, article_img3, link_text, link_url, reg_dt) " +
                "values(?,?,?,?,?,?,?,sysdate)";
            pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1, articleNo);
            pstmt.setString(2, dtldto.getArticleContent());
            pstmt.setString(3, dtldto.getArticleImg1());
            pstmt.setString(4, dtldto.getArticleImg2());
            pstmt.setString(5, dtldto.getArticleImg3());
            pstmt.setString(6, dtldto.getLinkText());
            pstmt.setString(7, dtldto.getLinkUrl());
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return articleNo;
    }

    // 게시글 기본 정보 수정
    public int updateArticle(ArticleDTO dto) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = "update article set category_id=?, subject=?, hash_tag=?, article_stat=?, content_role=?, reg_dt = sysdate where article_no=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getCategoryId());
            pstmt.setString(2, dto.getSubject());
            pstmt.setString(3, dto.getHashTag());
            pstmt.setInt(4, dto.getArticleStat());
            pstmt.setString(5, dto.getContent_role());
            pstmt.setInt(6, dto.getArticleNo());
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result;
    }
    
    // 게시글 삭제 
    public int deleteArticle(int article_no) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql1 = "DELETE FROM article_dtl WHERE article_no=?";
            pstmt = conn.prepareStatement(sql1);
            pstmt.setInt(1, article_no);
            pstmt.executeUpdate();
            pstmt.close();

            String sql2 = "DELETE FROM article WHERE article_no=?";
            pstmt = conn.prepareStatement(sql2);
            pstmt.setInt(1, article_no);
            result = pstmt.executeUpdate();

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return result;
    }    
    
    // 게시글 기본 정보 조회
    public ArticleDTO getArticle(int articleNo) {
        ArticleDTO dto = null;
        try {
            conn = OraConn.getConnection();

            String sql = 
            	"SELECT a.article_no, a.category_id, a.subject, a.member_id, " +
            	"       m.nick_nm, a.hash_tag, a.article_stat, a.content_role, a.good, a.cnt " +
            	"FROM article a " +
            	"JOIN member_bas m ON a.member_id = m.member_id " +
            	"WHERE a.article_no = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, articleNo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new ArticleDTO();
                dto.setArticleNo(rs.getInt("article_no"));
                dto.setCategoryId(rs.getString("category_id"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setNickNm(rs.getString("nick_nm"));
                dto.setHashTag(rs.getString("hash_tag"));
                dto.setArticleStat(rs.getInt("article_stat"));
                dto.setGood(rs.getInt("good"));
                dto.setCnt(rs.getInt("cnt"));
                dto.setContent_role(rs.getString("content_role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return dto;
    }
    
    // 게시글 조회수 증가 
    public void increaseCnt(int articleNo) {
        try {
            conn = OraConn.getConnection();
            String sql = "update article set cnt = cnt + 1 where article_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, articleNo);
            pstmt.executeUpdate();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
    }
    
    // 특정 사용자의 임시저장 글 목록을 조회 
    public List<ArticleDTO> getTempList(String memberId) {
        List<ArticleDTO> list = new ArrayList<>();
        try {
            conn = OraConn.getConnection();
            String sql = "select article_no, subject, reg_dt from article where member_id=? and article_stat=0 order by reg_dt desc";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ArticleDTO dto = new ArticleDTO();
                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setRegDt(rs.getTimestamp("reg_dt"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return list;
    }
    
    // 임시저장 글 목록 삭제 
    public boolean deleteTempArticle(int articleNo, String memberId){

        boolean result = false;

        try{
            conn = OraConn.getConnection();

            String sql = "DELETE FROM article "
                       + "WHERE article_no = ? "
                       + "AND member_id = ? "
                       + "AND article_stat = 0";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, articleNo);
            pstmt.setString(2, memberId);

            int cnt = pstmt.executeUpdate();

            if(cnt > 0){
                result = true;
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt, null);
        }

        return result;
    }
}
