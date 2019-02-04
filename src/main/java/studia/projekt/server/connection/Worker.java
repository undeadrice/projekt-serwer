package studia.projekt.server.connection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import studia.projekt.server.database.DAO;
import studia.projekt.server.database.model.Account;
import studia.projekt.server.database.model.MeasurementEntry;

/**
 * klasa odpowiedzialna za wykonywanie prac takich jak baza danych, sprawdzanie
 * poprawności danych itp
 * 
 */
public class Worker {

	private final ArrayBlockingQueue<Bundle> workQueue;
	private final ArrayBlockingQueue<Bundle> sendQueue;
	private final DAO dao;

	private final ServerConnection con;

	public Worker(ServerConnection con, ArrayBlockingQueue<Bundle> workQueue, ArrayBlockingQueue<Bundle> sendQueue,
			DAO dao) {
		super();
		this.con = con;
		this.workQueue = workQueue;
		this.sendQueue = sendQueue;
		this.dao = dao;
	}

	/**
	 * pobiera następne zadanie do wykonania z kolejki workQueue
	 * odpowiednia metoda jest wywoływana poprzez identyfikujący bajt
	 */
	public void performNextWork() throws InterruptedException, IOException, SQLException {
		Bundle b = workQueue.take();
		switch (b.getHeader()) {
		case ServerConnection.loginByte:
			onLoginWork(b);
			break;
		case ServerConnection.createByte:
			onCreateAccountWork(b);
			break;
		case ServerConnection.getMeasurementsByte:
			onGetMeasurementsByteWork(b);
			break;
		case ServerConnection.addMeasurementByte:
			onAddMeasurementByteWork(b);
			break;
		case ServerConnection.editMeasurementByte:
			onEditMeasurementByteWork(b);
			break;
		case ServerConnection.removeMeasurementByte:
			onRemoveMeasurementByteWork(b);
			break;
		case ServerConnection.logoutByte:
			onLogoutByteWork(b);
			break;

		}

	}

	/**
	 * sprawdza prawidłowość danych logowania, wrzuca do kolejki sendQueue nowy
	 * obiekt bundle z wpisem "status" wpis ten ma wartość true wtedy gdy dane
	 * logowania są prawidłowe, false gdy nie prawidłowe
	 * 
	 * @param b
	 */
	private void onLoginWork(Bundle b) throws IOException, SQLException, InterruptedException {
		List<Account> accounts = dao.selectAllAccounts();
		for (Account acc : accounts) {
			if (acc.getLogin().equals(b.getString("login")) && acc.getPassword().equals(b.getString("password"))) {
				b.putBool("status", true);
				b.putString("name", acc.getName());
				b.putString("surname", acc.getSurname());
				con.setAccountId(acc.getId());

				sendQueue.put(b);
				return;
			}
		}
		b.putBool("status", false);
		sendQueue.put(b);

	}

	/**
	 * sprawdza czy konto o podanej nazwie istnieje, następnie wrzuca do kolejki
	 * sendQueue nowy obiekt Bundle z wpisem "status" który informuje klienta o tym
	 * czy tworzenie konta się powiodło
	 * 
	 * @param b
	 */
	private void onCreateAccountWork(Bundle b) throws IOException, SQLException, InterruptedException {
		List<Account> accounts = dao.selectAllAccounts();
		for (Account acc : accounts) {
			if (acc.getLogin().equals(b.getString("login"))) {
				b.putBool("status", false);
				sendQueue.put(b);
				return;
			}
		}
		Account account = new Account(b.getString("login"), b.getString("password"), b.getString("name"),
				b.getString("surname"), b.getByte("sex"), b.getString("code"));

		dao.createAccount(account);
		b.putBool("status", true);
		sendQueue.put(b);
	}

	/**
	 * pobiera wszystkie wpisy związane z danym kontem i przekazuje je do kolejki
	 * sendQueue poprzez obiekt Bundle
	 * @param b
	 */
	private void onGetMeasurementsByteWork(Bundle b) throws SQLException, InterruptedException {
		List<MeasurementEntry> allMeasurements = dao.selectAllMeasurementEntries();
		List<MeasurementEntry> m = new ArrayList<>();
		allMeasurements.stream().filter(a -> a.getAccountId() == con.getAccountId()).forEach(a -> m.add(a));
		b.putInt("size", m.size());
		for (int i = 0; i < m.size(); i++) {
			b.putDouble("leukocyte" + Integer.toString(i), m.get(i).getLeukocyte());
			b.putDouble("erythrocyte" + Integer.toString(i), m.get(i).getErythrocyte());
			b.putDouble("hemoglobin" + Integer.toString(i), m.get(i).getHemoglobin());
			b.putDouble("hematocrit" + Integer.toString(i), m.get(i).getHematocrit());
			b.putDouble("mcv" + Integer.toString(i), m.get(i).getMcv());
			b.putDouble("mch" + Integer.toString(i), m.get(i).getMch());
			b.putDouble("mchc" + Integer.toString(i), m.get(i).getMchc());
			b.putDouble("platelets" + Integer.toString(i), m.get(i).getPlatelets());
			b.putDouble("lymphocyte" + Integer.toString(i), m.get(i).getLymphocyte());
			b.putInt("id" + Integer.toString(i), m.get(i).getId());
			b.putLong("date" + Integer.toString(i), m.get(i).getDate());

		}
		sendQueue.put(b);
	}

	/**
	 * dodaje nowy wpis do bazy danych, oraz pobiera wartość klucz głównego tego
	 * wpisu. Klucz ten jest przekazywany do kolejki sendQueue przez obiekt Bundle
	 * 
	 * @param b
	 */
	private void onAddMeasurementByteWork(Bundle b) throws IOException, SQLException, InterruptedException {
		dao.createMeasurementEntry(new MeasurementEntry(con.getAccountId(), b.getLong("date")));
		Integer nextId = dao.getNextKey();
		System.out.println("nextid: " + nextId);
		b.putInt("id", nextId);
		sendQueue.put(b);

	}
	/**
	 * edytuje wpisy w bazie danych
	 * @param b
	 */
	private void onEditMeasurementByteWork(Bundle b) throws IOException, SQLException {
		MeasurementEntry e = new MeasurementEntry(b.getInt("id"), con.getAccountId(), b.getLong("date"),
				b.getDouble("leukocyte"), b.getDouble("erythrocyte"), b.getDouble("hemoglobin"),
				b.getDouble("hematocrit"), b.getDouble("mcv"), b.getDouble("mch"), b.getDouble("mchc"),
				b.getDouble("platelets"), b.getDouble("lymphocyte"));
		dao.updateMeasurementEntry(e);

	}
	/**
	 * kasuje wybrany wpis z bazy danych
	 * @param b
	 */
	private void onRemoveMeasurementByteWork(Bundle b) throws IOException, SQLException, InterruptedException {
		dao.deleteMeasurementEntry(b.getInt("id"));
		sendQueue.put(b);
	}
	/**
	 * kończy obecne połączenie
	 * @param b
	 */
	private void onLogoutByteWork(Bundle b) {

	}
}
