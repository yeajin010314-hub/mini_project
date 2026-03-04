package web.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MemberDAO2;
import dto.MemberDTO;

@WebServlet("/member/join")
public class JoinServlet extends HttpServlet {

	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

/*조인서블릿 기능
 * 1. 회원가입 입력값 수신
 * 2. 필수값 누락 방지(서버 검증)
 * 3. 전화번호 형식 검증(하이픈 제거 + 숫자만 + 10~11자
 * 4. DB 기준 아이디/닉네임 중복 체크(서버 재검증)
 * 5. DTO로 데이터 전달
 * 6. 가입 성공/실패 처리
 * 		-가입 성공=>로그인 페이지 이동
 * 		-가입 실패=>alert 후 뒤로가기    	
 */
    	
        /* ================= 1. 인코딩 ================= */
        request.setCharacterEncoding("UTF-8");

        /* ================= 2. 파라미터 수신 ================= */
        String memberNm = request.getParameter("memberNm");
        String nickNm   = request.getParameter("nickNm");
        String memberId = request.getParameter("memberId");
        String passwd   = request.getParameter("passwd");
        String email    = request.getParameter("email");
        String telNo    = request.getParameter("telNo");
        String birth    = request.getParameter("birth");

        /* ================= 3. 필수값 서버 검증 ================= */
        if(isEmpty(memberNm) || isEmpty(nickNm) || isEmpty(memberId)
                || isEmpty(passwd) || isEmpty(email)) {

            alertAndBack(response, "필수 입력값이 누락되었습니다");
            return;
        }

        /* ================= 4. 전화번호 서버 검증 ================= */
        if (telNo != null) {
            telNo = telNo.replaceAll("-", "");
        }
        
        if(!isEmpty(telNo)) {

            if(!telNo.matches("\\d+")) {
                alertAndBack(response, "전화번호는 숫자만 입력하세요");
                return;
            }

            if(telNo.length() < 10 || telNo.length() > 11) {
                alertAndBack(response, "전화번호 형식이 올바르지 않습니다");
                return;
            }
        }

        /* ================= 5. DAO 획득 ================= */
        MemberDAO2 dao = MemberDAO2.getInstance();

        /* ================= 6. 중복 재검증 ================= */
        if(dao.isIdDuplicate(memberId)) {
            alertAndBack(response, "이미 사용 중인 아이디입니다");
            return;
        }

        if(dao.isNickDuplicate(nickNm, memberId)) {
            alertAndBack(response, "이미 사용 중인 닉네임입니다");
            return;
        }

        /* ================= 7. DTO 생성 ================= */
        MemberDTO dto = new MemberDTO();
        dto.setMemberNm(memberNm);
        dto.setNickNm(nickNm);
        dto.setMemberId(memberId);
        dto.setPasswd(passwd);
        dto.setEmail(email);
        dto.setTelNo(telNo);
        dto.setBirth(birth);

        /* ================= 8. DAO 호출 ================= */
        System.out.println("▶ JoinServlet 진입");
        System.out.println("▶ memberId = " + memberId);
        
        int result = dao.insertMember(dto);
        //나->DAO-> DTO에 담긴 데이터를 DB에 INSERT해라
        System.out.println("JoinServlet insert result=" + result);
        System.out.println("JoinServlet redirect url=" 
        								+ request.getContextPath() 
        									+ "/member/login/loginForm2.jsp");
        
        
        
        /* ================= 9. 결과 처리 ================= */
        if(result > 0) {
        	response.sendRedirect(request.getContextPath() + "/member/login/loginForm2.jsp");

        } else {
            alertAndBack(response, "회원가입 실패");
        }
    }

    /* ================= 공통 메서드 ================= */

    private boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    private void alertAndBack(HttpServletResponse response, String msg)
            throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println(
            "<script>alert('" + msg + "'); history.back();</script>"
        );
    }
    
}
