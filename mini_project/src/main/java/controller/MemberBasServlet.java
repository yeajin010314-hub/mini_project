package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.CategoryUpdateAction;
import action.JourReasonCdUpdateAction;
import action.MemberBasStatsAction;
import action.MemberBasUpdateAction;

/**
 * Servlet implementation class MemberBasServlet
 */
@WebServlet( {"/memberBasStats","/memberBasUpdate","/categoryUpdate","/jourReasonCdUpdate"} )
public class MemberBasServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // ================== forward ==================
        // 3. JSP로 forward
//      req.getRequestDispatcher("/dashboard/dashBoard.jsp").forward(request, response);

//		request.setCharacterEncoding("UTF-8");
		process(request, response);
}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		doGet(request, response);
//		request.setCharacterEncoding("UTF-8");
		process(request, response);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String path = uri.substring(ctx.length()); // /memberBasStats or /memberBasUpdate

        System.out.println("uri : [" + uri + "]");
        System.out.println("ctx : [" + ctx + "]");
        System.out.println("path : [" + path + "]");
        
        Action action = null;

        switch (path) {
            case "/memberBasStats":
            	// 화면 조회
                action = new MemberBasStatsAction();
                break;
            case "/memberBasUpdate":
            	// Update 처리
                action = new MemberBasUpdateAction();
                break;
            case "/categoryUpdate":
            	// Update 처리
                action = new CategoryUpdateAction();
//                action = null;
                break;
            case "/jourReasonCdUpdate":
            	// Update 처리
                action = new JourReasonCdUpdateAction();
//                action = null;
                break;
        }

        if (action != null) {
            String view = action.execute(req, resp);
            System.out.println("action view : " + view);
            
            // ================== forward ==================
            // 3. JSP로 forward
            if (view.startsWith("redirect:")) {
                String redirectPath = view.substring("redirect:".length());
                
                System.out.println("redirectPath TRUE : " + redirectPath );
                
                resp.sendRedirect(req.getContextPath() + redirectPath);
                return;
                
            } else {
                System.out.println("=== redirectPath FALSE === : " + view );
                req.getRequestDispatcher(view).forward(req, resp);
            }

        }else if ("/memberBasStats".equals(path)) { 
        	req.getRequestDispatcher("/dashboard/dashBoard.jsp").forward(req, resp);
        }else {
            // 예외 처리: 정의되지 않은 path
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "잘못된 요청입니다.");
        }
    }
}
