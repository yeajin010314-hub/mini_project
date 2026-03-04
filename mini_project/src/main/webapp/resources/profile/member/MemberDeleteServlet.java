package web.member;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MemberDAO2;
import dto.MemberDTO;


//회원 탈퇴
@WebServlet("/member/delete")
public class MemberDeleteServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        HttpSession session = req.getSession();
        MemberDTO user = (MemberDTO)session.getAttribute("loginUser");

        MemberDAO2 dao = MemberDAO2.getInstance();
        dao.deleteMember(user.getMemberId());

        session.invalidate(); // ⭐ 핵심

        res.sendRedirect("/main/mainForm.jsp");
    }
}
