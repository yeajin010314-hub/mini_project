package web.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.JoinDAO;

/*아아디, 닉네임 중복확인 전용 컨트롤러-> 아이디/닉네임 중복체크 API (type, value) → DUP/OK 텍스트 반환
  화면(JSP)으로 이동 ❌
 */
/*
 1) get post 둘 다 처리
 2) 어떤 중복확인인지 구분(type) + 검사값(value)
   String type = req.getParameter("type");
   String value = req.getParameter("value");
 3) DAO 호출해서 중복 여부 판단 
 */


@WebServlet("/member/check")
public class MemberCheckServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		process(req, resp);
	}
	
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	process(req,resp);
    }
    	private void process(HttpServletRequest req, HttpServletResponse resp)
    			throws IOException {
    		System.out.println("=== MemberCheckServlet START ===");
    		
    	req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/plain;charset=UTF-8");
    	
        String type = req.getParameter("type"); // memberId or nick
        String value = req.getParameter("value");
        
        System.out.println("type = " + type);
        System.out.println("value = [" + value + "]");

        JoinDAO dao = JoinDAO.getInstance();
        boolean duplicate = false;

        if ("memberId".equals(type)) {
        	 System.out.println(">> ID duplicate check start");
            duplicate = dao.isIdDuplicate(value);
            
        } else if ("nickNm".equals(type)) {
        	System.out.println(">> NICK duplicate check start");
            duplicate = dao.isNickDuplicateForJoin(value);
        }
        System.out.println("duplicate result = " + duplicate);

        resp.getWriter().print(duplicate ? "DUP" : "OK");
        System.out.println("response = " + (duplicate ? "DUP" : "OK"));
        System.out.println("=== MemberCheckServlet END ===");
    }
}
