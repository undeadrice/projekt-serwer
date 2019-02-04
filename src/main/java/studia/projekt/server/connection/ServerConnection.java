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
 * Klasa reprezentująca połączenie klienta
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

	// bajty służące do identyfikacji poleceń przy wysyłaniu, odbieraniu i pracy
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
	 * czynności ( bazy danych , sprawdzanie danych logowania )
	 */
	private final ExecutorService threads = Executors.newFixedThreadPool(3);

	/**
	 * logger przypisany do tej klasy
	 */
	private Logger logger = Logger.getLogger(ServerConnection.class.getName());

	/*
	 * kolejki wejscia i wyjscia - umożliwiają współpracę różnych wątków
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

	// obiekty odpowiedzialne za wykonywanie czynności związanych z wysyłaniem,
	// odbiorem i pracą z danymi
	private final Worker worker;
	private final Receiver receiver;
	private final Sender sender;

	public ServerConnection(Server server, DAO dao, Socket s) throws IOException {
		this.server = server;
		this.dao = dao;
		this.s = s;

		// dekorujemy prosty obiekt InputStream oraz OutputStream by łatwiej było
		// wysyłać i odbierać dane
		din = new DataInputStream(s.getInputStream());
		dout = new DataOutputStream(s.getOutputStream());

		this.sender = new Sender(this, sendQueue, dout);
		this.receiver = new Receiver(this, workQueue, din);
		this.worker = new Worker(this, workQueue, sendQueue, dao);

	}

	/// pętle odpowiedzialne za odbieranie , wysyłanie oraz pracę z danymi

	public void sendLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			sender.performNextSend();
		}
	}

	public void receiveLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			receiver.performNextRec();
		}
	}

	public void workLoop() throws InterruptedException, IOException, SQLException {
		while (true) {
			worker.performNextWork();
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

	/**
	 * przerywa połączenie, zatrzymuje wątki i resetuje serwer
	 */
	public synchronized void disconnect() {
		try {
			s.close();
			threads.shutdownNow();
			server.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

}
