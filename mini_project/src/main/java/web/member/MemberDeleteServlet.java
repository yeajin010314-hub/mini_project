package web.member;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.LeaveDAO;
import dao.MypageDAO;
import dto.MemberBasDTO;


//멤버딜리트서블릿->로그인체크->비번확인->탈퇴처리(서버에는 코드0번으로 남음)->상태/탈퇴일 업데이트->세션 무효화
@WebServlet("/member/delete")
public class MemberDeleteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = res.getWriter();

        // 1) 로그인 체크
        HttpSession session = req.getSession(false);
        if (session == null) {
            out.print("NOLOGIN");
            return;
        }

        MemberBasDTO user = (MemberBasDTO) session.getAttribute("loginUser");
        if (user == null) {
            out.print("NOLOGIN");
            return;
        }

        // 2) 비밀번호 파라미터 받기
        String pw = req.getParameter("passwd");
        if (pw == null || pw.trim().isEmpty()) {
            out.print("EMPTY_PW");
            return;
        }

        String memberId = user.getMemberId();

        // ✅ 비번 확인은 MypageDAO
        MypageDAO myDao = MypageDAO.getInstance();
        boolean okPw = myDao.checkPassword(memberId, pw.trim());
        if (!okPw) {
            out.print("WRONG_PW");
            return;
        }

        // ✅ 탈퇴 처리는 LeaveDAO
        LeaveDAO leaveDao = LeaveDAO.getInstance();
        int updated = leaveDao.leaveMember(memberId);
        
        if (updated == 1) {
            // 5) 세션 무효화(로그아웃)
            session.invalidate();
            out.print("OK");
        } else {
            out.print("FAIL");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // 직접 URL 접근 방지(선택)
        res.sendRedirect(req.getContextPath() + "/member/mypage/mypageForm.jsp");
    }
}
