package dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryDTO {
	// 필드(테이블 구조와 동일)
	private String categoryId;
	private String categoryNm;
	private String upCategoryId;
	private LocalDate regDt;
	private String delYn;

	// 필드(추가 컬럼)
	private String pathCategoryNm;	// 카테고리 경로명
	private int depthLevel;			// depthLevel
	
	// 상/하위 카테고리
	private List<CategoryDTO> child = new ArrayList<>();
	
	// 기본생성자
	public CategoryDTO() {};
	
	public CategoryDTO(
			String categoryId,
			String categoryNm,
			String upCategoryId)
	{
		this.categoryId = categoryId;
		this.categoryNm = categoryNm;
		this.upCategoryId = upCategoryId;
	}

	// 전체 생성자
	public CategoryDTO(
			String categoryId,
			String categoryNm,
			String upCategoryId,
			LocalDate regDt,
			String pathCategoryNm,
			int depthLevel)
	{
		this.categoryId = categoryId;
		this.categoryNm = categoryNm;
		this.upCategoryId = upCategoryId;
		this.regDt = regDt;
		this.pathCategoryNm = pathCategoryNm;
		this.depthLevel = depthLevel;
	}
	
	
	public void addChild(CategoryDTO child1) {
		child.add(child1);
    }
	
	// 메서드(Getter/Setter)
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryNm() {
		return categoryNm;
	}
	public void setCategoryNm(String categoryNm) {
		this.categoryNm = categoryNm;
	}
	public String getUpCategoryId() {
		return upCategoryId;
	}
	public void setUpCategoryId(String upCategoryId) {
		this.upCategoryId = upCategoryId;
	}
	public LocalDate getRegDt() {
		return regDt;
	}
	public void setRegDt(LocalDate regDt) {
		this.regDt = regDt;
	}

	public String getDelYn() {
		return delYn;
	}

	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}
	
	public String getPathCategoryNm() {
		return pathCategoryNm;
	}

	public void setPathCategoryNm(String pathCategoryNm) {
		this.pathCategoryNm = pathCategoryNm;
	}

	public int getDepthLevel() {
		return depthLevel;
	}

	public void setDepthLevel(int depthLevel) {
		this.depthLevel = depthLevel;
	}

	public List<CategoryDTO> getChild() {
		return child;
	}

	public void setChild(List<CategoryDTO> child) {
		this.child = child;
	}

	
} // class of end
