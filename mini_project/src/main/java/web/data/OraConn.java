package web.data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
public class OraConn {
	// jdbc oracle 1,2 단계
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//String dburl ="jdbc:oracle:thin:@192.168.219.198:1521:orcl";
			String dburl = "jdbc:oracle:thin:@58.73.200.225:1521:ORCL";
			String user ="web2";
			String pw="1234";
			conn = DriverManager.getConnection(dburl,user,pw);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void allClose(Connection c, PreparedStatement p , ResultSet r) {
		try { if(c != null)c.close(); }catch(Exception e) {}
		try { if(p != null)p.close(); }catch(Exception e) {}
		try { if(r != null)r.close(); }catch(Exception e) {}
	}
	
	public static void allClose(Connection c, PreparedStatement p ) {
		try { if(c != null)c.close(); }catch(Exception e) {}
		try { if(p != null)p.close(); }catch(Exception e) {}
	}
}














