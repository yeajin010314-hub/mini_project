package web.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MemberDAO2;
import dto.MemberDTO;

@WebServlet("/member/update")
public class MypageUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);
        MemberDTO user = (session == null) ? null : (MemberDTO) session.getAttribute("loginUser");

        // 1) 로그인 체크
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().print("NOLOGIN");
            return;
        }

        // 2) 파라미터
        String newNick = request.getParameter("nickNm");
        newNick = (newNick == null) ? "" : newNick.trim();

        if (newNick.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().print("EMPTY");
            return;
        }

        // (선택) 길이 제한: 프론트랑 맞추기 (2~6)
        if (newNick.length() < 2 || newNick.length() > 6) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().print("INVALID_LEN");
            return;
        }

        String myId = user.getMemberId();

        MemberDAO2 dao = MemberDAO2.getInstance();

        // 3) 중복 검사 (내 아이디 제외)
        if (dao.isNickDuplicate(newNick, myId)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
            response.getWriter().print("DUP");
            return;
        }

        // 4) DB 업데이트
        int updated = dao.updateNick(myId, newNick);
        if (updated <= 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().print("FAIL");
            return;
        }

        // 5) 세션 갱신
        user.setNickNm(newNick);
        session.setAttribute("loginUser", user);

        // 6) 성공 응답
        response.getWriter().print("OK");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().print("METHOD_NOT_ALLOWED");
    }
}
