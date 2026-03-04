package service;

import java.util.List;

import dao.AdminJournalReqDAO;
import dto.MemberBasDTO;

public class AdminJournalRequestServiceImpl {
	// 핃드(관리자 대시보드)
	private final AdminJournalReqDAO adminJournalReqDAO;

	// 생성자(관리자 대시보드)
    public AdminJournalRequestServiceImpl(AdminJournalReqDAO adminJournalReqDAO) {
        this.adminJournalReqDAO = adminJournalReqDAO;
    }

	// 4.1 기자 등록요청 조회
	public List<MemberBasDTO> getJournalRegRequestList(int pagenum, int pagesize) {
		return adminJournalReqDAO.selectJournalRegRequestList(pagenum, pagesize);
	}

    // 4.2 기자등록 요청 전체 건수 조회
    public int getJournalRegTotalCount() {
		return adminJournalReqDAO.selectJournalRegTotalCount();
	}
    
	// 4.3 기자 등록 상태변경
	public int changeMemberJournalCdStatus(MemberBasDTO dto) {
		return adminJournalReqDAO.updateMemberJournalCdStatus(dto);
	}

}
