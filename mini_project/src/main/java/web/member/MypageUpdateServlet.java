package web.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MypageDAO;
import dao.JoinDAO;
import dto.MemberBasDTO;

/*마이페이지업데이트서블릿->로그인체크->닉네임 중복검사->DB업뎃->세션 DTO동기화->OK반환(닉네임이 변경되었습니다)
마이페이지 닉네임 수정(로그인 사용자 닉네임 변경 처리 컨트롤러)
화면 이동(JSP 렌더링) ❌
1)로그인 체크(세션)
2)닉네임 파라미터 수신 + 검증
3)닉네임 중복 검사(내 아이디 제외)
4)DB업데이트
5)세션 DTO도 갱신

*/




@WebServlet("/member/update")
public class MypageUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);
        MemberBasDTO user = (session == null) ? null : (MemberBasDTO) session.getAttribute("loginUser");

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

        MypageDAO dao = MypageDAO.getInstance();
        JoinDAO joindao = JoinDAO.getInstance();//이부분 사용법 맞는지 확인
        
        // 3) 중복 검사 (내 아이디 제외)
        if (joindao.isNickDuplicate(newNick, myId)) {
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
        // DB만 바꾸면 세션에 남아있는 닉네임이 예전 값이라 화면이 꼬일 수 있는데,
        // 이걸 세션까지 같이 업데이트해서 일관성 유지
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
