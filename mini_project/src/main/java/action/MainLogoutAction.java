package action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MainLogoutAction implements Action {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		System.out.println("=========== MainLogout Action ============");

		// ---------------------------------------------------------
		// 로그인 session이 있는지 체크
		// ---------------------------------------------------------
		HttpSession session = request.getSession(false);

		if(session != null){
		    session.invalidate();
		}
		System.out.println("============= logout =============");
		
        // ===============================================================
        // 3. return
        // 조회는 forward, 처리(등록/수정/삭제)는 redirect를 사용해야함.
		// ===============================================================
        // redirect처리
		return "redirect:/main";
	}

}
