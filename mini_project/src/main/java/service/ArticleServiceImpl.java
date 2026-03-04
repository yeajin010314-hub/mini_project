package service;

import java.util.List;

import dao.MainArticleDAO;
import dto.ArticleDetailViewDTO;

public class ArticleServiceImpl {
	// 핃드(관리자 대시보드)
	private MainArticleDAO mainArticleDAO = new MainArticleDAO();

	// 생성자(관리자 대시보드)
    public ArticleServiceImpl(MainArticleDAO mainArticleDAO) {
        this.mainArticleDAO = mainArticleDAO;
    }

    
    public List<ArticleDetailViewDTO> getArticleDetailView(String categoryId, int rowLimit) {

		List<ArticleDetailViewDTO> articleDetailViewList = mainArticleDAO.selectArticleListById(categoryId, rowLimit);
        return articleDetailViewList;
    }
    
	public ArticleDetailViewDTO getMainTopArticlesByCategoryGroup(String div) {
		ArticleDetailViewDTO articleDetailView = mainArticleDAO.selectMainTopArticlesByCategoryGroup(div);
        return articleDetailView;
		
	}

		
}
