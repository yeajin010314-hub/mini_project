package web.member;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MypageDAO;
import dto.MemberBasDTO;

//리포터서블릿-기자 신청/취소->DAO 결과코드에 따라 응답
@WebServlet("/member/reporterApply.do")
public class ReporterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	
    	request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);
        MemberBasDTO user = (session == null) ? null : (MemberBasDTO) session.getAttribute("loginUser");

        if (user == null) {
            response.getWriter().print("NOLOGIN");
            return;
        }

        String memberId = user.getMemberId();
        String mode = request.getParameter("mode"); // cancel 여부
        String reason = request.getParameter("reason");
        reason = (reason == null) ? "" : reason.trim();

        MypageDAO dao = MypageDAO.getInstance();

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
        case "OK": response.getWriter().print("APPLY_OK"); break; //신청성공
        case "ALREADY_APPLIED": response.getWriter().print("ALREADY_APPLIED"); break; //이미 신청(대기)상태
        case "ALREADY_REPORTER": response.getWriter().print("ALREADY_REPORTER"); break; //이미 기자상태
        case "ON_HOLD": response.getWriter().print("ON_HOLD"); break; //보류상태
        case "TOOEARLY": response.getWriter().print("TOOEARLY"); break; //가입일로부터 7일 이내(신청불가)
        default: response.getWriter().print("FAIL"); //DB오류/실패
      }
    }
}
