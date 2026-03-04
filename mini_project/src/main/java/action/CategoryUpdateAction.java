package action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AdminCategoryDAO;
import dao.AdminDashboardStatsDAO;
import dao.AdminJournalReqDAO;
import dao.AdminMemberDAO;
import dto.CategoryDTO;
import service.AdminCategoryServiceImpl;
import service.AdminDashboardServiceImpl;
import service.AdminJournalRequestServiceImpl;
import service.AdminMemberServiceImpl;

public class CategoryUpdateAction implements Action {
//	private final MemberService service;

	private final AdminDashboardServiceImpl 	 dashboardService;
	private final AdminMemberServiceImpl         memberService;
	private final AdminCategoryServiceImpl 		 categoryService;
	private final AdminJournalRequestServiceImpl JournalReqService;

	// 생성자
	public CategoryUpdateAction() {
        // DAO 생성 후 Service 생성자에 전달
//        MemberRepository memberRepositoryDao = new MemberRepositoryImpl();
//        this.service = new MemberServiceImpl(memberRepositoryDao);
        
        AdminDashboardStatsDAO dashboardStatsDAO = new AdminDashboardStatsDAO();
        this.dashboardService = new AdminDashboardServiceImpl(dashboardStatsDAO);
        
        AdminMemberDAO memberDAO = new AdminMemberDAO();
        this.memberService = new AdminMemberServiceImpl(memberDAO);
        
        AdminCategoryDAO categoryDAO = new AdminCategoryDAO();
        this.categoryService = new AdminCategoryServiceImpl(categoryDAO);
        
        AdminJournalReqDAO journalReqDAO = new AdminJournalReqDAO();
        this.JournalReqService = new AdminJournalRequestServiceImpl(journalReqDAO);

	}

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("===== modifyCategory 호출됨 =====");

		String [] categoryIds = request.getParameterValues("categoryId");
		String [] categoryNms = request.getParameterValues("categoryNm");
		String [] upCategoryIds = request.getParameterValues("upCategoryId");
		String [] deleteCategoryIds = request.getParameterValues("deleteCategoryIds");
		
		if(categoryIds == null || categoryNms == null || upCategoryIds == null)
		{
		 	request.getSession().setAttribute("message", "선택된 카테고리가 없습니다.");
		 	return "redirect:/memberBasStats";
		}
		
		System.out.println("----------------------------------------------");
		System.out.println("categoryIds.length : " + categoryIds.length);
		
		for (int i = 0; i < categoryIds.length; i++) {
			System.out.println("[" + i + "] : [" + categoryIds[i] + "]");
		}
		
		
		// 각 row마다 처리
		for (int i = 0; i < categoryIds.length; i++) {
			String cid = categoryIds[i];
            String cnm = categoryNms[i];
            String upCid = upCategoryIds[i];
            

    		// delete 체크 여부 확인
            String delYn = "N";
            if (deleteCategoryIds != null && deleteCategoryIds.length > i) {
                delYn = "Y".equals(deleteCategoryIds[i]) ? "Y" : "N";
            }

    		System.out.println("categoryId = [" + cid + "]");
    		System.out.println("categoryNm = [" + cnm + "]");
    		System.out.println("upCategoryId = [" + upCid + "]");
    		System.out.println("deleteCategoryId = [" + delYn + "]");

    		
            CategoryDTO dto = new CategoryDTO(cid, cnm, upCid);
            dto.setDelYn(delYn);

            System.out.println("카테고리 처리: ID=" + cid + ", 이름=" + cnm + ", 상위ID=" + upCid + ", 삭제=" + delYn);

            // 삭제 요청일 경우 하위 카테고리 존재 여부 확인
            if ("Y".equals(delYn)) {
            	// 3.3 삭제대상의 하위 카테고리 존재여부 검증
                int validDeletableCategory = categoryService.validDeletableCategory(dto);
                
                if (validDeletableCategory > 0) {
                    request.getSession().setAttribute("messageCategory",
                            "카테고리ID=[" + cid + "] 하위 카테고리 존재하므로 삭제 불가!!");
                    continue; // 삭제 불가면 다음 row 처리
                }
            }
            
            // DB 업데이트
            int categoryRoleChgResult = categoryService.modifyCategory(dto);
            System.out.println("categoryRoleChgResult: " + categoryRoleChgResult);
            
            System.out.println("============== message ==============");
            System.out.println("카테고리ID = [" + cid + "] 변경 완료");
    		System.out.println("----------------------------------------------");
            
		}
		
		request.getSession().setAttribute("messageCategory", "카테고리 수정/삭제 완료");

        // 조회는 forward, 처리(등록/수정/삭제)는 redirect를 사용해야함.
        // redirect 처리
        return "redirect:/memberBasStats";
	}

}
