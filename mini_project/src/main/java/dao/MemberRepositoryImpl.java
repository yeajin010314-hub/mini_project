package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dto.AdminMemberStatsDTO;
import dto.CategoryDTO;
import dto.MemberBasDTO;
import dto.MemberRoleChange;

//각 DAO분리로 삭제예정
public class MemberRepositoryImpl implements MemberRepository {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	// 1.1 회원 현황 통계 조회(record DTO용)
	@Override
	public List<AdminMemberStatsDTO> selectMemberStatsList() {
		// MemberBas 회원현황 통계 추출(record DTO용)
		List<AdminMemberStatsDTO> listStatsResult = new ArrayList<>();
		int rowCount = 0; // 수행 건수
		
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 관리자 대시보드(회원현황) 집계조회
		    sql = "Select										\n"
	    		+ "        cd.CD_NM								\n"
	    		+ "       ,cd.CD_VAL as CD_VAL					\n"
	    		+ "       ,Count(MEMBER_ID) as CNT				\n"
	    		+ "From    member_bas a							\n"
	    		+ "       ,CODE_MST CD							\n"
	    		+ "Where  1 = 1									\n"
	    		+ "And    a.MEMBER_STAT_CD(+) = cd.CD_VAL		\n"
	    		+ "And    CD.CD_ID = 'MEMBER_STAT_CD'			\n"
	    		+ "Group by cd.CD_NM, cd.CD_VAL					\n"
	    		+ "Order by cd.CD_VAL							\n";

		    System.out.println("MemberRepositoryImpl.findById.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
		    	rowCount++;
		    	String cdnm = rs.getString("cd_nm");
		    	String cdval = rs.getString("cd_val");
		        int cnt = rs.getInt("cnt");
		        System.out.println("cdnm = [" + cdnm + "], cdval = [" + cdval + "],사용자 수 = [" + cnt + "]");
		        listStatsResult.add(new AdminMemberStatsDTO(cdnm, cdval, cnt));
		        
//		    	listStatsResult.add
//		    	(
//	                    new AdminMemberStatusDTO
//	                    (
//	                        rs.getString("cdnm"),
//	                        rs.getString("cdval"),
//	                        rs.getInt("cnt")
//	                    )
//	            );
		    }
		    System.out.println("조회된 role 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return listStatsResult;
	}

	// 2.1 MemberBas 회원 List 조회(일반 DTO용)
	@Override
	public List<MemberBasDTO> findAll(int pagenum, int pagesize) {
		
		List<MemberBasDTO> listResultSet = new ArrayList<>();
		int rowCount = 0; // 수행 건수
		
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 관리자 대시보드(회원현황) 집계조회
		    sql = "Select 												\n"
	    		+ "        MEMBER_ID as memberId						\n"
	    		+ "       ,MEMBER_NM as memberNm						\n"
	    		+ "       ,PASSWD as passwd								\n"
	    		+ "       ,BIRTH as birth								\n"
	    		+ "       ,EMAIL as email								\n"
	    		+ "       ,TEL_NO as telNo								\n"
	    		+ "       ,NICK_NM as nickNm							\n"
	    		+ "       ,MEMBER_STAT_CD as memberStatCd				\n"
	    		+ "       ,cd1.CD_NM      as memberStatNm				\n"
	    		+ "       ,JOURNAL_REASON_CD as journalReasonCd			\n"
	    		+ "       ,cd2.CD_NM         as journalReasonNm			\n"
	    		+ "       ,JOURNAL_REASON_DESC as journalReasonDesc		\n"
	    		+ "       ,a.REG_DT as regDt 							\n"
	    		+ "From    member_bas a									\n"
	    		+ "       ,CODE_MST CD1									\n"
	    		+ "       ,CODE_MST CD2									\n"
	    		+ "Where  1 = 1											\n"
	    		+ "And    a.MEMBER_STAT_CD(+)    = cd1.CD_VAL			\n"
	    		+ "And    a.JOURNAL_REASON_CD(+) = cd2.CD_VAL			\n"
	    		+ "And    CD1.CD_ID = 'MEMBER_STAT_CD'					\n"
	    		+ "And    CD2.CD_ID = 'JOURNAL_REASON_CD'				\n"
	    		+ "And    MEMBER_ID IS NOT NULL							\n"
	    		+ "Order  by MEMBER_ID									\n"
	    		+ "OFFSET (? -1) * ? ROWS FETCH NEXT ? ROWS ONLY";

		    System.out.println("MemberRepositoryImpl.findAll.sql : " + sql);
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

		    	MemberBasDTO dto = new MemberBasDTO();
                dto.setMemberId(rs.getString("memberId"));
                dto.setMemberNm(rs.getString("memberNm"));
                dto.setPasswd(rs.getString("passwd"));
                dto.setBirth(rs.getString("birth"));
                dto.setEmail(rs.getString("email"));
                dto.setTelNo(rs.getString("telNo"));
                dto.setNickNm(rs.getString("nickNm"));
                dto.setMemberStatCd(rs.getString("memberStatCd"));
                dto.setMemberStatNm(rs.getString("memberStatNm"));
                dto.setJournalReasonCd(rs.getString("journalReasonCd"));
                dto.setJournalReasonNm(rs.getString("journalReasonNm"));
                dto.setJournalReasonDesc(rs.getString("journalReasonDesc"));
                
                Date date = rs.getDate("regDt");
                if (date != null) {
	                dto.setRegDt(rs.getDate("regDt").toLocalDate());
                }
                listResultSet.add(dto);
		    }
		    System.out.println("조회된 dto 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return listResultSet;
	}

	// 1.2 기자 등록요청 통계 조회(record DTO용)
	@Override
	public List<AdminMemberStatsDTO> selectReqReportStatList() {

		List<AdminMemberStatsDTO> listStatsResult = new ArrayList<>();
		int rowCount = 0; // 수행 건수
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 관리자 대시보드(회원현황) 집계조회
		    sql = "Select										\n"
	    		+ "        cd.CD_NM								\n"
	    		+ "       ,cd.CD_VAL as CD_VAL					\n"
	    		+ "       ,Count(MEMBER_ID) as CNT				\n"
	    		+ "From    member_bas a							\n"
	    		+ "       ,CODE_MST CD							\n"
	    		+ "Where  1 = 1									\n"
	    		+ "And    a.JOURNAL_REASON_CD(+) = cd.CD_VAL	\n"
	    		+ "And    cd.CD_ID = 'JOURNAL_REASON_CD'		\n"
	    		+ "And    cd.cd_val != '0'                      \n"
	    		+ "Group by cd.CD_NM, cd.CD_VAL					\n"
	    		+ "Order by cd.CD_VAL							\n";

		    System.out.println("MemberRepositoryImpl.selectReqReportStatList.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
		    	rowCount++;
		    	String cdnm = rs.getString("cd_nm");
		    	String cdval = rs.getString("cd_val");
		        int cnt = rs.getInt("cnt");
		        
		        System.out.println("기자 등록요청 cdnm = [" + cdnm + "], cdval = [" + cdval + "],사용자 수 = [" + cnt + "]");
		        listStatsResult.add(new AdminMemberStatsDTO(cdnm, cdval, cnt));
		        
		    }
		    System.out.println("기자 등록요청 조회된 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

		return listStatsResult;
	}

	// 1.3 카테고리 통계 조회(record DTO용)
	@Override
	public List<AdminMemberStatsDTO> selectCategoryStatList() {
		
		List<AdminMemberStatsDTO> listCategoryResult = new ArrayList<>();
		int rowCount = 0; // 수행 건수
    	String cdnm = "";	// 코드명
    	String cdval = "";	// 코드값
        int cnt = 0;		// 건수

        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 카테고리 통계 조회(record DTO용)
		    sql = "Select										\n"
		    	+ "        DECODE(LVL, 1, '상위', 2, '하위', null) as CD_NM	\n"
		    	+ "       ,LVL as CD_VAL						\n"
		    	+ "       ,Count(*) as Cnt						\n"
		    	+ "From   (										\n"
		    	+ "        Select								\n"
		    	+ "                a.*, level as LVL			\n"
		    	+ "        From  CATEGORY a						\n"
		    	+ "        Start with UP_CATEGORY_ID = '00000'	\n"
		    	+ "        Connect by NOCYCLE PRIOR CATEGORY_ID = UP_CATEGORY_ID\n"
		    	+ "       )										\n"
		    	+ "Group by LVL									\n";

		    System.out.println("MemberRepositoryImpl.selectCategoryStatList.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
		    	rowCount++;
		    	cdnm = rs.getString("cd_nm");
		    	cdval = rs.getString("cd_val");
		        cnt = rs.getInt("cnt");
		        System.out.println("카테고리 cdnm = [" + cdnm + "], cdval = [" + cdval + "],사용자 수 = [" + cnt + "]");
		        listCategoryResult.add(new AdminMemberStatsDTO(cdnm, cdval, cnt));
		    }
		    System.out.println("카테고리 조회 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return listCategoryResult;
	}

	// 2.2 전체 회원수 조회
	@Override
	public int selectmemberTotalCount() {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 카테고리 통계 조회(record DTO용)
		    sql = "Select Count(*) as CNT From MEMBER_BAS";

		    System.out.println("MemberRepositoryImpl.selectmemberTotalCount.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    if(rs.next()) {
		    	result = rs.getInt("cnt");
		    }
		    System.out.println("전체 회원 건수: " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return result;
	}

	// 2.3 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
	@Override
	public int updateMemberRoleChange(MemberRoleChange dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
		    sql = "Update							\n"
	    		+ "        MEMBER_BAS				\n"
	    		+ "Set     MEMBER_STAT_CD = ?		\n"
	    		+ "Where  member_id = ?";

		    System.out.println("MemberRepositoryImpl.updateMemberRoleChange.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
			pstmt.setString(1, dto.getMemberStatCd());
			pstmt.setString(2, dto.getMemberId());
			result = pstmt.executeUpdate();
		    
		    System.out.println("회원번호 : [" + dto.getMemberId() + "], 회원상태코드 : [" + dto.getMemberStatCd() + "] 변경 건수 : " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return result;
	}
	
	// 2.4 회원관리 신규 회원 추가
	@Override
	public int insertMemberBas(MemberBasDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
		    sql = "Insert into member_bas Values(?, ?, ?, ?, ?, ?, ?, 1, 0, ?, SYSDATE)";

		    System.out.println("MemberRepositoryImpl.insertCategory.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
		    pstmt.setString(1, dto.getMemberId());
			pstmt.setString(2, dto.getMemberNm());
			pstmt.setString(3, dto.getPasswd());
			pstmt.setString(4, dto.getBirth());
			pstmt.setString(5, dto.getEmail());
			pstmt.setString(6, dto.getTelNo());
			pstmt.setString(7, dto.getNickNm());
			pstmt.setString(8, dto.getJournalReasonDesc());

			result = pstmt.executeUpdate();
		    
		    System.out.println("회원기본 : [" + dto.toString() + "], 변경 건수 : " + result);
		    
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

        return result;
	}

	
	// 3.3 삭제대상의 하위 카테고리 존재여부 확인
	@Override
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
	
	// 3.3 Category 수정
	@Override
	public int updateCategory(CategoryDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
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

	
	// 3.4 Category 등록
	@Override
	public int insertCategory(CategoryDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
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
	
	
	// 3.1 Category List 조회(일반 DTO용)
	@Override
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

	// 3.2 전체 카테고리 건수 조회
	@Override
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

	// 00. 공통코드 조회(record DTO)
	@Override
	public List<AdminMemberStatsDTO> selectStatByCode(String cd) {
		List<AdminMemberStatsDTO> statByCd = new ArrayList<>();
		int rowCount = 0;
		
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원현황의 회원상태코드 조회(JOURNAL_REASON_CD)
		    sql = "Select CD_NM,CD_VAL From CODE_MST Where CD_ID = ?";

		    System.out.println("MemberRepositoryImpl.selectStatByCode.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, cd);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next())
		    {
		    	rowCount++;
		    	String cdnm = rs.getString("cd_nm");
		    	String cdval = rs.getString("cd_val");
		        System.out.println("cd = [" + cd + "], cdnm = [" + cdnm + "], cdval = [" + cdval + "]");
		        statByCd.add(new AdminMemberStatsDTO(cdnm, cdval, 0));
		        
		    }
		    System.out.println("코드명 : [" + cd + "] 조회된 상태 건수: [" + rowCount + "]");
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

		return statByCd;
	}

	// 4.1 기자 등록요청 조회
	@Override
	public List<MemberBasDTO> selectJournalRegRequestList(int pagenum, int pagesize) {
		List<MemberBasDTO> JourRegReqList = new ArrayList<>();
		int rowCount = 0;
		
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 기자 등록요청 조회
		    sql = "Select													\n"
	    		+ "        MEMBER_ID            as memberId					\n"
	    		+ "       ,MEMBER_NM            as memberNm					\n"
	    		+ "       ,NICK_NM              as nickNm					\n"
	    		+ "       ,JOURNAL_REASON_CD    as journalReasonCd			\n"
	    		+ "       ,JOURNAL_REASON_DESC  as journalReasonDesc		\n"
	    		+ "       ,cd.cd_nm             as journalReasonNm			\n"
	    		+ "       ,a.REG_DT             as regDt					\n"
	    		+ "From    member_bas a										\n"
	    		+ "       ,CODE_MST cd										\n"
	    		+ "Where  1 = 1												\n"
	    		+ "And    cd.cd_id  = 'JOURNAL_REASON_CD'					\n"
	    		+ "And    cd.cd_val = '1'									\n"
	    		+ "And    cd.CD_VAL = a.JOURNAL_REASON_CD					\n"
		    	+ "OFFSET (? -1) * ? ROWS FETCH NEXT ? ROWS ONLY";


		    System.out.println("MemberRepositoryImpl.selectJournalRegRequestList.sql : " + sql);
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
		    	MemberBasDTO dto = new MemberBasDTO();
		    	dto.setMemberId(rs.getString("memberId"));
		    	dto.setMemberNm(rs.getString("memberNm"));
		    	dto.setNickNm(rs.getString("nickNm"));
		    	dto.setJournalReasonCd(rs.getString("journalReasonCd"));
		    	dto.setJournalReasonDesc(rs.getString("journalReasonDesc"));
		    	dto.setJournalReasonNm(rs.getString("journalReasonNm"));

		    	Date date = rs.getDate("regDt");
                if (date != null) {
	                dto.setRegDt(rs.getDate("regDt").toLocalDate());
                }
		    	
		        System.out.println("memberId = [" + rs.getString("memberId") + "], " 
        				+ "memberNm = [" + rs.getString("memberNm") + "], "
        				+ "nickNm = ["+ rs.getString("nickNm") + "], "
        				+ "journalReasonCd = ["+ rs.getString("journalReasonCd") + "], "
        				+ "journalReasonDesc = ["+ rs.getString("journalReasonDesc") + "], "
        				+ "journalReasonNm = ["+ rs.getString("journalReasonNm") + "], "
        				+ "regDt = ["+ rs.getDate("regDt") + "]"
        				  );
		        JourRegReqList.add(dto);
		        
		    }
		    System.out.println(" 조회된 상태 건수: [" + rowCount + "]");
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}

		return JourRegReqList;
	}


	// 4.3 기자 등록 상태변경
	@Override
	public int updateMemberJournalCdStatus(MemberBasDTO dto) {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
		    sql = "Update								\n"
		    		+ "        MEMBER_BAS				\n"
		    		+ "Set     JOURNAL_REASON_CD   = ?	\n"
		    		+ "       ,JOURNAL_REASON_DESC = ?	\n"
		    		+ "       ,REG_DT = SYSDATE			\n"
		    		+ "Where  MEMBER_ID = ?				\n";

		    System.out.println("MemberRepositoryImpl.updateMemberJournalCdStatus.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
			pstmt.setString(1, dto.getMemberStatCd());
			pstmt.setString(2, dto.getMemberId());
			result = pstmt.executeUpdate();
		    
		    System.out.println("회원번호 : [" + dto.getMemberId() + "], 회원상태코드 : [" + dto.getMemberStatCd() + "] 변경 건수 : " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return result;
	}

	@Override
	public int selectJournalRegTotalCount() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void save(MemberBasDTO memberBas) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void update(MemberBasDTO memberBas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String memberId) {
		// TODO Auto-generated method stub
		
	}


}