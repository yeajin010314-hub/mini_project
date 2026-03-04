package web.member;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dao.MypageDAO;
import dto.MemberBasDTO;

//업데이트프로필 이미지서블릿
//로그인체크->멀티파트 이미지업로드 검증->서버 디렉토리 저장->DB에 경로저장->세션DTO동기화->ok/path 반환
@WebServlet("/member/updateProfileImg")
@MultipartConfig(maxFileSize = 5*1024*1024, maxRequestSize = 6*1024*1024)
public class UpdateProfileImgServlet extends HttpServlet {
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    req.setCharacterEncoding("UTF-8");
    res.setContentType("text/plain; charset=UTF-8");

    HttpSession session = req.getSession(false);
    MemberBasDTO user = (session==null)? null : (MemberBasDTO)session.getAttribute("loginUser");
    if(user == null){
      res.setStatus(401);
      res.getWriter().print("NOLOGIN");
      return;
    }

    Part filePart = req.getPart("profileFile");
    if(filePart == null || filePart.getSize() <= 0){
      res.setStatus(400);
      res.getWriter().print("NOFILE");
      return;
    }

    String ct = filePart.getContentType();
    if(ct == null || !ct.startsWith("image/")){
      res.setStatus(400);
      res.getWriter().print("NOTIMAGE");
      return;
    }

    String uploadDir = getServletContext().getRealPath("/resources/upload/profile");
    File dir = new File(uploadDir);
    if(!dir.exists()) dir.mkdirs();

    String ext = "jpg";
    if(ct.contains("png")) ext = "png";
    else if(ct.contains("gif")) ext = "gif";
    else if(ct.contains("webp")) ext = "webp";

    String savedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
    File savedFile = new File(dir, savedName);

    try(InputStream in = filePart.getInputStream()){
      Files.copy(in, savedFile.toPath());
    }

    String dbPath = "/resources/upload/profile/" + savedName;

    boolean ok = MypageDAO.getInstance().updateProfileImgUrl(user.getMemberId(), dbPath);
    if(!ok){
      try{ savedFile.delete(); }catch(Exception e){}
      res.setStatus(500);
      res.getWriter().print("FAIL");
      return;
    }

    // 세션 동기화(DTO에 필드 있어야 함)
    user.setProfileImgUrl(dbPath);
    session.setAttribute("loginUser", user);

    res.getWriter().print("OK|" + dbPath);
  }
}
