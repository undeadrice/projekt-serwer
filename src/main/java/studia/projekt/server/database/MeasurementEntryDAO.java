package studia.projekt.server.database;

import java.sql.SQLException;
import java.util.List;


import studia.projekt.server.database.model.MeasurementEntry;

/**
 * Zbiór metod mających na celu wykonywanie podstawowych operacji CRUD na danych
 * związanych z wynikami krwi
 * 
 *
 */
public interface MeasurementEntryDAO {

	void createMeasurementEntry(MeasurementEntry entry) throws SQLException;

	MeasurementEntry selectMeasurementEntry(Integer id) throws SQLException;

	void updateMeasurementEntry(MeasurementEntry entry) throws SQLException;

	Boolean deleteMeasurementEntry(Integer id) throws SQLException;

	List<MeasurementEntry> selectAllMeasurementEntries() throws SQLException;
	
	Integer getNextKey()throws SQLException;

}
