package web.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/member/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        String memberId = null;
        Integer autoCheck = null;

        if (session != null) {
            // 로그아웃해도 아이디 저장은 남길 거면 미리 빼두고
        	memberId = (String) session.getAttribute("savedId");
            autoCheck = (Integer) session.getAttribute("autoCheck");

            // 세션 전체 날리기(로그인 정보 포함)
            session.invalidate();
        }

        // 새 세션 만들어서 memberId만 복구 (아이디 저장 유지)
        if (memberId != null && autoCheck != null && autoCheck == 1) {
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("savedId", memberId);
            newSession.setAttribute("autoCheck", 1);
        }

        // 메인 or 로그인 화면으로 이동 (원하는 대로)
        response.sendRedirect(request.getContextPath() + "/main/mainForm.jsp");
    }
}
