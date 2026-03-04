package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dto.MemberBasDTO;

/*
 * 작성자 : 김상현
 * 작성일 : 2026-02-03
 * DESC : 기자 등록 요청에 대한 조회 및 승인/거절 처리를 담당하는 DAO
 */
public class AdminJournalReqDAO {
	// 필드 선언
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * 4.1 기자등록 요청 대상을 조회한다.
	 * 
	 * @param 화면에 표시될 페이지번호(pagenum)
	 * @param 한 화면에 표시될 페이지의 사이즈(pagesize)
	 * @return 기자등록요청 목록
	 */
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
	    		+ "       ,PROC_REASON          as procReason   			\n"
	    		+ "       ,cd.cd_nm             as journalReasonNm			\n"
	    		+ "       ,a.REG_DT             as regDt					\n"
	    		+ "From    member_bas a										\n"
	    		+ "       ,CODE_MST cd										\n"
	    		+ "Where  1 = 1												\n"
	    		+ "And    cd.cd_id  = 'JOURNAL_REASON_CD'					\n"
	    		+ "And    cd.cd_val = '1'									\n"
	    		+ "And    cd.CD_VAL = a.JOURNAL_REASON_CD					\n"
	    		+ "Order  by MEMBER_ID										\n"
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
		    	dto.setProcReason(rs.getString("procReason"));
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

	/**
	 * 4.2 기자등록 요청한 전체 건수를 조회한다.
	 * 
	 * @param 없음.
	 * @return 기자등록요청한 전체 행 수
	 */
    public int selectJournalRegTotalCount() {
		int result = 0;
        try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 카테고리 통계 조회(record DTO용)
		    sql = "Select Count(*) as cnt From member_bas Where  1 = 1 And JOURNAL_REASON_CD = '1'";

		    System.out.println("MemberRepositoryImpl.selectJournalRegTotalCount.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    if(rs.next()) {
		    	result = rs.getInt("cnt");
		    }
		    System.out.println("전체 기자등록 요청 건수: " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return result;
    }

	/**
	 * 4.3 기자 등록요청한 대상의 상태를 변경한다.
	 * 
	 * @param 기자등록 요청대상.
	 * @return 상대변경한 행 수
	 */
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
		    		+ "       ,PROC_REASON = ?			\n"
		    		+ "       ,REG_DT = SYSDATE			\n"
		    		+ "Where  MEMBER_ID = ?				\n";

		    System.out.println("MemberRepositoryImpl.updateMemberJournalCdStatus.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
			pstmt.setString(1, dto.getJournalReasonCd());
			pstmt.setString(2, dto.getProcReason());
			pstmt.setString(3, dto.getMemberId());
			result = pstmt.executeUpdate();
		    
		    System.out.println("회원번호 : [" + dto.getMemberId() + "], 처리사유 : [" + dto.getProcReason() + "] 변경 건수 : " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		return result;
	}

} // class of end;
