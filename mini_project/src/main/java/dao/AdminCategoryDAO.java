package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dto.CategoryDTO;

/*
 * 작성자 : 홍길동
 * 작성일 : 2026-02-03
 * DESC : 관리자 카테고리 관리 기능에서 카테고리 CRUD 처리를 담당하는 DAO
 */
public class AdminCategoryDAO {
	// 필드 선언
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * 3.1 Category 전체 목록을 조회한다.
	 * 
	 * @param 화면에 표시될 페이지번호(pagenum)
	 * @param 한 화면에 표시될 페이지의 사이즈(pagesize)
	 * @return 카테고리 목록
	 */
	public List<CategoryDTO> selectCategoryList(int pagenum, int pagesize) {
		List<CategoryDTO> categoryListResult = new ArrayList<>();
		int rowCount = 0;
		
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // Category List 조회(일반 DTO용)
		    sql = "Select																	\n"
		    		+ "        CATEGORY_ID 			 				 as categoryId			\n"
		    		+ "       ,CATEGORY_NM 			 				 as categoryNm			\n"
		    		+ "       ,UP_CATEGORY_ID						 as upCategoryId		\n"
		    		+ "       ,SYS_CONNECT_BY_PATH(CATEGORY_NM, '/') as pathCategoryNm		\n"
		    		+ "       ,LEVEL 								 as depthLevel			\n"
		    		+ "       ,REG_DT								 as regDt				\n"
		    		+ "From  CATEGORY														\n"
		    		+ "Start with UP_CATEGORY_ID = '00000'									\n"
		    		+ "Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID				\n"
		    		+ "ORDER SIBLINGS BY UP_CATEGORY_ID asc, CATEGORY_ID, REG_DT			\n"
		    		+ "OFFSET (? -1) * ? ROWS FETCH NEXT ? ROWS ONLY";


		    System.out.println("MemberRepositoryImpl.selectCategoryList.sql : " + sql);
		    System.out.println("pagenum : " + pagenum);
		    System.out.println("pagesize : " + pagesize);
		    
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setInt(1, pagenum);
		    pstmt.setInt(2, pagesize);
		    pstmt.setInt(3, pagesize);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
		    	rowCount++;
		    	CategoryDTO dto = new CategoryDTO();

		    	dto.setCategoryId(rs.getString("categoryId"));
		    	dto.setCategoryNm(rs.getString("categoryNm"));
		    	dto.setUpCategoryId(rs.getString("upCategoryId"));
		    	dto.setPathCategoryNm(rs.getString("pathCategoryNm"));
		    	dto.setDepthLevel(rs.getInt("depthLevel"));
                
                Date date = rs.getDate("regDt");
                if (date != null) {
	                dto.setRegDt(rs.getDate("regDt").toLocalDate());
                }
                categoryListResult.add(dto);

		        System.out.println("categoryId = [" + rs.getString("categoryId") + "], " 
		        				+ "categoryNm = [" + rs.getString("categoryNm") + "], "
		        				+ "upCategoryId = ["+ rs.getString("upCategoryId") + "], "
		        				+ "PathCategoryNm = ["+ rs.getString("pathCategoryNm") + "], "
		        				+ "DepthLevel = ["+ rs.getString("DepthLevel") + "], "
		        				+ "upCategoryId = ["+ rs.getString("regDt") + "]"
		        				  );
		        
		    }
		    System.out.println("전체 카테고리 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return categoryListResult;
	}

	/**
	 * 3.2 등록된 카테고리 전체건수를 조회한다.
	 * 
	 * @param 없음.
	 * @return 카테고리의 전체 행 수
	 */
	public int selectCategoryTotalCount() {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 카테고리 통계 조회(record DTO용)
		    sql = "Select Count(*) as cnt From CATEGORY";

		    System.out.println("MemberRepositoryImpl.selectCategoryTotalCount.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    if(rs.next()) {
		    	result = rs.getInt("cnt");
		    }
		    System.out.println("전체 카테고리 건수: " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return result;
	}

	/**
	 * 3.3 삭제대상의 하위 카테고리가 존재하는지 확인한다.
	 * 
	 * @param 삭제대상 카테고리
	 * @return 카테고리의 전체 행 수
	 */
	public int selectExistsChildCategory(CategoryDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
		    sql = "Select													\n"
	    		+ "        Count(*) as cnt									\n"
	    		+ "From category											\n"
	    		+ "Where  category_id != ?									\n"
	    		+ "And    DEL_YN = 'N'										\n"
	    		+ "Start With category_id = ?								\n"
	    		+ "And    DEL_YN = 'N'										\n"
	    		+ "Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID	\n"
	    		+ "And    del_yn = 'N'";

		    System.out.println("MemberRepositoryImpl.selectExistsChildCategory.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
			pstmt.setString(1, dto.getCategoryId());
			pstmt.setString(2, dto.getCategoryId());

		    System.out.println("카테고리ID : [" + dto.getCategoryId() + "], 카테고리명 : [" + dto.getCategoryNm() + "], 상위카테고리ID : [" 
		    				+ dto.getUpCategoryId() + "], 삭제여부 : [" + dto.getDelYn() + "], 변경 건수 : " + result);
		    
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
		        result = rs.getInt("cnt");
		        System.out.println("하위 카테고리 수 = [" + result + "]");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

        return result;

	}
	
	/**
	 * 3.3 변경해야할 대상Category를 변경한다.
	 * 
	 * @param 변경대상 카테고리
	 * @return 카테고리의 변경된 행 수
	 */
	public int updateCategory(CategoryDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 카테고리 수정
		    sql = "UPDATE						\n"
	    		+ "        CATEGORY				\n"
	    		+ "SET     CATEGORY_NM = ?		\n"
	    		+ "       ,UP_CATEGORY_ID = ?	\n"
	    		+ "       ,REG_DT = SYSDATE		\n"
	    		+ "       ,DEL_YN = ?    		\n"
	    		+ "WHERE  CATEGORY_ID = ?";

		    System.out.println("MemberRepositoryImpl.updateCategory.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
			pstmt.setString(1, dto.getCategoryNm());
			pstmt.setString(2, dto.getUpCategoryId());
			pstmt.setString(3, dto.getDelYn());
			pstmt.setString(4, dto.getCategoryId());

		    System.out.println("카테고리ID : [" + dto.getCategoryId() + "], 카테고리명 : [" + dto.getCategoryNm() + "], 상위카테고리ID : [" 
		    				+ dto.getUpCategoryId() + "], 삭제여부 : [" + dto.getDelYn() + "], 변경 건수 : " + result);
		    
			result = pstmt.executeUpdate();
		    
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

        return result;
	}

	/**
	 * 3.4 신규 Category를 등록한다.
	 * 
	 * @param 신규 카테고리
	 * @return 카테고리의 추가된 행 수
	 */
	public int insertCategory(CategoryDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 신규 카테고리 등록
		    sql = "Insert into CATEGORY VALUES(?, ?, ?, SYSDATE)";

		    System.out.println("MemberRepositoryImpl.insertCategory.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
		    pstmt.setString(1, dto.getCategoryId());
			pstmt.setString(2, dto.getCategoryNm());
			pstmt.setString(2, dto.getUpCategoryId());
			
			result = pstmt.executeUpdate();
		    
		    System.out.println("카테고리ID : [" + dto.getCategoryId() + "], 카테고리명 : [" + dto.getCategoryNm() + "], 상위카테고리ID : [" 
		    				+ dto.getUpCategoryId() + "], 변경 건수 : " + result);
		    
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

        return result;
	}

	/**
	 * 3.5 Category의 Tree구조를 조회한다.
	 * 
	 * @param 없음.
	 * @return Tree 구조 카테고리 목록
	 */
	public List<CategoryDTO> selectCategoryTreeList() {
		List<CategoryDTO> categoryListResult = new ArrayList<>();
		Map<String, CategoryDTO> map = new LinkedHashMap<>();
		
		int rowCount = 0;
		
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // Category List 조회(일반 DTO용), 공지 전용 카테고리 제외(00005)
		    sql = "Select																\n"
		    		+ "        CATEGORY_ID 			 				 as categoryId		\n"
		    		+ "       ,CATEGORY_NM 			 				 as categoryNm		\n"
		    		+ "       ,UP_CATEGORY_ID						 as upCategoryId	\n"
		    		+ "From   CATEGORY													\n"
		    		+ "Where  DEL_YN = 'N'                                              \n"
		    		+ "And    CATEGORY_ID != '00005'                                    \n"
		    		+ "Start with UP_CATEGORY_ID = '00000'								\n"
		    		+ "Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID			\n"
		    		+ "ORDER SIBLINGS BY UP_CATEGORY_ID asc, CATEGORY_ID, REG_DT";


		    System.out.println("MemberRepositoryImpl.selectCategoryTreeList.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
	    	System.out.println("===========================================================");

		    // 1. map 생성
		    while(rs.next()) 
		    {
		    	rowCount++;
		    	CategoryDTO dto = new CategoryDTO();

		    	String catId = rs.getString("categoryId");
	            String catNm = rs.getString("categoryNm");
	            String upCatId = rs.getString("upCategoryId");
	            
		    	dto.setCategoryId(catId);
		    	dto.setCategoryNm(catNm);
		    	dto.setUpCategoryId(upCatId);
                
		    	map.put(catId, dto);
		    	// ---------------- map log ------------------------
//		    	System.out.println("map : [" + map + "], map.size : [" + map.size() + "]");
//		    	System.out.println("catId : [" + catId + "], catNm : [" + catNm + "], upCatId : [" + upCatId + "]");
//		    	System.out.println("--------------------------------------------------------------");

		    	
//		    	for(Map.Entry<String, CategoryDTO> entry : map.entrySet()) {
//
//		    	    System.out.println("key = " + entry.getKey());
//		    	    System.out.println("value = " + entry.getValue());
//		    	}
	            
//		        System.out.println("categoryId = [" + rs.getString("categoryId") + "], " 
//		        				+ "categoryNm = [" + rs.getString("categoryNm") + "], "
//		        				+ "upCategoryId = ["+ rs.getString("upCategoryId") + "], "
//		        				  );
//		        System.out.println("========================================================");
		    }
		    
		    // 2. 트리 구성(List<CategoryDTO>에 구성)
		    for(CategoryDTO dto : map.values()) {

		        String upCatId = dto.getUpCategoryId();

		        if("00000".equals(upCatId)) {
		            categoryListResult.add(dto);
		        } else {
		            CategoryDTO parent = map.get(upCatId);

		            if(parent != null) {
		                parent.getChild().add(dto);
		            }
		        }
		    }
		    System.out.println("카테고리 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return categoryListResult;
	}
	
} // class of end;
