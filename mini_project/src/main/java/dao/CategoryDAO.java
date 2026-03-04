package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.ArticleDTO;

public class CategoryDAO {
	private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // 상위 카테고리 조회
    public List<ArticleDTO> getCategoryList(String memberStatCd){

        List<ArticleDTO> list = new ArrayList<>();

        try {
            conn = OraConn.getConnection();

            String sql = "select category_id, category_nm " +
                         "from category " +
                         "where up_category_id = '00000' ";

            // 관리자(3)가 아니면 공지 카테고리 제외
            if(!"3".equals(memberStatCd)){
                sql += " and category_id != '00005' ";
            }

            sql += " order by reg_dt";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                ArticleDTO dto = new ArticleDTO();
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

    
    // 하위 카테고리 조회
    public List<ArticleDTO> getSubCategoryList(String upCategoryId){
    	List<ArticleDTO> list = new ArrayList<>();
    	try {
    		conn = OraConn.getConnection();
    		String sql = "select category_id, category_nm from category where up_category_id = ? order by reg_dt";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, upCategoryId);
    		rs = pstmt.executeQuery();
    		while(rs.next()) {
    			ArticleDTO dto = new ArticleDTO();
    			dto.setCategoryId(rs.getString("category_id"));
    			dto.setCategoryNm(rs.getString("category_nm"));
    			list.add(dto);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}finally {
    		OraConn.allClose(conn, pstmt, rs);
    	}
    	return list;
    }    
    
    // 카테고리 이름 조회
    public String getCategoryName(String categoryId) {
        String name = "";
        try {
            conn = OraConn.getConnection();
            String sql = "SELECT category_nm FROM category WHERE category_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, categoryId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                name = rs.getString("category_nm");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        	OraConn.allClose(conn, pstmt, rs);
        }
        return name;
    }
}
