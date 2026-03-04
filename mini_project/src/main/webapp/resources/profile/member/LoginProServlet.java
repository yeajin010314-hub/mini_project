package web.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.MemberDAO2;
import dto.MemberDTO;

@WebServlet("/member/loginPro")
public class LoginProServlet extends HttpServlet {

	
	/*로그인프로서블릿 기능(컨트롤러)
	-로그인 인증처리 + 세션세팅 + 아이디저장(세션기반) + 로그인 후 리다이렉트
	
	-/member/loginPrp로 들어온 로그인 Post 요청을 처리
	-DAO로 아이디/비번 검증 
	-성공하면 세션에 로그인 사용자 정보 세팅
	-실해하면 로그인폼으로 에러코드 붙여서 리다이렉트
	-로그인 후 원래 가려던 페이지가 있으면 그쪽으로 보냄	
	*/
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("memberId");
        String pw = request.getParameter("passwd");
        String autoid = request.getParameter("autoid"); // 체크 시 "1" (또는 null)

        //서버검증 (빈값)
        if (id == null || id.trim().isEmpty() || pw == null || pw.trim().isEmpty()) {
        	response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=empty");
        	return;
        }
        
        // 1) 로그인 체크
        MemberDAO2 dao = MemberDAO2.getInstance();
        MemberDTO user = dao.login(id.trim(), pw.trim());
        
        
       
        // 로그인 실패
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp?err=1");
            return;
        }

        // ) 세션 생성/획득
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", user);
        
        
        //해당부분 팀원과 맞추기
        session.setAttribute("memberId", user.getMemberId());
        

        // ) 아이디 저장(세션기반)
        if("1".equals(autoid) || "on".equalsIgnoreCase(autoid)) {
        	session.setAttribute("savedId",id.trim());
        	session.setAttribute("autoCheck", 1);
        }else {
        	session.removeAttribute("savedId");
        	session.removeAttribute("autoCheck");
        }
       

        // 5) 로그인 후 원래 가려던 곳으로 보내기 (인터셉트용)
        String redirect = (String) session.getAttribute("redirectAfterLogin");
        if (redirect != null) {
            session.removeAttribute("redirectAfterLogin");
            response.sendRedirect(redirect);
            return;
        }

        // 6) 기본 이동
        response.sendRedirect(request.getContextPath() + "/main/mainForm.jsp");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    	throws IOException {
    	response.sendRedirect(request.getContextPath()+"/member/login/loginForm2.jsp");
    }
}
