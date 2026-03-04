package dto;

public class ArticleDetailViewDTO {
	private ArticleViewDTO article;
    private ArticleDtlViewDTO articleDtl;
    
	// 기본생성자
	public ArticleDetailViewDTO() {}

	public ArticleViewDTO getArticle() {
		return article;
	}

	public void setArticle(ArticleViewDTO article) {
		this.article = article;
	}

	public ArticleDtlViewDTO getArticleDtl() {
		return articleDtl;
	}

	public void setArticleDtl(ArticleDtlViewDTO articleDtl) {
		this.articleDtl = articleDtl;
	};

}
