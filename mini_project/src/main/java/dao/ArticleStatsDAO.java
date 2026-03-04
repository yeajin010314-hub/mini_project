package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.ArticleDTO;

/*
작성자 : 박재운
작성일 : 2026-02-10
통계성 조회 데이터베이스 처리를 담당하는 DAO 클래스
통계성 조회 기능 제공 
*/
public class ArticleStatsDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
	// 댓글이 많은 순서대로 상위 게시글을 조회
    public List<ArticleDTO> getTopByComment(int limit) {
        List<ArticleDTO> list = new ArrayList<>();
        try {
            conn = OraConn.getConnection();

            String sql =
                "SELECT * FROM ( " +
                "  SELECT a.article_no, a.subject, a.member_id, a.category_id, a.cnt, a.good, a.reg_dt, " +
                "         d.article_img1, " +    
                "         m.nick_nm, c.category_nm, " +
                "         (SELECT COUNT(*) FROM article_comment c2 " +
                "            WHERE c2.article_no = a.article_no) AS comment_cnt " +
                "  FROM article a " +
                "  JOIN member_bas m ON a.member_id = m.member_id " +
                "  JOIN category c ON a.category_id = c.category_id " +
                "  LEFT JOIN article_dtl d ON a.article_no = d.article_no " +
                "  WHERE a.article_stat = 1 " +
                "  And a.content_role = '0'" + 
                "  ORDER BY comment_cnt DESC " +
                ") WHERE ROWNUM <= ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArticleDTO dto = new ArticleDTO();

                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setNickNm(rs.getString("nick_nm"));
                dto.setCategoryId(rs.getString("category_id"));
                dto.setCategoryNm(rs.getString("category_nm"));
                dto.setCnt(rs.getInt("cnt"));
                dto.setGood(rs.getInt("good"));
                dto.setRegDt(rs.getTimestamp("reg_dt"));

                String img = rs.getString("article_img1");
                if(img == null || img.trim().equals("")) {
                    dto.setThumbnail(null);
                } else {
                    dto.setThumbnail(img);
                }

                list.add(dto);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return list;
    }

    
    // 북마크가 많은 순서대로 게시글을 조회
    public List<ArticleDTO> getTopByBookmark(int limit) {
        List<ArticleDTO> list = new ArrayList<>();
        try {
            conn = OraConn.getConnection();

            String sql =
                "SELECT * FROM ( " +
                "  SELECT a.article_no, a.subject, a.member_id, a.category_id, a.cnt, a.good, a.reg_dt, " +
                "         d.article_img1, " +
                "         m.nick_nm, c.category_nm, " +
                "         (SELECT COUNT(*) FROM article_div ad " +
                "           WHERE ad.article_no = a.article_no AND ad.article_div_cd = 2) AS bookmark_cnt " +
                "  FROM article a " +
                "  JOIN member_bas m ON a.member_id = m.member_id " +
                "  JOIN category c ON a.category_id = c.category_id " +
                "  LEFT JOIN article_dtl d ON a.article_no = d.article_no " +
                "  WHERE a.article_stat = 1 " +
                "  And a.content_role = '0'" + 
                "  ORDER BY bookmark_cnt DESC " +
                ") WHERE ROWNUM <= ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArticleDTO dto = new ArticleDTO();

                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setNickNm(rs.getString("nick_nm"));
                dto.setCategoryId(rs.getString("category_id"));
                dto.setCategoryNm(rs.getString("category_nm"));
                dto.setCnt(rs.getInt("cnt"));
                dto.setGood(rs.getInt("good"));
                dto.setRegDt(rs.getTimestamp("reg_dt"));
                String img = rs.getString("article_img1");
                if(img == null || img.trim().equals("")) {
                    dto.setThumbnail(null);
                } else {
                    dto.setThumbnail(img);
                }

                list.add(dto);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return list;
    }
    
    // 랜덤으로 게시글을 조회 
    public List<ArticleDTO> getRandomArticles(int limit) {
        List<ArticleDTO> list = new ArrayList<>();
        try {
            conn = OraConn.getConnection();

            String sql =
                "SELECT * FROM ( " +
                "  SELECT a.article_no, a.subject, a.member_id, a.category_id, a.cnt, a.good, a.reg_dt, " +
                "         d.article_img1, " +
                "         m.nick_nm, c.category_nm " +
                "  FROM article a " +
                "  JOIN member_bas m ON a.member_id = m.member_id " +
                "  JOIN category c ON a.category_id = c.category_id " +
                "  LEFT JOIN article_dtl d ON a.article_no = d.article_no " +   // ★ 핵심
                "  WHERE a.article_stat = 1 " +
                "  And a.content_role = '0'" + 
                "  ORDER BY dbms_random.value " +
                ") WHERE ROWNUM <= ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArticleDTO dto = new ArticleDTO();

                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setNickNm(rs.getString("nick_nm"));
                dto.setCategoryId(rs.getString("category_id"));
                dto.setCategoryNm(rs.getString("category_nm"));
                dto.setCnt(rs.getInt("cnt"));
                dto.setGood(rs.getInt("good"));
                dto.setRegDt(rs.getTimestamp("reg_dt"));
                String img = rs.getString("article_img1");
                if(img == null || img.trim().equals("")) {
                    dto.setThumbnail(null);
                } else {
                    dto.setThumbnail(img);
                }
                list.add(dto);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return list;
    }
}
