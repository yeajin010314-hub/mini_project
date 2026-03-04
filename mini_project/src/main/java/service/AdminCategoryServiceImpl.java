package service;

import java.util.List;

import dao.AdminCategoryDAO;
import dto.CategoryDTO;

public class AdminCategoryServiceImpl {
	// 핃드(관리자 대시보드)
	private final AdminCategoryDAO adminCategoryDAO;

	// 생성자(관리자 대시보드)
    public AdminCategoryServiceImpl(AdminCategoryDAO adminCategoryDAO) {
        this.adminCategoryDAO = adminCategoryDAO;
    }

	// Category List 조회(일반 DTO용)
	public List<CategoryDTO> getCategoryList(int pagenum, int pagesize) {
		return adminCategoryDAO.selectCategoryList(pagenum, pagesize);
	}

	// 3.2 전체 카테고리 건수 조회
	public int getCategoryTotalCount() {
		return adminCategoryDAO.selectCategoryTotalCount();
	}

	// 3.3 삭제대상의 하위 카테고리 존재여부 검증
	public int validDeletableCategory(CategoryDTO dto) {
		return adminCategoryDAO.selectExistsChildCategory(dto);
	}

	// 3.3 Category 수정
	public int modifyCategory(CategoryDTO dto) {
		return adminCategoryDAO.updateCategory(dto);
	}

	// 3.4 Category 등록
	public int registerCategory(CategoryDTO dto) {
		return adminCategoryDAO.insertCategory(dto);
	}

	// 3.4 Category 등록
	public List<CategoryDTO> getCategoryTreeList() {
		return adminCategoryDAO.selectCategoryTreeList();
	}

}
