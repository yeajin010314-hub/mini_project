package web.member;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.LoginDAO;


/*아이디 찾기 서블릿 / 아이디 찾기 전용 컨트롤러
-사용자가 입력한 이름 + 생년월일 + 이메일로 DB아이디 조회
-결과를 텍스트 응답으로 반환

특징: 순수 응답용 서블릿 jsp이동 및 화면 렌더링 x
(AJAX/응답 전용 컨트롤러)
*/

@WebServlet("/member/findId")
public class FindIdServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=UTF-8");

        //파라미터 받기
        String name = req.getParameter("name");
        String birth = req.getParameter("birth"); // 프론트에서 YYYYMMDD로 들어옴
        String email = req.getParameter("email");

        name = (name == null) ? "" : name.trim();
        birth = (birth == null) ? "" : birth.trim();
        email = (email == null) ? "" : email.trim();

        //필수값 누락검사-> 입력값 하나라도 빠지면 400에러 줌
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

        LoginDAO dao = LoginDAO.getInstance();
        String memberId = dao.findMemberId(name, birth, email);

        res.setStatus(HttpServletResponse.SC_OK);

        if (memberId == null || memberId.trim().isEmpty()) {
            res.getWriter().print("NOTFOUND");
        } else {
            res.getWriter().print("OK|" + LoginDAO.maskId(memberId));
        }

    }
}