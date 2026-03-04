package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.MemberBasDTO;
import web.data.OraConn;

public class LoginDAO {

    /* ================= 싱글톤 ================= */
    private static final LoginDAO instance = new LoginDAO();
    public static LoginDAO getInstance() { return instance; }
    private LoginDAO() {}

    /* ================= 공통 변수 ================= */
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    /** 1) 로그인 검증 (탈퇴회원은 막힘: MEMBER_STAT_CD <> '0') */
    public synchronized MemberBasDTO login(String memberId, String passwd) {
    	MemberBasDTO dto = null;

        Connection c = null;
        PreparedStatement p = null;
        ResultSet r = null;

        try {
            c = OraConn.getConnection();

            String sql =
                "SELECT MEMBER_ID, MEMBER_NM, PASSWD, BIRTH, EMAIL, TEL_NO, NICK_NM, " +
                "       MEMBER_STAT_CD, JOURNAL_REASON_CD, JOURNAL_REASON_DESC, " +
                "       NVL(PROFILE_IMG_URL,'') AS PROFILE_IMG_URL, " +
                "       NVL(PROC_REASON,'') AS PROC_REASON " +
                "  FROM MEMBER_BAS " +
                " WHERE MEMBER_ID=? " +
                "   AND PASSWD=? " +
                "   AND NVL(MEMBER_STAT_CD,'1') <> '0'";

            p = c.prepareStatement(sql);
            p.setString(1, memberId);
            p.setString(2, passwd);
            r = p.executeQuery();

            if (r.next()) {
                dto = new MemberBasDTO();
                dto.setMemberId(r.getString("MEMBER_ID"));
                dto.setMemberNm(r.getString("MEMBER_NM"));
                dto.setPasswd(r.getString("PASSWD"));
                dto.setBirth(r.getString("BIRTH"));
                dto.setEmail(r.getString("EMAIL"));
                dto.setTelNo(r.getString("TEL_NO"));
                dto.setNickNm(r.getString("NICK_NM"));
                dto.setMemberStatCd(r.getString("MEMBER_STAT_CD"));
                dto.setJournalReasonCd(r.getString("JOURNAL_REASON_CD"));
                dto.setJournalReasonDesc(r.getString("JOURNAL_REASON_DESC"));
                dto.setProfileImgUrl(r.getString("PROFILE_IMG_URL"));
                try { dto.setProcReason(r.getString("PROC_REASON")); } catch (Exception ignore) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(c, p, r);
        }

        return dto;
    }

    /** 2) 로그인 실패 시: “탈퇴 계정인지” 확인(아이디/비번까지 맞는 탈퇴계정) */
    public synchronized boolean isLeavedAccount(String memberId, String passwd) {
        boolean ok = false;
        try {
            conn = OraConn.getConnection();
            String sql = """
                SELECT COUNT(*)
                  FROM MEMBER_BAS
                 WHERE MEMBER_ID = ?
                   AND PASSWD = ?
                   AND NVL(MEMBER_STAT_CD,'1') = '0'
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, passwd);
            rs = pstmt.executeQuery();
            if (rs.next()) ok = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return ok;
    }

    /** 3) 복구 가능(3개월 이내) 체크 */
    public synchronized boolean canRestoreWithin3Months(String memberId) {
        boolean ok = false;
        try {
            conn = OraConn.getConnection();
            String sql = """
                SELECT COUNT(*) CNT
                  FROM MEMBER_BAS
                 WHERE MEMBER_ID = ?
                   AND NVL(MEMBER_STAT_CD,'1') = '0'
                   AND LEAVE_DT IS NOT NULL
                   AND LEAVE_DT >= ADD_MONTHS(TRUNC(SYSDATE), -3)
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) ok = rs.getInt("CNT") > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return ok;
    }

    /** 4) 복구 전 “이메일이 맞는지” 검증 */
    public synchronized boolean verifyRestoreEmail(String memberId, String email) {
        boolean ok = false;
        try {
            conn = OraConn.getConnection();
            String sql = """
                SELECT COUNT(*)
                  FROM MEMBER_BAS
                 WHERE MEMBER_ID = ?
                   AND LOWER(TRIM(EMAIL)) = LOWER(TRIM(?))
                   AND NVL(MEMBER_STAT_CD,'1') = '0'
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, email);
            rs = pstmt.executeQuery();
            if (rs.next()) ok = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return ok;
    }

    /** 5) 최종 복구 실행(기자신청값 유지) */
    public synchronized int restoreMember(String memberId) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = """
                UPDATE MEMBER_BAS
                   SET MEMBER_STAT_CD = '1',
                       LEAVE_DT = NULL
                 WHERE MEMBER_ID = ?
                   AND NVL(MEMBER_STAT_CD,'1') = '0'
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }

    /** 6) 아이디 찾기 */
    public synchronized String findMemberId(String name, String birth, String email) {
        String memberId = null;
        try {
            conn = OraConn.getConnection();
            String sql =
                "SELECT MEMBER_ID " +
                "  FROM MEMBER_BAS " +
                " WHERE TRIM(MEMBER_NM)=TRIM(?) " +
                "   AND REPLACE(TRIM(BIRTH),'-','')=TRIM(?) " +
                "   AND LOWER(TRIM(EMAIL))=LOWER(TRIM(?))";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, birth);
            pstmt.setString(3, email);
            rs = pstmt.executeQuery();
            if (rs.next()) memberId = rs.getString("MEMBER_ID");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return memberId;
    }

    /** 7) 비번 재설정 전 확인: 아이디 + 이메일 일치 여부 */
    public synchronized boolean verifyIdEmail(String memberId, String email) {
        boolean ok = false;
        try {
            conn = OraConn.getConnection();
            String sql =
                "SELECT COUNT(*) FROM MEMBER_BAS " +
                " WHERE MEMBER_ID=? AND TRIM(EMAIL)=TRIM(?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, email);
            rs = pstmt.executeQuery();
            if (rs.next()) ok = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return ok;
    }

    /** 8) 아이디 마스킹(FindIdServlet에서 사용) */
    public static String maskId(String id) {
        if (id == null) return "";
        id = id.trim();
        int n = id.length();
        if (n <= 1) return "*";
        if (n == 2) return id.substring(0, 1) + "*";
        int show = Math.min(3, n);
        String head = id.substring(0, show);
        return head + "*".repeat(n - show);
    }
}
