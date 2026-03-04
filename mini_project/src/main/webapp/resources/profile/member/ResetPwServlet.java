package web.member;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MemberDAO2;

@WebServlet("/member/resetPw")
public class ResetPwServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	
    	req.setCharacterEncoding("UTF-8");
    	res.setContentType("text/plain; charset=UTF-8");
    	
    	String memberId = req.getParameter("memberId");
    	String email = req.getParameter("email");
    	String newPw = req.getParameter("newPw");
    	String newPw2 = req.getParameter("newPw2");
    	
    	memberId = (memberId == null) ? "" : memberId.trim();
    	email = (email == null) ? "" : email.trim();
    	newPw = (newPw == null) ? "" : newPw.trim();
        newPw2 = (newPw2 == null) ? "" : newPw2.trim();

         if (memberId.isEmpty() || email.isEmpty() || newPw.isEmpty() || newPw2.isEmpty()) {
             res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             res.getWriter().print("EMPTY");
             return;
         }
         if (newPw.length() < 4 || newPw.length() > 20) {
             res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             res.getWriter().print("INVALID_LEN");
             return;
         }
         if (!newPw.equals(newPw2)) {
             res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
             res.getWriter().print("MISMATCH");
             return;
         }

         MemberDAO2 dao = MemberDAO2.getInstance();

         // 1) 아이디+이메일이 실제로 존재하는지 확인
         boolean ok = dao.verifyIdEmail(memberId, email);
         if (!ok) {
             res.setStatus(HttpServletResponse.SC_OK);
             res.getWriter().print("NOTFOUND");
             return;
         }

         // 2) 비밀번호 업데이트
         int updated = dao.updatePassword(memberId, newPw);
         if (updated == 1) {
             res.setStatus(HttpServletResponse.SC_OK);
             res.getWriter().print("OK");
         } else {
             res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
             res.getWriter().print("FAIL");
         }
     }
 }