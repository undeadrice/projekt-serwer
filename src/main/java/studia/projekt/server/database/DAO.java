package studia.projekt.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import studia.projekt.server.database.model.Account;
import studia.projekt.server.database.model.MeasurementEntry;

/**
 * Konkretna implementacja wszystkich metod wykorzystująca SQLite
 * 
 * @author bruce
 *
 */
public class DAO extends AbstractSQLiteDAO {

	public DAO() {

	}

	@Override
	public void createAccount(Account account) throws SQLException {
		try (PreparedStatement ps = this.getCon().prepareStatement(
				"INSERT INTO account(login,password,name,surname,sex,code)" + "VALUES(?,?,?,?,?,?)")) {
			ps.setString(1, account.getLogin());
			ps.setString(2, account.getPassword());
			ps.setString(3, account.getName());
			ps.setString(4, account.getSurname());
			ps.setByte(5, account.getSex());
			ps.setString(6, account.getCode());
			ps.execute();
		}

	}

	@Override
	public Account selectAccount(Integer id) throws SQLException {
		try (PreparedStatement ps = this.getCon().prepareStatement("select * from account where id = ?")) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getByte(6), rs.getString(7));
			}
		}
		return null;
	}

	@Override
	public void updateAccount(Account account) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Boolean deleteAccount(Account account) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Account> selectAllAccounts() throws SQLException {
		List<Account> accounts = new ArrayList<>();
		try (PreparedStatement ps = this.getCon().prepareStatement("select * from account")) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				accounts.add(new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getByte(6), rs.getString(7)));
			}
		}
		return accounts;
	}

	@Override
	public void createMeasurementEntry(MeasurementEntry entry) throws SQLException {
		try (PreparedStatement ps = this.getCon().prepareStatement(
				"INSERT INTO entry_measurement(date,account_id,leukocyte,erythrocyte,hemoglobin,hematocrit,mcv,mch,mchc,platelets,lymphocyte)"
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {
			ps.setLong(1, entry.getDate());
			ps.setInt(2, entry.getAccountId());
			ps.setDouble(3, entry.getLeukocyte());
		
			ps.setDouble(4, entry.getErythrocyte());
			ps.setDouble(5, entry.getHemoglobin());
			ps.setDouble(6, entry.getHematocrit());
			ps.setDouble(7, entry.getMcv());
			ps.setDouble(8, entry.getMch());
			ps.setDouble(9, entry.getMchc());
			ps.setDouble(10, entry.getPlatelets());
			ps.setDouble(11, entry.getLymphocyte());
			ps.execute();
		}

	}

	@Override
	public MeasurementEntry selectMeasurementEntry(Integer id) throws SQLException {
		try (PreparedStatement ps = this.getCon().prepareStatement("SELECT * FROM entry_measurement where id = ?")) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new MeasurementEntry(rs.getInt(1), rs.getInt(2), rs.getLong(3), rs.getDouble(4), rs.getDouble(5),
						rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getDouble(9), rs.getDouble(10),
						rs.getDouble(11), rs.getDouble(12));
			}
		}
		return null;
	}

	@Override
	public void updateMeasurementEntry(MeasurementEntry entry) throws SQLException {
		try (PreparedStatement ps = this.getCon().prepareStatement(
				"UPDATE entry_measurement SET date = ?,account_id = ?,leukocyte = ?,erythrocyte = ?,hemoglobin = ?,hematocrit = ?,mcv = ? ,mch = ?,mchc= ?,platelets = ?,lymphocyte = ? "
				+ "WHERE id = ?")) {
			ps.setLong(1, entry.getDate());
			ps.setInt(2, entry.getAccountId());
			ps.setDouble(3, entry.getLeukocyte());
			ps.setDouble(4, entry.getErythrocyte());
			ps.setDouble(5, entry.getHemoglobin());
			ps.setDouble(6, entry.getHematocrit());
			ps.setDouble(7, entry.getMcv());
			ps.setDouble(8, entry.getMch());
			ps.setDouble(9, entry.getMchc());
			ps.setDouble(10, entry.getPlatelets());
			ps.setDouble(11, entry.getLymphocyte());
			ps.setInt(12, entry.getId());
			ps.execute();
		}

	}

	@Override
	public Boolean deleteMeasurementEntry(Integer id) throws SQLException {
		try (PreparedStatement ps = this.getCon().prepareStatement("DELETE FROM entry_measurement where id = ?")) {
			ps.setInt(1, id);
			ps.execute();
		}
		return null;
	}

	@Override
	public List<MeasurementEntry> selectAllMeasurementEntries() throws SQLException {
		List<MeasurementEntry> entries = new ArrayList<>();
		try (PreparedStatement ps = this.getCon().prepareStatement("SELECT * FROM entry_measurement")) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				entries.add(new MeasurementEntry(rs.getInt(1), rs.getInt(2), rs.getLong(3), rs.getDouble(4),
						rs.getDouble(5), rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getDouble(9),
						rs.getDouble(10), rs.getDouble(11), rs.getDouble(12)));
			}
		}
		return entries;
	}

}
