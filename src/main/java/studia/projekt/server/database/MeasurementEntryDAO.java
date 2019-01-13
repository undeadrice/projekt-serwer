package studia.projekt.server.database;

import java.sql.SQLException;
import java.util.List;

import studia.projekt.server.database.model.Account;
import studia.projekt.server.database.model.MeasurementEntry;

/**
 * Zbiór metod mających na celu wykonywanie podstawowych operacji CRUD na danych związanych z wynikami krwi
 * @author bruce
 *
 */
public interface MeasurementEntryDAO {

	
	void createMeasurementEntry(MeasurementEntry entry) throws SQLException;

	Account selectMeasurementEntry(Integer id) throws SQLException;

	void updateMeasurementEntry(MeasurementEntry entry) throws SQLException;

	Boolean deleteMeasurementEntry(MeasurementEntry entry) throws SQLException;

	List<Account> selectAllMeasurementEntries() throws SQLException;
	
}
