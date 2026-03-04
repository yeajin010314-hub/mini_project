package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.ArticleDTO;
import web.data.OraConn;

public class MypageArticleDivDAO {

    /* ================= 싱글톤 ================= */
    private static final MypageArticleDivDAO instance = new MypageArticleDivDAO();
    public static MypageArticleDivDAO getInstance() { return instance; }
    private MypageArticleDivDAO() {}

    /* ================= 공통 변수 ================= */
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    /** ✅ 내가 좋아요한 기사 목록 (ARTICLE_DIV_CD='1') */
    public synchronized List<ArticleDTO> getLikedArticles(String memberId) {
        List<ArticleDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql =
                "SELECT a.ARTICLE_NO, a.CATEGORY_ID, a.SUBJECT, a.MEMBER_ID, " +
                "       a.HASH_TAG, a.ARTICLE_STAT, a.GOOD, a.CNT, a.REG_DT, a.CONTENT_ROLE " +
                "  FROM ARTICLE_DIV d " +
                "  JOIN ARTICLE a ON a.ARTICLE_NO = d.ARTICLE_NO " +
                " WHERE d.MEMBER_ID = ? " +
                "   AND d.ARTICLE_DIV_CD = '1' " +
                " ORDER BY d.REG_DT DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ArticleDTO dto = new ArticleDTO();
                dto.setArticleNo(rs.getInt("ARTICLE_NO"));
                dto.setCategoryId(rs.getString("CATEGORY_ID"));
                dto.setSubject(rs.getString("SUBJECT"));
                dto.setMemberId(rs.getString("MEMBER_ID")); // 작성자 ID
                dto.setHashTag(rs.getString("HASH_TAG"));
                dto.setArticleStat(rs.getInt("ARTICLE_STAT"));
                dto.setGood(rs.getInt("GOOD"));
                dto.setCnt(rs.getInt("CNT"));
                dto.setRegDt(rs.getTimestamp("REG_DT"));
                dto.setContent_role(rs.getString("CONTENT_ROLE"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return list;
    }

    /** ✅ 내가 작성한 기사 목록 */
    public synchronized List<ArticleDTO> getMyArticles(String memberId) {
        List<ArticleDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql =
                "SELECT ARTICLE_NO, CATEGORY_ID, SUBJECT, MEMBER_ID, HASH_TAG, " +
                "       ARTICLE_STAT, GOOD, CNT, REG_DT, CONTENT_ROLE " +
                "  FROM ARTICLE " +
                " WHERE MEMBER_ID = ? " +
                " ORDER BY REG_DT DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ArticleDTO dto = new ArticleDTO();
                dto.setArticleNo(rs.getInt("ARTICLE_NO"));
                dto.setCategoryId(rs.getString("CATEGORY_ID"));
                dto.setSubject(rs.getString("SUBJECT"));
                dto.setMemberId(rs.getString("MEMBER_ID"));
                dto.setHashTag(rs.getString("HASH_TAG"));
                dto.setArticleStat(rs.getInt("ARTICLE_STAT"));
                dto.setGood(rs.getInt("GOOD"));
                dto.setCnt(rs.getInt("CNT"));
                dto.setRegDt(rs.getTimestamp("REG_DT"));
                dto.setContent_role(rs.getString("CONTENT_ROLE"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return list;
    }
    
    // 회원 구분 0 : 일반 회원, 1 : 기자, 2 : 관리자 
    public String getMemberStatCd(String memberId) {
        String memberStatCd = null;
        try {
           conn = OraConn.getConnection();
           String sql = "select member_stat_cd from member_bas where member_id=?";
           pstmt = conn.prepareStatement(sql);
           pstmt.setString(1, memberId);
           rs = pstmt.executeQuery();
           if(rs.next()) {
              memberStatCd = rs.getString("member_stat_cd");
           }        
        }catch(Exception e) {
           e.printStackTrace();
        }finally {
           OraConn.allClose(conn, pstmt, rs);
        }
        
        return memberStatCd;
     }
}
