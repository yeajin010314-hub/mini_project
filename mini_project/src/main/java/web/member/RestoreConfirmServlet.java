package web.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


//리스토어컨펌서블릿 - 복구대상 회원으로 판정된 사용자만 restoreConfirm.jsp화면을 볼 수 있도록 제한하는 서블릿
//직접 URI 접근 차단
//정상 로그인 흐름 거친 사람만 통과


@WebServlet("/member/restoreConfirm")
public class RestoreConfirmServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("restoreMemberId") == null) {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=restore_no_session");
            return;
        }

        request.getRequestDispatcher("/member/login/restoreConfirm.jsp")
               .forward(request, response);
    }
}
