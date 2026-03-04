package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dto.AdminMemberStatsDTO;

/*
 * 작성자 : 김상현
 * 작성일 : 2026-02-03
 * DESC : 관리자 통계 대시보드에서 사용하는 통계 데이터 조회를 위한 DAO
 */
public class AdminDashboardStatsDAO {
	
	// 필드 선언
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * 1.1 회원 현황 통계를 조회(record DTO용)한다.
	 * 
	 * @param 없음.
	 * @return 회원현황 통계목록
	 */
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
		    }
		    System.out.println("조회된 role 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return listStatsResult;
	}

	/**
	 * 1.2 기자 등록요청 통계 조회(record DTO용)한다.
	 * 
	 * @param 없음.
	 * @return 회원등록요청 통계목록
	 */
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

	/**
	 * 1.3 카테고리 통계 조회(record DTO용)한다.
	 * 
	 * @param 없음.
	 * @return 카테고리 통계목록
	 */
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

	/**
	 * 00. 공통코드를 조회(record DTO)한다.
	 * 
	 * @param 조회할 공통코드
	 * @return 공통코드 목록
	 */
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

} // class of end;
