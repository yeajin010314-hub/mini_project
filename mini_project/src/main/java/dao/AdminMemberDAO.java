package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dto.MemberBasDTO;
import dto.MemberRoleChange;

/*
 * 작성자 : 김상현
 * 작성일 : 2026-02-03
 * DESC : 관리자 회원관리 기능에서 회원 정보 조회 및 상태 변경을 처리하는 DAO
 */
public class AdminMemberDAO {
	// 필드 선언
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	/**
	 * 2.1 MemberBas 회원 List 조회(일반 DTO용)한다.
	 * 
	 * @param 화면에 표시될 페이지번호(pagenum)
	 * @param 한 화면에 표시될 페이지의 사이즈(pagesize)
	 * @return 회원현황 통계목록
	 */
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
	    		+ "       ,a.JOIN_DT as joinDt 							\n"
	    		+ "       ,a.LEAVE_DT as leaveDt						\n"
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

		    System.out.println("AdminMemberDAO.findAll.sql : " + sql);
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
                date = rs.getDate("leaveDt");
                if (date != null) {
	                dto.setLeaveDt(rs.getDate("leaveDt").toLocalDate());
                }
                date = rs.getDate("joinDt");
                if (date != null) {
	                dto.setJoinDt(rs.getDate("joinDt").toLocalDate());
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

	/**
	 * 2.2 전체 회원의 수를 조회한다.
	 * 
	 * @param 없음
	 * @return 전체 회원의 수
	 */
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

		    System.out.println("AdminMemberDAO.selectmemberTotalCount.sql : " + sql);
		    
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

	/**
	 * 2.3 회원관리 역활 변경(탈퇴, 회원, 기자, 관리자)한다.
	 * 
	 * @param 변경해야할 회원의 목록(MemberRoleChange)
	 * @return 전체 회원의 수
	 */
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
	    		+ "       ,JOURNAL_REASON_CD = '0'  \n"
	    		+ "       ,JOURNAL_REASON_DESC = '' \n"
	    		+ "       ,REG_DT = SYSDATE			\n"
	    		+ "       ,LEAVE_DT = DECODE(?, '0', SYSDATE, NULL)\n"
	    		+ "Where  member_id = ?";

		    System.out.println("AdminMemberDAO.updateMemberRoleChange.sql : " + sql);
		    
		    pstmt = conn.prepareStatement(sql);
		    
			pstmt.setString(1, dto.getMemberStatCd());
			pstmt.setString(2, dto.getMemberStatCd());
			pstmt.setString(3, dto.getMemberId());
			result = pstmt.executeUpdate();
		    
		    System.out.println("회원번호 : [" + dto.getMemberId() + "], 회원상태코드 : [" + dto.getMemberStatCd() + "] 변경 건수 : " + result);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return result;
	}
	
	/**
	 * 2.4 회원관리에서 신규회원을 추가한다.
	 * 
	 * @param 신규 회원(MemberBasDTO)
	 * @return 처리된 행 수
	 */
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

		    System.out.println("AdminMemberDAO.insertCategory.sql : " + sql);
		    
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

	/**
	 * 2.5 단일 회원 조회한다.(관리자)
	 * 
	 * @param 회원아이디(membereId)
	 * @return 회원정보
	 */
	public MemberBasDTO selectMemberBas(String memberId) {
		MemberBasDTO dto = new MemberBasDTO();
		int rowCount = 0; // 수행 건수
		
		try {
			conn = OraConn.getConnection();
		    if(conn != null) {
		        System.out.println("=======[DB 연결 성공]=======");
		    } else {
		    	System.out.println("=======[@@DB 연결 실패@@]=======");
		    }
		    
		    // 관리자 대시보드(회원현황) 집계조회
		    sql = "Select													\n"
	    		+ "        MEMBER_ID                as memberId				\n"
	    		+ "       ,MEMBER_NM                as memberNm				\n"
	    		+ "       ,PASSWD                   as passwd				\n"
	    		+ "       ,BIRTH                    as birth				\n"
	    		+ "       ,EMAIL                    as email				\n"
	    		+ "       ,TEL_NO                   as telNo				\n"
	    		+ "       ,NICK_NM                  as nickNm				\n"
	    		+ "       ,MEMBER_STAT_CD           as memberStatCd			\n"
	    		+ "       ,JOURNAL_REASON_CD        as journalReasonCd		\n"
	    		+ "       ,JOURNAL_REASON_DESC      as journalReasonDesc	\n"
	    		+ "       ,REG_DT                   as regDt				\n"
	    		+ "       ,PROFILE_IMG_URL          as profileImgUrl		\n"
	    		+ "From   MEMBER_BAS										\n"
	    		+ "Where  1 = 1												\n"
	    		+ "And    MEMBER_ID = ?";

		    System.out.println("AdminMemberDAO.selectMemberBas.sql : " + sql);
		    System.out.println("memberId : " + memberId);
		    
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, memberId);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) 
		    {
		    	rowCount++;

                dto.setMemberId(rs.getString("memberId"));
                dto.setMemberNm(rs.getString("memberNm"));
                dto.setPasswd(rs.getString("passwd"));
                dto.setBirth(rs.getString("birth"));
                dto.setEmail(rs.getString("email"));
                dto.setTelNo(rs.getString("telNo"));
                dto.setNickNm(rs.getString("nickNm"));
                dto.setMemberStatCd(rs.getString("memberStatCd"));
                dto.setJournalReasonCd(rs.getString("journalReasonCd"));
                dto.setJournalReasonDesc(rs.getString("journalReasonDesc"));
                
                Date date = rs.getDate("regDt");
                if (date != null) {
	                dto.setRegDt(rs.getDate("regDt").toLocalDate());
                }
                
                dto.setProfileImgUrl(rs.getString("profileImgUrl"));
		    }
		    System.out.println("조회된 dto 건수: " + rowCount);
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			OraConn.allClose(conn, pstmt, rs);
		}
		
		return dto;
		
	}

} // class of end;
