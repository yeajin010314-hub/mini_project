package web.member;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MypageDAO;
import dto.MemberBasDTO;

/*비밀번호 재설정 서블릿 컨트롤러
  로그인한 사용자만 대상
  현재 비번 확인
  비번 변경
  *jsp 이동 없음
 AJAX용 “API 컨트롤러” 타입*/
/*
 mode 값에 따라 두 가지 기능을 제공.
mode=check는 현재 비밀번호가 맞는지 확인
mode=change는 현재 비번 검증 → 새 비번 규칙 검사 
→ 새비번 확인일치검사까지 통과-> DB에 업데이트해서 변경
*/
@WebServlet("/member/changePw")
public class ChangePwServlet extends HttpServlet {
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("text/plain; charset=UTF-8");

        HttpSession session = req.getSession();
        MemberBasDTO user = (MemberBasDTO) session.getAttribute("loginUser");
        
        //로그인했는지 먼저 확인
        if (user == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().print("NOLOGIN");
            return;
        }

        String mode = req.getParameter("mode");
        mode = (mode == null) ? "" : mode.trim();

        MypageDAO dao = MypageDAO.getInstance();

        // =========================
        // 1) 현재 비번 검증만
        // mode=check
        // =========================
        if ("check".equals(mode)) {
            String curPw = req.getParameter("currentPw");
            curPw = (curPw == null) ? "" : curPw.trim();

            if (curPw.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().print("EMPTY");
                return;
            }

            boolean ok = dao.checkPassword(user.getMemberId(), curPw);
            res.setStatus(HttpServletResponse.SC_OK);
            res.getWriter().print(ok ? "OK" : "WRONG");
            return;
        }

        // =========================
        // 2) 비밀번호 변경
        // mode=change
        // =========================
        if ("change".equals(mode)) {
            String curPw = req.getParameter("currentPw");
            String newPw = req.getParameter("newPw");
            String newPw2 = req.getParameter("newPw2");

            curPw = (curPw == null) ? "" : curPw.trim();
            newPw = (newPw == null) ? "" : newPw.trim();
            newPw2 = (newPw2 == null) ? "" : newPw2.trim();

            if (curPw.isEmpty() || newPw.isEmpty() || newPw2.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().print("EMPTY");
                return;
            }

            // 현재 비번 검증
            boolean ok = dao.checkPassword(user.getMemberId(), curPw);
            if (!ok) {
                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().print("WRONG");
                return;
            }

            // 새 비번 길이(프론트랑 동일 룰)
            if (newPw.length() < 4 || newPw.length() > 20) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().print("INVALID_LEN");
                return;
            }

            // 새 비번 확인
            if (!newPw.equals(newPw2)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                res.getWriter().print("MISMATCH");
                return;
            }

            int updated = dao.updatePassword(user.getMemberId(), newPw);
            if (updated == 1) {
                res.setStatus(HttpServletResponse.SC_OK);
                res.getWriter().print("OK");
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.getWriter().print("FAIL");
            }
            return;
        }

        // =========================
        // mode 누락/이상
        // =========================
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().print("INVALID_MODE");
    }
}