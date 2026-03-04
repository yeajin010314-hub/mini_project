package service;

import java.util.List;

import dao.AdminDashboardStatsDAO;
import dto.AdminMemberStatsDTO;

public class AdminDashboardServiceImpl {
	// 핃드(관리자 대시보드)
	private final AdminDashboardStatsDAO adminDashboardStatsDAO;

	// 생성자(관리자 대시보드)
    public AdminDashboardServiceImpl(AdminDashboardStatsDAO adminDashboardStatsDAO) {
        this.adminDashboardStatsDAO = adminDashboardStatsDAO;
    }

    /* 메서드 */
    // 회원 통계 조회(record DTO사용)
    public List<AdminMemberStatsDTO> getMemberStatsList() {
		return adminDashboardStatsDAO.selectMemberStatsList();
	}
	
	// 기자 등록요청 통계 조회(record DTO사용)
	public List<AdminMemberStatsDTO> getReqReportStatList() {
		return adminDashboardStatsDAO.selectReqReportStatList();
	}

	// 카테고리 통계 조회(record DTO사용)
	public List<AdminMemberStatsDTO> getCategoryStatList() {
		return adminDashboardStatsDAO.selectCategoryStatList();
	}

	// 상태코드 조회(record DTO)
	public List<AdminMemberStatsDTO> getStatByCode(String cd) {
		return adminDashboardStatsDAO.selectStatByCode(cd);
	}

}
