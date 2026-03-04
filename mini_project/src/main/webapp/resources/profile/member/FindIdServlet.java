package web.member;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MemberDAO2;


//아이디 찾기 서블릿

@WebServlet("/member/findId")
public class FindIdServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=UTF-8");

        String name = req.getParameter("name");
        String birth = req.getParameter("birth"); // 프론트에서 YYYYMMDD로 들어옴
        String email = req.getParameter("email");

        name = (name == null) ? "" : name.trim();
        birth = (birth == null) ? "" : birth.trim();
        email = (email == null) ? "" : email.trim();

        if (name.isEmpty() || birth.isEmpty() || email.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().print("EMPTY");
            return;
        }
        if (birth.length() != 8) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.getWriter().print("INVALID_BIRTH");
            return;
        }

        MemberDAO2 dao = MemberDAO2.getInstance();
        String memberId = dao.findMemberId(name, birth, email);

        res.setStatus(HttpServletResponse.SC_OK);

        if (memberId == null || memberId.trim().isEmpty()) {
            res.getWriter().print("NOTFOUND");
        } else {
            res.getWriter().print("OK|" + MemberDAO2.maskId(memberId));
        }

    }
}