package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.MemberBasDTO;
import web.data.OraConn;

public class JoinDAO {

    /* ================= 싱글톤 ================= */
    private static final JoinDAO instance = new JoinDAO();
    public static JoinDAO getInstance() { return instance; }
    private JoinDAO() {}

    /* ================= 공통 변수 ================= */
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    /** 1) 아이디 중복 체크 (회원가입 / AJAX) */
    public boolean isIdDuplicate(String memberId) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql = "SELECT COUNT(*) FROM MEMBER_BAS WHERE MEMBER_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) result = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result;
    }

    /** 2) 신규 회원가입 시: 탈퇴 아이디 포함 “이미 존재하면 가입 불가” */
    public boolean isIdUnavailableForJoin(String memberId) {
        boolean exists = false;
        try {
            conn = OraConn.getConnection();
            String sql = "SELECT COUNT(*) FROM MEMBER_BAS WHERE MEMBER_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) exists = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return exists; // true면 가입 불가
    }

    /** 3) 닉네임 중복 체크 (내 정보 수정용, 내 아이디 제외) */
    public boolean isNickDuplicate(String nick, String myId) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql = """
                SELECT COUNT(*)
                FROM MEMBER_BAS
                WHERE NICK_NM=? 
                  AND MEMBER_ID<>?
                  AND NVL(MEMBER_STAT_CD,'1') <> '0'
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nick);
            pstmt.setString(2, myId);
            rs = pstmt.executeQuery();
            if (rs.next()) result = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result;
    }

    /** 4) 회원가입용 닉네임 중복체크 (AJAX / join) */
    public boolean isNickDuplicateForJoin(String nick) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql =
                "SELECT COUNT(*) FROM MEMBER_BAS " +
                "WHERE TRIM(NICK_NM)=TRIM(?) AND NVL(MEMBER_STAT_CD,'1') <> '0'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nick == null ? "" : nick.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) result = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result;
    }

    /** 5) 회원가입 INSERT */
    public int insertMember(MemberBasDTO dto) {
        int result = 0;
        try {
            conn = OraConn.getConnection();
            String sql = """
                INSERT INTO MEMBER_BAS
                (MEMBER_ID, MEMBER_NM, PASSWD, BIRTH, EMAIL, TEL_NO, NICK_NM, 
                 MEMBER_STAT_CD, JOURNAL_REASON_CD, JOURNAL_REASON_DESC, REG_DT, 
                 PROFILE_IMG_URL, PROC_REASON, JOIN_DT, LEAVE_DT)
                VALUES
                (?,?,?,?,?,?,?,'1','0','',SYSDATE,'','',SYSDATE,NULL)
            """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getMemberId());
            pstmt.setString(2, dto.getMemberNm());
            pstmt.setString(3, dto.getPasswd());
            pstmt.setString(4, dto.getBirth());
            pstmt.setString(5, dto.getEmail());
            pstmt.setString(6, dto.getTelNo());
            pstmt.setString(7, dto.getNickNm());
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, null);
        }
        return result;
    }
    /**회원가입용 전화번호 중복체크 (서버검증용) */
    public boolean isTelDuplicateForJoin(String telNo) {
        boolean result = false;
        try {
            conn = OraConn.getConnection();
            String sql =
                "SELECT COUNT(*) FROM MEMBER_BAS " +
                "WHERE TRIM(TEL_NO)=TRIM(?) " +
                "  AND NVL(MEMBER_STAT_CD,'1') <> '0'"; // 탈퇴(0) 제외
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, telNo == null ? "" : telNo.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) result = rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            OraConn.allClose(conn, pstmt, rs);
        }
        return result; // true면 중복(가입 불가)
    }
    
}