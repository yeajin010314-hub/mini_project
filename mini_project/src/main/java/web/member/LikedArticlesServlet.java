/* =========================================================
 * 1) /src/web/member/LikedArticlesServlet.java
 *    - 내가 좋아요한 기사 목록(JSON)
 *    - 모달 컬럼: 기사넘버 | ID(작성자) | 제목 | 작성일
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

//
@WebServlet("/member/likedArticles")
public class LikedArticlesServlet extends HttpServlet {

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

        // DAO 조회
        List<ArticleDTO> list = MypageArticleDivDAO.getInstance().getLikedArticles(memberId);

        // JSON 응답 생성
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < list.size(); i++) {
            ArticleDTO a = list.get(i);
            if (i > 0) sb.append(",");

            sb.append("{")
              .append("\"articleNo\":").append(a.getArticleNo()).append(",")
              // 좋아요 목록에서는 작성자ID를 보여주면 되니까 ARTICLE.MEMBER_ID를 그대로 사용
              .append("\"memberId\":\"").append(escapeJson(a.getMemberId())).append("\",")
              .append("\"subject\":\"").append(escapeJson(a.getSubject())).append("\",")

              // regDt가 Timestamp면 toString으로 문자열화
              .append("\"regDt\":\"").append(escapeJson(a.getRegDt() == null ? "" : a.getRegDt().toString())).append("\"")
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