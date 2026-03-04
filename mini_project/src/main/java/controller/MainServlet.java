package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import action.MainLogoutAction;
import action.MainProcess;

@WebServlet({"/main","/logout"})
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		process(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String path = uri.substring(ctx.length()); // /main or /category.do

        System.out.println("uri : [" + uri + "]");
        System.out.println("ctx : [" + ctx + "]");
        System.out.println("path : [" + path + "]");
        
        Action action = null;

        switch (path) {
            case "/main":
            	// 화면 조회
                action = new MainProcess();
                break;
            case "/logout":
                action = new MainLogoutAction();
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

        } else if ("/main".equals(path)) { 
        		req.getRequestDispatcher("/main").forward(req, resp);
        } else {
            // 예외 처리: 정의되지 않은 path
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "잘못된 요청입니다.");
        }
    }

}
