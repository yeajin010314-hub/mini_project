package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.ArticleDTO;

/*
작성자 : 박재운
작성일 : 2026-02-04
게시글 좋아요 및 북마크 기능을 처리하는 DAO 클래스
ARTICLE_DTL 테이블을 이용하여 좋아요 / 북마크 정보를 관리 
*/
public class ArticleDivDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // 특정 사용자가 해당 게시글에 좋아요를 눌렀는지 확인 
	public boolean isArticleLiked(int article_no, String member_id){
        boolean liked = false;
        try{
            conn = OraConn.getConnection();
            String sql = "SELECT COUNT(*) FROM article_div WHERE article_no=? AND member_id=? AND article_div_cd=1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, member_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                liked = rs.getInt(1) > 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt, rs);
        }
        return liked;
    }
	
	// 게시글 좋아요 정보를 추가
    public void insertArticleLike(int article_no, String member_id){
        try{
            conn = OraConn.getConnection();
            String sql = "INSERT INTO article_div(article_no, member_id, comment_id, article_div_cd, reg_dt) VALUES(?, ?, '0', 1, SYSDATE)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, member_id);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 게시글 좋아요를 취소(삭제)
    public void deleteArticleLike(int article_no, String member_id){
        try{
            conn = OraConn.getConnection();
            String sql = "DELETE FROM article_div WHERE article_no=? AND member_id=? AND article_div_cd=1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, member_id);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 게시글의 좋아요 개수 1 증가 
    public void increaseArticleGood(int article_no){
        try{
            conn = OraConn.getConnection();
            String sql = "UPDATE article SET good = good + 1 WHERE article_no=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 게시글의 좋아요 개수 1 감소
    public void decreaseArticleGood(int article_no){
        try{
            conn = OraConn.getConnection();
            String sql = "UPDATE article SET good = good - 1 WHERE article_no=? AND good > 0";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 특정 사용자가 게시글을 북마크 했는지 확인하는 메서드 
    public boolean isArticleBookmarked(int article_no, String member_id){
        boolean bookmarked = false;
        try{
            conn = OraConn.getConnection();
            String sql = "SELECT COUNT(*) FROM article_div WHERE article_no=? AND member_id=? AND article_div_cd=2";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, member_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                bookmarked = rs.getInt(1) > 0;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt, rs);
        }
        return bookmarked;
    }

    // 게시글을 북마크 목록에 추가하는 메서드 
    public void insertArticleBookmark(int article_no, String member_id){
        try{
            conn = OraConn.getConnection();
            String sql = "INSERT INTO article_div(article_no, member_id, comment_id, article_div_cd, reg_dt) VALUES(?, ?, '0', 2, SYSDATE)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, member_id);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }

    // 북마크를 취소하는 메서드 
    public void deleteArticleBookmark(int article_no, String member_id){
        try{
            conn = OraConn.getConnection();
            String sql = "DELETE FROM article_div WHERE article_no=? AND member_id=? AND article_div_cd=2";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, member_id);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 사용자가 북마크한 게시글 목록을 조회
    // 페이징, 검색, 정렬 기능 포함 
    public List<ArticleDTO> getBookmarkedArticles(
            String member_id,
            int startRow, int endRow,
            String keyword, String searchType,
            String sort) {

        List<ArticleDTO> list = new ArrayList<>();

        String orderBy;
        if ("popular".equals(sort)) {
            orderBy = "a.cnt desc";
        } else if ("recommended".equals(sort)) {
            orderBy = "a.good desc";
        } else {
            orderBy = "a.reg_dt desc";
        }

        try {
            conn = OraConn.getConnection();

            String sql = "select * from ( " +
                         " select row_number() over(order by " + orderBy + ") rn, " +
                         "        a.article_no, a.subject, a.member_id, a.category_id, " +
                         "        a.cnt, a.reg_dt, a.good, " +
                         "        m.nick_nm, c.category_nm, " +
                         "        d.article_img1 " + 
                         "   from article a " +
                         "   join member_bas m on a.member_id = m.member_id " +
                         "   join category c on a.category_id = c.category_id " +
                         "   join article_div ad on a.article_no = ad.article_no " +
                         "   left join article_dtl d on a.article_no = d.article_no " +
                         "  where a.article_stat = 1 " +
                         "    and ad.member_id = ? " +
                         "    and ad.article_div_cd = 2 ";
            
            // 검색 조건 추가 
            if(keyword != null && !keyword.trim().equals("")) {
                if("subject".equals(searchType)) {
                    sql += "and a.subject like ? ";
                } else if("article_content".equals(searchType)) {
                    sql += "and a.article_no in "
                         + "(select article_no from article_dtl where article_content like ?) ";
                } else if("nickNm".equals(searchType)) {
                    sql += "and m.nick_nm like ? ";
                } else if("category".equals(searchType)) {
                    sql += "and c.category_nm like ? ";
                }
            }

            sql += ") where rn between ? and ?";

            pstmt = conn.prepareStatement(sql);

            int idx = 1;
            pstmt.setString(idx++, member_id);

            if(keyword != null && !keyword.trim().equals("")) {
                pstmt.setString(idx++, "%" + keyword + "%");
            }

            pstmt.setInt(idx++, startRow);
            pstmt.setInt(idx++, endRow);

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
                dto.setRegDt(rs.getTimestamp("reg_dt"));
                dto.setGood(rs.getInt("good"));
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

    // 북마크한 게시글의 전체 개수를 조회
    // 페이징 처리를 위해 사용
    public int getBookmarkedArticleCount(String member_id, String keyword, String searchType) {
        int count = 0;

        try {
            conn = OraConn.getConnection();

            String sql = "select count(*) " +
                         "from article a " +
                         "join article_div ad on a.article_no = ad.article_no " +
                         "join member_bas m on a.member_id = m.member_id " +
                         "join category c on a.category_id = c.category_id " +
                         "where a.article_stat = 1 " +
                         "  and ad.member_id = ? " +
                         "  and ad.article_div_cd = 2 "; // 북마크만

            if(keyword != null && !keyword.trim().equals("")) {
                if("subject".equals(searchType)) {
                    sql += " and a.subject like ? ";
                } else if("article_content".equals(searchType)) {
                    sql += " and a.article_no in "
                         + "(select article_no from article_dtl where article_content like ?) ";
                } else if("nickNm".equals(searchType)) {
                    sql += " and m.nick_nm like ? ";
                } else if("category".equals(searchType)) {
                    sql += " and c.category_nm like ? ";
                }
            }

            pstmt = conn.prepareStatement(sql);
            int idx = 1;
            pstmt.setString(idx++, member_id);

            if(keyword != null && !keyword.trim().equals("")) {
                pstmt.setString(idx++, "%" + keyword + "%");
            }

            rs = pstmt.executeQuery();
            if(rs.next()) {
                count = rs.getInt(1);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return count;
    }
    
}
