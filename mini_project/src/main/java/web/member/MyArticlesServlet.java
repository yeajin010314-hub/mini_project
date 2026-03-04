/* =========================================================
 * 2) /src/web/member/MyArticlesServlet.java
 *    - 내가 작성한 글 목록(JSON)
 *    - 기자(2) / 관리자(3)만 허용
 *    - 모달 컬럼: 기사넘버 | 제목 | 작성일 | 조회수
 * ========================================================= */
package web.member;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MypageArticleDivDAO;
import dto.ArticleDTO;

@WebServlet("/member/myArticles")
public class MyArticlesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        req.setCharacterEncoding("UTF-8");
        res.setContentType("application/json; charset=UTF-8");

        // 로그인 체크
        HttpSession session = req.getSession(false);
        String memberId = (session == null) ? null : (String) session.getAttribute("memberId");

        if (memberId == null || memberId.trim().isEmpty()) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            res.getWriter().print("NOLOGIN");
            return;
        }

        // ✅ 서버에서도 기자/관리자 체크 (프론트만 막으면 우회됨)
        MypageArticleDivDAO mdao = MypageArticleDivDAO.getInstance();
        String memberStatCd = mdao.getMemberStatCd(memberId);
        memberStatCd = (memberStatCd == null) ? "" : memberStatCd.trim();

        // 기자(2) 또는 관리자(3)만 가능
        if (!"2".equals(memberStatCd) && !"3".equals(memberStatCd)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            res.getWriter().print("FORBIDDEN");
            return;
        }

        // DAO 조회
        List<ArticleDTO> list = MypageArticleDivDAO.getInstance().getMyArticles(memberId);

        // JSON 응답 생성
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < list.size(); i++) {
            ArticleDTO a = list.get(i);
            if (i > 0) sb.append(",");

            sb.append("{")
              .append("\"articleNo\":").append(a.getArticleNo()).append(",")
              .append("\"subject\":\"").append(escapeJson(a.getSubject())).append("\",")

              .append("\"regDt\":\"").append(escapeJson(a.getRegDt() == null ? "" : a.getRegDt().toString())).append("\",")

              .append("\"cnt\":").append(a.getCnt())
              .append("}");
        }

        sb.append("]");
        res.getWriter().print(sb.toString());
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}