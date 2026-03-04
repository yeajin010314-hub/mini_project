package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.MemberBasDTO;
import dto.ArticleDTO;
import dto.ArticleDivDTO;
import web.data.OraConn;

public class MypageDAO {

    /* ================= 싱글톤 ================= */
    private static final MypageDAO instance = new MypageDAO();
    public static MypageDAO getInstance() { return instance; }
    private MypageDAO() {}

    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    /** 마이페이지용: DB 최신 회원정보 1줄 조회 */
    public synchronized MemberBasDTO getMemberInfo(String memberId) {
    	MemberBasDTO dto = null;
        try {
            conn = OraConn.getConnection();
            String sql =
                "SELECT MEMBER_ID, MEMBER_NM, PASSWD, BIRTH, EMAIL, TEL_NO, NICK_NM, " +
                "       MEMBER_STAT_CD, JOURNAL_REASON_CD, JOURNAL_REASON_DESC, " +
                "       REG_DT, PROFILE_IMG_URL, PROC_REASON, LEAVE_DT, JOIN_DT " +
                "  FROM MEMBER_BAS " +
                " WHERE MEMBER_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new MemberBasDTO();
                dto.setMemberId(rs.getString("MEMBER_ID"));
                dto.setMemberNm(rs.getString("MEMBER_NM"));
                dto.setBirth(rs.getString("BIRTH"));
                dto.setEmail(rs.getString("EMAIL"));
                dto.setTelNo(rs.getString("TEL_NO"));
                dto.setNickNm(rs.getString("NICK_NM"));
                dto.setMemberStatCd(rs.getString("MEMBER_STAT_CD"));
                dto.setJournalReasonCd(rs.getString("JOURNAL_REASON_CD"));
                dto.setJournalReasonDesc(rs.getString("JOURNAL_REASON_DESC"));
                dto.setProfileImgUrl(rs.getString("PROFILE_IMG_URL"));
                try { dto.setProcReason(rs.getString("PROC_REASON")); } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return dto;
    }

    /** 프로필(이메일/전화) 같이 업데이트(원하면 사용) */
    public synchronized int updateProfile(String id, String email, String phone) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = "UPDATE MEMBER_BAS SET EMAIL=?, TEL_NO=? WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, id);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 이메일만 변경 */
    public synchronized boolean updateEmail(String memberId, String email) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql = "UPDATE MEMBER_BAS SET EMAIL=? WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, memberId);
            result = (pstmt.executeUpdate() == 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 프로필 이미지 경로 업데이트 */
    public synchronized boolean updateProfileImgUrl(String memberId, String profilePath) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql = "UPDATE MEMBER_BAS SET PROFILE_IMG_URL=? WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, profilePath);
            pstmt.setString(2, memberId);
            result = (pstmt.executeUpdate() == 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 현재 비번 확인 */
    public synchronized boolean checkPassword(String id, String pw) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql = "SELECT COUNT(*) FROM MEMBER_BAS WHERE MEMBER_ID=? AND PASSWD=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            rs = pstmt.executeQuery();
            if (rs.next()) result = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result;
    }

    /** 비밀번호 변경(로그인 후 변경 / 재설정에도 사용 가능) */
    public synchronized int updatePassword(String id, String pw) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = "UPDATE MEMBER_BAS SET PASSWD=? WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, pw);
            pstmt.setString(2, id);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 닉네임 변경 */
    public synchronized int updateNick(String memberId, String nick) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = "UPDATE MEMBER_BAS SET NICK_NM=? WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nick);
            pstmt.setString(2, memberId);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 기자 신청 상태 코드만 조회 */
    public synchronized String getJournalStatus(String memberId) {
        String status = null;
        try {
            conn = OraConn.getConnection();
            String sql = "SELECT JOURNAL_REASON_CD FROM MEMBER_BAS WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) status = rs.getString("JOURNAL_REASON_CD");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return status;
    }

    /** JOURNAL_REASON_CD + JOURNAL_REASON_DESC 업데이트 */
    public synchronized boolean updateJournalStatus(String memberId, String statusCd, String reason) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql = "UPDATE MEMBER_BAS SET JOURNAL_REASON_CD=?, JOURNAL_REASON_DESC=? WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statusCd);
            pstmt.setString(2, reason);
            pstmt.setString(3, memberId);
            result = pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 기자 신청(7일 제한 포함) */
    public synchronized String applyJournal(String memberId, String reason) {
        try {
            conn = OraConn.getConnection();

            // 1) 현재 상태 확인
            String checkSql = "SELECT NVL(JOURNAL_REASON_CD,'0') AS CD FROM MEMBER_BAS WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (!rs.next()) return "NOUSER";

            String cd = rs.getString("CD");
            OraConn.allClose(null, pstmt, rs);
            pstmt = null; rs = null;

            if ("1".equals(cd)) return "ALREADY_APPLIED";
            if ("2".equals(cd)) return "ALREADY_REPORTER";
            if ("4".equals(cd)) return "ON_HOLD";

            // 2) 7일 경과 체크
            String daySql =
                "SELECT CASE WHEN (TRUNC(SYSDATE) - TRUNC(JOIN_DT)) >= 7 " +
                "            THEN 1 ELSE 0 END AS OK " +
                "  FROM MEMBER_BAS WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(daySql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt("OK") != 1) {
                OraConn.allClose(null, pstmt, rs);
                return "TOOEARLY";
            }
            OraConn.allClose(null, pstmt, rs);
            pstmt = null; rs = null;

            // 3) 신청 업데이트(대기: 1)
            String upSql =
                "UPDATE MEMBER_BAS " +
                "   SET JOURNAL_REASON_CD = '1', " +
                "       JOURNAL_REASON_DESC = ?, " +
                "       PROC_REASON = NULL " +
                " WHERE MEMBER_ID = ?";
            pstmt = conn.prepareStatement(upSql);
            pstmt.setString(1, reason);
            pstmt.setString(2, memberId);

            int updated = pstmt.executeUpdate();
            return (updated == 1) ? "OK" : "FAIL";

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return "FAIL";
    }

    /** 기자 신청 취소(대기 상태(1)일 때만 0으로) */
    public synchronized String cancelJournal(String memberId) {
        try {
            conn = OraConn.getConnection();
            String sql = """
                UPDATE MEMBER_BAS
                   SET JOURNAL_REASON_CD='0',
                       JOURNAL_REASON_DESC='',
                       PROC_REASON=''
                 WHERE MEMBER_ID=?
                   AND JOURNAL_REASON_CD='1'
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int updated = pstmt.executeUpdate();
            return (updated == 1) ? "OK" : "INVALID_STATUS";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return "FAIL";
    }

    /** 회원 구분 코드 조회(1:일반,2:기자,3:관리자 등) */
    public synchronized String getMemberStatCd(String memberId) {
        String memberStatCd = null;
        try {
            conn = OraConn.getConnection();
            String sql = "SELECT MEMBER_STAT_CD FROM MEMBER_BAS WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) memberStatCd = rs.getString("MEMBER_STAT_CD");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return memberStatCd;
    }
    
    
    
    
}