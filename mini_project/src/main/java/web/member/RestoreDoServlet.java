package web.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.LeaveDAO;

//리스토어두서블릿-탈퇴회원 복구 실행 서블릿
@WebServlet("/member/restoreDo")
public class RestoreDoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=restore_no_session");
            return;
        }

        String memberId = (String) session.getAttribute("restoreMemberId");
        if (memberId == null || memberId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=restore_no_session");
            return;
        }

        String email = request.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/member/restoreConfirm?err=email_empty");
            return;
        }

        LeaveDAO dao = LeaveDAO.getInstance();

        // 안전하게 다시 3개월 체크
        if (!dao.canRestoreWithin3Months(memberId)) {
            session.removeAttribute("restoreMemberId");
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=leaved_expired");
            return;
        }

        // 이메일 검증
        boolean okEmail = dao.verifyRestoreEmail(memberId, email.trim());
        if (!okEmail) {
            response.sendRedirect(request.getContextPath() + "/member/restoreConfirm?err=email_wrong");
            return;
        }

        // 복구 실행
        int updated = dao.restoreMember(memberId);
        session.removeAttribute("restoreMemberId");

        if (updated == 1) {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?restored=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=restore_fail");
        }
    }
}
