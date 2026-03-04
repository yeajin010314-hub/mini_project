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
목록 조회 데이터베이스 처리를 담당하는 DAO 클래스
게시글 리스트 조회, 페이징, 검색, 정렬 기능 제공 
*/
public class ArticleListDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // 게시글 목록 조회
    // 카테고리, 키워드 검색 지원 
    public List<ArticleDTO> getArticleList(
            int startRow,
            int endRow,
            String keyword,
            String categoryId
    ) {
        List<ArticleDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql =
                "select * from ( " +
                "  select row_number() over(order by a.reg_dt desc) rn, " +
                "         a.article_no, a.subject, a.member_id, a.category_id, " +
                "         a.cnt, a.reg_dt, " +
                "         m.nick_nm, c.category_nm " + 
                "    from article a " +
                "    join member_bas m on a.member_id = m.member_id " +
                "    join category c on a.category_id = c.category_id " +
                "   where a.article_stat = 1 " + 
                "   And a.content_role = '0'";

            if (categoryId != null && !categoryId.trim().equals("")) {
                sql += " and a.category_id = ? ";
            }

            if (keyword != null && !keyword.trim().equals("")) {
                sql += " and ( a.subject like ? "
                     + " or a.article_no in ( "
                     + "     select ad.article_no "
                     + "       from article_dtl ad "
                     + "      where ad.article_content like ? "
                     + " )) ";
            }

            sql += ") where rn between ? and ?";

            pstmt = conn.prepareStatement(sql);

            int idx = 1;
            if (categoryId != null && !categoryId.trim().equals("")) {
                pstmt.setString(idx++, categoryId);
            }
            if (keyword != null && !keyword.trim().equals("")) {
                pstmt.setString(idx++, "%" + keyword + "%");
                pstmt.setString(idx++, "%" + keyword + "%");
            }

            pstmt.setInt(idx++, startRow);
            pstmt.setInt(idx++, endRow);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                ArticleDTO dto = new ArticleDTO();
                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setNickNm(rs.getString("nick_nm"));
                dto.setCategoryId(rs.getString("category_id"));
                dto.setCategoryNm(rs.getString("category_nm"));
                dto.setCnt(rs.getInt("cnt"));
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
    
    // 게시글 목록 조회
    // 정렬, 검색타입 지원
    // 인기순, 추천순, 최신순 정렬 가능
    // 썸네일 이미지 정보 포함 
    public List<ArticleDTO> getArticleList( 
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

            String sql =
                "select * from ( " +
                " select row_number() over(order by " + orderBy + ") rn, " +
                "        a.article_no, a.subject, a.member_id, a.category_id, " +
                "        a.cnt, a.reg_dt, a.good, " +
                "        m.nick_nm, c.category_nm, " +
                "        d.article_img1 " +
                "   from article a " +
                "   join member_bas m on a.member_id = m.member_id " +
                "   join category c on a.category_id = c.category_id " +
                "   left join article_dtl d on a.article_no = d.article_no " +
                "  	where a.article_stat = 1 " + 
                "   And a.content_role = '0'";

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

    // 최신 게시글 목록을 조회하되 특정 게시글과 이미 조회된 게시글들은 제외하고 가져오기 
    public List<ArticleDTO> getLatestArticlesExclude(int article_no, int limit, List<Integer> excludeIds) {

        List<ArticleDTO> list = new ArrayList<>();

        try{
            conn = OraConn.getConnection();

            StringBuilder sql = new StringBuilder();

            sql.append("SELECT * FROM ( ");
            sql.append("  SELECT a.article_no, a.subject, a.member_id, a.cnt, a.good, a.reg_dt, ");
            sql.append("         a.category_id, m.member_nm ");
            sql.append("  FROM article a, member_bas m ");
            sql.append("  WHERE a.member_id = m.member_id ");
            sql.append("    AND a.article_no != ? ");
            sql.append(" 	AND a.article_stat = 1 ");
            sql.append("    AND a.content_role = '0' ");

            // 이미 가져온 글 제외
            if(excludeIds != null && excludeIds.size() > 0){
                sql.append(" AND a.article_no NOT IN (");

                for(int i=0; i<excludeIds.size(); i++){
                    sql.append("?");
                    if(i < excludeIds.size()-1){
                        sql.append(",");
                    }
                }
                sql.append(")");
            }

            sql.append("  ORDER BY a.reg_dt DESC ");
            sql.append(") WHERE ROWNUM <= ?");

            pstmt = conn.prepareStatement(sql.toString());

            int idx = 1;

            pstmt.setInt(idx++, article_no);

            if(excludeIds != null){
                for(Integer id : excludeIds){
                    pstmt.setInt(idx++, id);
                }
            }

            pstmt.setInt(idx++, limit);

            rs = pstmt.executeQuery();

            while(rs.next()){
                ArticleDTO dto = new ArticleDTO();

                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setMemberNm(rs.getString("member_nm"));
                dto.setCnt(rs.getInt("cnt"));
                dto.setGood(rs.getInt("good"));
                dto.setRegDt(rs.getTimestamp("reg_dt"));
                dto.setCategoryId(rs.getString("category_id"));

                list.add(dto);
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            OraConn.allClose(conn, pstmt, rs);
        }

        return list;
    }
    
    // 게시글 개수를 조회
    // 검색 조건이 있을 경우 조건에 맞는 게시글 수만 조회
    public int getArticleCount(String keyword, String searchType) {
        int count = 0;

        try {
            conn = OraConn.getConnection();

            String sql = "select count(*) from article a "
                       + "join member_bas m on a.member_id = m.member_id "
                       + "join category c on a.category_id = c.category_id "
                       + "where a.article_stat = 1 "
                       + "and a.content_role = '0'";
            
            // 검색 조건이 있을 경우 조건 추가 
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

            pstmt = conn.prepareStatement(sql);
            
            int idx = 1;
            if(keyword != null && !keyword.trim().equals("")) {
                pstmt.setString(idx++, "%" + keyword + "%");
            }

            rs = pstmt.executeQuery();
            if(rs.next()) count = rs.getInt(1);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return count;
    }
    
    // 최신순으로 게시글을 페이징 처리하여 조회하는 메서드
    public List<ArticleDTO> getRecentArticlesPaging(int startRow, int endRow) {

        List<ArticleDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql =
                "SELECT * FROM ( " +
                "  SELECT ROWNUM rnum, A.* FROM ( " +
                "    SELECT a.article_no, a.subject, a.member_id, a.cnt, a.good, a.reg_dt, " +
                "           a.category_id, m.nick_nm, c.category_nm " +
                "    FROM article a " +
                "    JOIN member_bas m ON a.member_id = m.member_id " +
                "    JOIN category c ON a.category_id = c.category_id " +
                "    WHERE a.content_role = '0' " +
                "	 AND a.article_stat = 1 " +
                "    ORDER BY a.reg_dt DESC " +
                "  ) A " +
                ") WHERE rnum BETWEEN ? AND ?";

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, startRow);
            pstmt.setInt(2, endRow);

            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArticleDTO dto = new ArticleDTO();

                dto.setArticleNo(rs.getInt("article_no"));
                dto.setSubject(rs.getString("subject"));
                dto.setMemberId(rs.getString("member_id"));
                dto.setNickNm(rs.getString("nick_nm"));
                dto.setCnt(rs.getInt("cnt"));
                dto.setGood(rs.getInt("good"));
                dto.setRegDt(rs.getTimestamp("reg_dt"));
                dto.setCategoryId(rs.getString("category_id"));
                dto.setCategoryNm(rs.getString("category_nm"));

                list.add(dto);
            }

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return list;
    }

    
    // 전체 게시글 수를 조회하는 메서드 
    public int getTotalArticleCount() {

        int count = 0;

        try {
            conn = OraConn.getConnection();

            String sql = "SELECT COUNT(*) FROM article where content_role = '0' and article_stat = 1";

            pstmt = conn.prepareStatement(sql);
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
    
    // 공지글 목록 조회
    // content_role = 1 인 게시글만 최신순으로 조회
    public List<ArticleDTO> getNoticeList() {

        List<ArticleDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql =
                "select * from ( " +
                "  select a.article_no, a.subject, a.member_id, a.category_id, " +
                "         a.cnt, a.good, a.reg_dt, " +
                "         m.nick_nm, c.category_nm, " +
                "         d.article_img1 " +
                "    from article a " +
                "    join member_bas m on a.member_id = m.member_id " +
                "    join category c on a.category_id = c.category_id " +
                "    left join article_dtl d on a.article_no = d.article_no " +
                "   where a.article_stat = 1 " +
                "     and a.content_role = '1' " +
                "     and m.member_stat_cd = '3' " +
                "   order by a.reg_dt desc " +
                ") where rownum <= 5";

            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            while (rs.next()) {
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }

        return list;
    }
    
}
