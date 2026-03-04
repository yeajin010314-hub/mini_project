package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import web.data.OraConn;

public class LeaveDAO {

    /* ================= 싱글톤 ================= */
    private static final LeaveDAO instance = new LeaveDAO();
    public static LeaveDAO getInstance() { return instance; }
    private LeaveDAO() {}

    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    /** 탈퇴 처리(삭제 X) */
    public synchronized int leaveMember(String memberId) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = """
                UPDATE MEMBER_BAS
                   SET MEMBER_STAT_CD = '0',
                       LEAVE_DT = SYSDATE
                 WHERE MEMBER_ID = ?
                   AND NVL(MEMBER_STAT_CD,'1') <> '0'
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result; // 1 성공, 0 이미 탈퇴/없음
    }
  //복구 가능(3개월 이내) 체크
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

    //복구 전 “이메일이 맞는지” 검증
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

    //최종 복구 실행(기자신청값 유지)
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
        return result; // 1 성공
    }

}
