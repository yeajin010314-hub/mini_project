package web.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/*로그아웃 서블릿 기능
-/member/logout 요청을 받아 로그아웃 처리
-로그인 관련 세션 정보는 전부 제거
-아이디 저장은 유지
-로그아웃 후 메인화면으로 이동

*/

@WebServlet("/member/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

    	//false-> 세션 없으면 새로 만들지 않음.
        HttpSession session = request.getSession(false);

        String memberId = null;
        Integer autoCheck = null;

        if (session != null) {
            // 로그아웃 후 (세션 날리기 전에) 남길 정보만 빼두기 
        	memberId = (String) session.getAttribute("savedId");
            autoCheck = (Integer) session.getAttribute("autoCheck");

            // 세션 전체 날리기(로그인 정보 포함)
            session.invalidate();
        }

        // 새 세션 만들어서 memberId만 복구 (아이디 저장 유지)(아이디 저장 체크한 사람에 한해서)
        if (memberId != null && autoCheck != null && autoCheck == 1) {
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("savedId", memberId);
            newSession.setAttribute("autoCheck", 1);
        }

        // 메인화면으로 이동
        response.sendRedirect(request.getContextPath() + "/main");
    }
}
