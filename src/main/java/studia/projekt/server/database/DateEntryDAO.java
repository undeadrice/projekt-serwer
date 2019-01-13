package studia.projekt.server.database;

import java.sql.SQLException;
import java.util.List;

import studia.projekt.server.database.model.Account;
import studia.projekt.server.database.model.DateEntry;

public interface DateEntryDAO {

	void createDateEntry(DateEntry entry) throws SQLException;

	Account selectDateEntry(Integer id) throws SQLException;

	void updateDateEntry(DateEntry entry) throws SQLException;

	Boolean deleteDateEntry(DateEntry entry) throws SQLException;

	List<Account> selectAllDateEntries() throws SQLException;

}
