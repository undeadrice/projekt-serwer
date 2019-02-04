package studia.projekt.server.database;

import java.sql.SQLException;
import java.util.List;

import studia.projekt.server.database.model.Account;

/**
 * Zbiór metod mających na celu wykonywanie podstawowych operacji Create , Read , Update , Delete (CRUD) na danych
 * związanych z kontami
 * 
 *
 */
public interface AccountDAO {

	void createAccount(Account account) throws SQLException;

	Account selectAccount(Integer id) throws SQLException;

	void updateAccount(Account account) throws SQLException;

	Boolean deleteAccount(Account account) throws SQLException;

	List<Account> selectAllAccounts() throws SQLException;

}
