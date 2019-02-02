package studia.projekt.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Częściowa implementacja, wykorzystująca SQLite
 * 
 * @author bruce
 *
 */
public abstract class AbstractSQLiteDAO implements AccountDAO, MeasurementEntryDAO {

	/**
	 *Ścieżka do bazy danych
	 */
	public static final String PATH = "C:\\Users\\bruce\\Desktop\\projekt klient\\DB.db";
	
	/**
	 * rodzaj bazy
	 */
	public static final String DRIVER = "jdbc:sqlite:";
	
	/**
	 * obiekt Connection reprezentujący połączenie z bazą danych
	 */
	protected Connection con;
	
	
	public void connect() throws SQLException {
		con = DriverManager.getConnection(DRIVER+PATH);
	}
	
	public void close() throws SQLException {
		con.close();
	}

	public Connection getCon() {
		return con;
	}
	
	@Override
	public Integer getNextKey() throws SQLException {
		try(PreparedStatement ps = con.prepareStatement("SELECT seq FROM sqlite_sequence WHERE name = ?")){
			ps.setString(1, "entry_measurement");
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				
				Integer next = rs.getInt(1);

				return next;
			}
		}
	
		return null;
	}
	
	
}
