package web.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.LoginDAO;
import dto.MemberBasDTO;


//로그인프로서블릿
//로그인 검증->탈퇴계정이면 3개월 내 복구->새 세션으로 아이디 저장->원래 가려던 페이지로 이동
@WebServlet("/member/loginPro")
public class LoginProServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("memberId");
        String pw = request.getParameter("passwd");
        String autoid = request.getParameter("autoid");

        if (id == null || id.trim().isEmpty() || pw == null || pw.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=empty");
            return;
        }

        id = id.trim();
        pw = pw.trim();

        LoginDAO dao = LoginDAO.getInstance();

        // 1) 정상 로그인(탈퇴회원은 dao.login에서 막힘)
        MemberBasDTO user = dao.login(id, pw);

        if (user == null) {
            // 2) 탈퇴 계정인지 확인(아이디/비번이 맞는 탈퇴계정)
            boolean leaved = dao.isLeavedAccount(id, pw);
            if (leaved) {
                boolean canRestore = dao.canRestoreWithin3Months(id);
                if (canRestore) {
                    // 복구 진행을 위해 세션에 복구대상 아이디 저장
                    HttpSession session = request.getSession();
                    session.setAttribute("restoreMemberId", id);

                    // 복구 확인 페이지로 이동
                    response.sendRedirect(request.getContextPath() + "/member/restoreConfirm");
                    return;
                } else {
                    // 탈퇴 3개월 초과
                    response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=leaved_expired");
                    return;
                }
            }

            // 3) 그냥 로그인 실패
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=1");
            return;
        }

        // 4) 세션 세팅
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        session.setAttribute("memberId", user.getMemberId());

        // 5) 아이디 저장(세션기반)
        if ("1".equals(autoid) || "on".equalsIgnoreCase(autoid)) {
            session.setAttribute("savedId", id);
            session.setAttribute("autoCheck", 1);
        } else {
            session.removeAttribute("savedId");
            session.removeAttribute("autoCheck");
        }

        // 6) 원래 가려던 곳
        String redirect = (String) session.getAttribute("redirectAfterLogin");
        if (redirect != null) {
            session.removeAttribute("redirectAfterLogin");
            response.sendRedirect(redirect);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/main");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp");
    }
}
