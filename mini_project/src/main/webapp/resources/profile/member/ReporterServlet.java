package web.member;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MemberDAO2;
import dto.MemberDTO;

@WebServlet("/member/reporterApply.do")
public class ReporterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	
    	request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);
        MemberDTO user = (session == null) ? null : (MemberDTO) session.getAttribute("loginUser");

        if (user == null) {
            response.getWriter().print("NOLOGIN");
            return;
        }

        String memberId = user.getMemberId();
        String mode = request.getParameter("mode"); // cancel 여부
        String reason = request.getParameter("reason");
        reason = (reason == null) ? "" : reason.trim();

        MemberDAO2 dao = MemberDAO2.getInstance();

        /* ======================
           신청 취소
           ====================== */
        if ("cancel".equals(mode)) {
            String result = dao.cancelJournal(memberId);
            response.getWriter().print(
                "OK".equals(result) ? "CANCEL_OK" : result
            );
            return;
        }

        /* ======================
           신청 / 재신청
           ====================== */
        if (reason.isEmpty()) {
            response.getWriter().print("EMPTY_REASON");
            return;
        }

        // 반드시 applyJournal을 타야 7일 제한이 적용됨
        String result = dao.applyJournal(memberId, reason);

        switch (result) {
            case "OK":
                response.getWriter().print("APPLY_OK");
                break;
            case "ALREADY": // 대기(1) or 승인(2)
                response.getWriter().print("ALREADY_APPLIED");
                break;
            case "TOOEARLY":
                response.getWriter().print("TOOEARLY");
                break;
            default:
                response.getWriter().print("FAIL");
        }
    }
}
