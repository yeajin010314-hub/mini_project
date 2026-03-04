package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.CommentDTO;


/*
작성자 : 박재운
작성일 : 2026.02.04
댓글 관련 DB 처리를 담당하는 DAO 클래스
댓글 조회, 등록, 삭제, 좋아요 기능 처리
*/
public class CommentDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // 특정 게시글의 댓글 목록 조회
    public List<CommentDTO> getCommentList(int article_no) {

        List<CommentDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql = "SELECT c.comment_id, c.member_id, m.nick_nm, c.article_no, c.up_comment_id, c.comment_content, c.comment_good, c.reg_dt FROM article_comment c JOIN member_bas m ON c.member_id = m.member_id WHERE c.article_no = ? ORDER BY c.reg_dt ASC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                CommentDTO cdto = new CommentDTO();

                cdto.setCommentId(rs.getString("comment_id"));
                cdto.setMemberId(rs.getString("member_id"));
                cdto.setNickNm(rs.getString("nick_nm"));
                cdto.setArticleNo(rs.getInt("article_no"));
                cdto.setUpCommentId(rs.getString("up_comment_id"));
                cdto.setCommentContent(rs.getString("comment_content"));
                cdto.setCommentGood(rs.getInt("comment_good"));
                cdto.setRegDt(rs.getTimestamp("reg_dt"));

                list.add(cdto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return list;
    }
    
    // 댓글 등록 처리 
    public boolean insertComment(String member_id, int article_no, String up_comment_id, String comment_content){
    	try {
    		conn = OraConn.getConnection();
    		String sql ="insert into article_comment(comment_id, member_id, article_no, up_comment_id, comment_content, comment_good, reg_dt) values(comment_seq.nextval,?,?,?,?,0,sysdate)";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, member_id);
    		pstmt.setInt(2, article_no);
    		pstmt.setString(3, (up_comment_id == null || up_comment_id.equals("")) ? null : up_comment_id);
    		pstmt.setString(4, comment_content);
    		
    		int cnt = pstmt.executeUpdate();
    		return cnt > 0;
    	}catch(Exception e) {
    		e.printStackTrace();
    	}finally {
    		OraConn.allClose(conn, pstmt, rs);
    	}
    	return false;
    }
    
    // 댓글 삭제 처리 
    public void deleteComment(String comment_id){
        try{
            conn = OraConn.getConnection();
            String sql = "DELETE FROM article_comment where comment_id IN (select comment_id from article_comment start with comment_id=? connect by prior comment_id = up_comment_id)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comment_id);;
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 특정 회원이 특정 댓글에 좋아요를 눌렀는지 확인 
    public boolean isCommentLiked(String comment_id, String member_id){
        boolean liked = false;
        try{
            conn = OraConn.getConnection();
            String sql = "SELECT COUNT(*) FROM article_div WHERE comment_id=? AND member_id=? AND article_div_cd=3";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comment_id);
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

    // 댓글 좋아요 추가 
    public void insertCommentLike(String comment_id, String memberId, int article_no){
        try{
            conn = OraConn.getConnection();
            String sql = "INSERT INTO article_div (article_no, comment_id, member_id, article_div_cd, reg_dt) VALUES (?, ?, ?, 3, sysdate)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);
            pstmt.setString(2, comment_id);
            pstmt.setString(3, memberId);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 댓글 좋아요 취소(삭제)
    public void deleteCommentLike(String comment_id, String memberId, int article_no){
        try{
            conn = OraConn.getConnection();
            String sql = "DELETE FROM article_div WHERE article_no=? AND comment_id=? AND member_id=? AND article_div_cd=3";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, article_no);       // 추가
            pstmt.setString(2, comment_id);
            pstmt.setString(3, memberId);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 댓글의 좋아요 개수 증감 처리 
    public void updateCommentGood(String comment_id, int delta){
        try{
            conn = OraConn.getConnection();
            String sql = "UPDATE article_comment SET comment_good = comment_good + ? WHERE comment_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, delta);
            pstmt.setString(2, comment_id);
            pstmt.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt);
        }
    }
    
    // 특정 댓글의 현재 좋아요 수 조회 
    public int getCommentGoodCount(String comment_id){
        int count = 0;
        try{
            conn = OraConn.getConnection();
            String sql = "SELECT comment_good FROM article_comment WHERE comment_id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comment_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                count = rs.getInt(1);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt, rs);
        }
        return count;
    }
    
    // 댓글 작성자 조회 
    public String getCommentWriter(String comment_id){
        String writer = null;
        try{
            conn = OraConn.getConnection();
            String sql = "SELECT member_id FROM article_comment WHERE comment_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, comment_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                writer = rs.getString("member_id");
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt, rs);
        }
        return writer;
    }
}
