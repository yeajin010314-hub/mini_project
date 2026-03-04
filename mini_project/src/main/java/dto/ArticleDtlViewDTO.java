package dto;

import java.time.LocalDateTime;

public class ArticleDtlViewDTO {
    private int 	   articleNo;
    private String articleContent;
    private String articleImg1;
    private String articleImg2;
    private String articleImg3;
    private String linkText;
    private String linkUrl;
    private LocalDateTime regDt;


	// 기본생성자
	public ArticleDtlViewDTO() {};
	
	// 전체 생성자
	public ArticleDtlViewDTO(
		    int 	   articleNo,
		    String articleContent,
		    String articleImg1,
		    String articleImg2,
		    String articleImg3,
		    String linkText,
		    String linkUrl,
		    LocalDateTime regDt)
	{
		this.articleNo = articleNo;
		this.articleContent = articleContent;
		this.articleImg1 = articleImg1;
		this.articleImg2 = articleImg2;
		this.articleImg3 = articleImg3;
		this.linkText = linkText;
		this.linkUrl = linkUrl;
		this.regDt = regDt;
	}

	public int getArticleNo() {
		return articleNo;
	}

	public void setArticleNo(int articleNo) {
		this.articleNo = articleNo;
	}

	public String getArticleContent() {
		return articleContent;
	}

	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}

	public String getArticleImg1() {
		return articleImg1;
	}

	public void setArticleImg1(String articleImg1) {
		this.articleImg1 = articleImg1;
	}

	public String getArticleImg2() {
		return articleImg2;
	}

	public void setArticleImg2(String articleImg2) {
		this.articleImg2 = articleImg2;
	}

	public String getArticleImg3() {
		return articleImg3;
	}

	public void setArticleImg3(String articleImg3) {
		this.articleImg3 = articleImg3;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public LocalDateTime getRegDt() {
		return regDt;
	}

	public void setRegDt(LocalDateTime regDt) {
		this.regDt = regDt;
	}
	
	
} // class of end;
