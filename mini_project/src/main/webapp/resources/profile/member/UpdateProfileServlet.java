package web.member;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MemberDAO2;
import dto.MemberDTO;

@WebServlet("/member/updateProfile")
public class UpdateProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

    	req.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=UTF-8"); // ✅ AJAX용

        HttpSession session = req.getSession();
        MemberDTO user = (MemberDTO) session.getAttribute("loginUser");

        if (user == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().print("NOLOGIN");
            return;
        }

        String email = req.getParameter("email");
        if (email == null) email = "";
        email = email.trim();

        // ✅ 최소 검증(서버에서도 한번은 걸러)
        if (email.isEmpty() || !email.contains("@")) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().print("INVALID_EMAIL");
            return;
        }

        MemberDAO2 dao = MemberDAO2.getInstance();
        boolean ok = dao.updateEmail(user.getMemberId(), email); // ✅ 이메일만 업데이트

        if (!ok) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().print("FAIL");
            return;
        }

        // ✅ 세션 동기화(이메일만)
        user.setEmail(email);
        session.setAttribute("loginUser", user);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().print("OK");
    }
}
