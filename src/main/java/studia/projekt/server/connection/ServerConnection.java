package studia.projekt.server.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import studia.projekt.server.Server;
import studia.projekt.server.database.DAO;
import studia.projekt.server.database.model.Account;
import studia.projekt.server.database.model.MeasurementEntry;

/**
 * Klasa reprezentująca połączenie klienta z serwerem wg założeń serwer będzie
 * przechowywał tylko 1 obiekt tego typu, z czego wynika też ograniczenie do
 * obsługi jednego połączenia jednocześnie
 * 
 * 
 * Połączenie opiera się na protokole TCP
 * 
 * @author bruce
 *
 */
public class ServerConnection {

	/**
	 * id konta związanego z tym połączeniem
	 */
	private Integer accountId = null;

	public static final byte loginByte = 0x01;
	public static final byte createByte = 0x02;
	public static final byte getMeasurementsByte = 0x03;
	public static final byte addMeasurementByte = 0x04;
	public static final byte editMeasurementByte = 0x05;
	public static final byte removeMeasurementByte = 0x06;
	public static final byte logoutByte = 0x07;

	/**
	 * rozmiar tablic znajdujących się w kolejkach
	 */
	public static final int QUEUE_SIZE = 10;

	/**
	 * referencja do serwera
	 */
	Server server;

	/**
	 * pula 3 wątków do obsługi połączenia, każdy wątek będzie przypisany do jednego
	 * z poniższych zadań: 1. Obsługa strumieni wyjścia (wysyłanie danych) 2.
	 * Obsługa strumieni wejścia (odbiór danych); 3. Wykonywanie pozostałych
	 * czynności
	 */
	private final ExecutorService threads = Executors.newFixedThreadPool(3);

	/**
	 * logger przypisany do tej klasy
	 */
	private Logger logger = Logger.getLogger(ServerConnection.class.getName());

	/*
	 * kolejki wejscia i wyjscia ich zadaniem jest umożliwienie współpracy różnych
	 * wątków
	 */
	private final ArrayBlockingQueue<Bundle> workQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
	private final ArrayBlockingQueue<Bundle> sendQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
	// strumień wejścia din oraz wyjscia dout
	private final DataInputStream din;
	private final DataOutputStream dout;

	/**
	 * połączenie z klientem
	 */
	private final Socket s;

	/**
	 * Data access object połączenie i obsługa bazy danych
	 */
	private final DAO dao;

	/**
	 * konstruktor
	 * 
	 * @throws IOException
	 */
	public ServerConnection(Server server, DAO dao, Socket s) throws IOException {
		this.server = server;
		this.dao = dao;
		this.s = s;

		// "dekorujemy" prosty obiekt InputStream oraz OutputStream by łatwiej było
		// wysyłać i odbierać dane
		din = new DataInputStream(s.getInputStream());
		dout = new DataOutputStream(s.getOutputStream());

	}

	public void sendLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			performNextSend();
		}
	}

	public void receiveLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			performNextRec();
		}
	}

	public void workLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			performNextWork();
		}
	}

	/**
	 * przekazuje zadania wątkom z puli threads i je uruchamia
	 */
	public void start() {
		threads.execute(() -> {
			try {
				sendLoop();
			} catch (InterruptedException | IOException | SQLException e) {
				disconnect();
			}
		});
		threads.execute(() -> {
			try {
				receiveLoop();
			} catch (InterruptedException | IOException | SQLException e) {
				disconnect();
			}
		});
		threads.execute(() -> {
			try {
				workLoop();
			} catch (InterruptedException | IOException | SQLException e) {
				disconnect();
			}
		});
	}

	public synchronized void disconnect() {
		try {
			s.close();
			threads.shutdownNow();
			server.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void performNextRec() throws IOException, SQLException, InterruptedException {
		System.out.println("waiting");
		byte b = din.readByte();
		System.out.println("rec: " + b);
		switch (b) {
		case loginByte:
			onLoginRec();
			break;
		case createByte:
			onCreateAccountRec();
			break;
		case getMeasurementsByte:
			onGetMeasurementsByteRec();
			break;
		case addMeasurementByte:
			onAddMeasurementByteRec();
			break;
		case editMeasurementByte:
			onEditMeasurementByteRec();
			break;
		case removeMeasurementByte:
			onRemoveMeasurementByteRec();
			break;
		case logoutByte:
			onLogoutByteRec();
			break;

		}
	}

	// OK
	private void onLoginRec() throws IOException, InterruptedException {
		String[] arr = new String[2];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = din.readUTF();
		}
		Bundle b = new Bundle(loginByte);
		b.putString("login", arr[0]);
		b.putString("password", arr[1]);
		workQueue.put(b);
	}

	// OK
	private void onCreateAccountRec() throws IOException, SQLException, InterruptedException {
		Bundle b = new Bundle(createByte);
		String[] arr = new String[5];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = din.readUTF();
		}

		b.putString("login", arr[0]);
		b.putString("password", arr[1]);
		b.putString("name", arr[2]);
		b.putString("surname", arr[3]);
		b.putString("code", arr[4]);
		b.putByte("sex", din.readByte());
		workQueue.put(b);
	}

	// OK
	private void onGetMeasurementsByteRec() throws InterruptedException {
		Bundle b = new Bundle(getMeasurementsByte);
		workQueue.put(b);
	}

	// OK
	private void onAddMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(addMeasurementByte);
		b.putLong("date", din.readLong());
		workQueue.put(b);

	}

	// ok
	private void onEditMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(addMeasurementByte);

		b.putInt("id", din.readInt());
		b.putLong("date", din.readLong());
		Double[] vals = new Double[9];
		for (int i = 0; i < vals.length; i++) {
			vals[i] = din.readDouble();
		}

		b.putDouble("leukocyte", vals[0]);
		b.putDouble("erythrocyte", vals[1]);
		b.putDouble("hemoglobin", vals[2]);
		b.putDouble("hematocrit", vals[3]);
		b.putDouble("mcv", vals[4]);
		b.putDouble("mch", vals[5]);
		b.putDouble("mchc", vals[6]);
		b.putDouble("platelets", vals[7]);
		b.putDouble("lymphocyte", vals[8]);
		workQueue.put(b);
	}

	// ok
	private void onRemoveMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(removeMeasurementByte);
		b.putInt("id", din.readInt());
		workQueue.put(b);
	}

	// ok
	private void onLogoutByteRec() throws InterruptedException {
		Bundle b = new Bundle(logoutByte);
		workQueue.put(b);

	}

	// ok
	private void performNextWork() throws InterruptedException, IOException, SQLException {
		Bundle b = workQueue.take();
		switch (b.getHeader()) {
		case loginByte:
			onLoginWork(b);
			break;
		case createByte:
			onCreateAccountWork(b);
			break;
		case getMeasurementsByte:
			onGetMeasurementsByteWork(b);
			break;
		case addMeasurementByte:
			onAddMeasurementByteWork(b);
			break;
		case editMeasurementByte:
			onEditMeasurementByteWork(b);
			break;
		case removeMeasurementByte:
			onRemoveMeasurementByteWork(b);
			break;
		case logoutByte:
			onLogoutByteWork(b);
			break;

		}

	}

	// ok
	private void onLoginWork(Bundle b) throws IOException, SQLException, InterruptedException {
		List<Account> accounts = dao.selectAllAccounts();
		for (Account acc : accounts) {
			if (acc.getLogin().equals(b.getString("login")) && acc.getPassword().equals(b.getString("password"))) {
				b.putBool("status", true);
				this.accountId = acc.getId();
				System.out.println(accountId);
				sendQueue.put(b);
				return;
			}
		}
		b.putBool("status", false);
		sendQueue.put(b);

	}

	// ok
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

	// ok
	private void onGetMeasurementsByteWork(Bundle b) throws SQLException, InterruptedException {
		List<MeasurementEntry> allMeasurements = dao.selectAllMeasurementEntries();
		List<MeasurementEntry> m = new ArrayList<>();
		allMeasurements.stream().filter(a -> a.getAccountId() == this.accountId).forEach(a -> m.add(a));
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

	private void onAddMeasurementByteWork(Bundle b) throws IOException, SQLException, InterruptedException {
		dao.createMeasurementEntry(new MeasurementEntry(this.accountId, b.getLong("date")));
		Integer nextId = dao.getNextKey();
		System.out.println("nextid: " + nextId);
		b.putInt("id", nextId);
		sendQueue.put(b);

	}

	private void onEditMeasurementByteWork(Bundle b) throws IOException, SQLException {
		MeasurementEntry e = new MeasurementEntry(b.getInt("id"), this.accountId, b.getLong("date"),
				b.getDouble("leukocyte"), b.getDouble("erythrocyte"), b.getDouble("hemoglobin"),
				b.getDouble("hematocrit"), b.getDouble("mcv"), b.getDouble("mch"), b.getDouble("mchc"),
				b.getDouble("platelets"), b.getDouble("lymphocyte"));
		dao.updateMeasurementEntry(e);

	}

	private void onRemoveMeasurementByteWork(Bundle b) throws IOException, SQLException, InterruptedException {
		dao.deleteMeasurementEntry(b.getInt("id"));
		sendQueue.put(b);
	}

	private void onLogoutByteWork(Bundle b) {

	}

	private void performNextSend() throws InterruptedException, IOException, SQLException {
		Bundle b = sendQueue.take();
		switch (b.getHeader()) {
		case loginByte:
			onLoginSend(b);
			break;
		case createByte:
			onCreateAccountSend(b);
			break;
		case getMeasurementsByte:
			onGetMeasurementsByteSend(b);
			break;
		case addMeasurementByte:
			onAddMeasurementByteSend(b);
			break;
		case editMeasurementByte:
			onEditMeasurementByteSend(b);
			break;
		case removeMeasurementByte:
			onRemoveMeasurementByteSend(b);
			break;
		case logoutByte:
			onLogoutByteSend(b);
			break;

		}

	}

	private void onLoginSend(Bundle b) throws IOException, SQLException, InterruptedException {
		dout.writeByte(b.getHeader());
		dout.writeBoolean(b.getBool("status"));
		dout.writeUTF(b.getString("login"));
		
		dout.writeUTF(b.getString("login"));
		dout.writeUTF(b.getString("login"));
		
		
	}

	private void onCreateAccountSend(Bundle b) throws IOException, SQLException, InterruptedException {
		dout.writeByte(b.getHeader());
		dout.writeBoolean(b.getBool("status"));
		dout.writeUTF(b.getString("login"));
	}

	private void onGetMeasurementsByteSend(Bundle b) throws SQLException, IOException {
		// wyślij wszystko :|
		dout.writeByte(b.getHeader());
		int size = b.getInt("size");
		dout.writeInt(size);
	
		for (int i = 0; i < size; i++) {
			dout.writeDouble(b.getDouble("leukocyte" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("erythrocyte" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("hemoglobin" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("hematocrit" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("mcv" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("mch" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("mchc" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("platelets" + Integer.toString(i)));
			dout.writeDouble(b.getDouble("lymphocyte" + Integer.toString(i)));
			dout.writeInt(b.getInt("id" + Integer.toString(i)));
			dout.writeLong(b.getLong("date" + Integer.toString(i)));
		}

	}

	private void onAddMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeInt(b.getInt("id"));
		dout.writeLong(b.getLong("date"));
	}

	private void onEditMeasurementByteSend(Bundle b) throws IOException {
		// nic?
	}

	private void onRemoveMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeInt(b.getInt("id"));
		
	}

	private void onLogoutByteSend(Bundle b) {
		// status
	}

}
