package service;

import java.util.List;

import dao.AdminMemberDAO;
import dto.MemberBasDTO;
import dto.MemberRoleChange;

public class AdminMemberServiceImpl {
	// 핃드(관리자 대시보드)
	private final AdminMemberDAO adminMemberDAO;

	// 생성자(관리자 대시보드)
    public AdminMemberServiceImpl(AdminMemberDAO adminMemberDAO) {
        this.adminMemberDAO = adminMemberDAO;
    }

	// 회원 전체 조회(일반 DTO 사용)
	public List<MemberBasDTO> getFindAll(int pagenum, int pagesize) {
		return adminMemberDAO.findAll(pagenum, pagesize);
	}

	// 전체 회원수 조회(return 회원수)
	public int getMemberTotalCount() {
		return adminMemberDAO.selectmemberTotalCount();
	}

	// 2.3 회원관리 역활 수정(탈퇴, 회원, 기자, 관리자)
	public int setMemberRoleChange(MemberRoleChange dto) {
		return adminMemberDAO.updateMemberRoleChange(dto);
	}

	// 2.4 회원관리 신규 회원 추가
	public int registerMemberBas(MemberBasDTO dto) {
		return adminMemberDAO.insertMemberBas(dto);
	}

	// 2.4 회원관리 신규 회원 추가
	public MemberBasDTO getMemberBas(String memberId) {
		return adminMemberDAO.selectMemberBas(memberId);
	}

}
