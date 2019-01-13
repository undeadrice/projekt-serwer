package studia.projekt.server.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import studia.projekt.server.Server;

/**
 * Klasa reprezentująca połączenie klienta z serwerem
 * wg założeń serwer będzie przechowywał tylko 1 obiekt tego typu,
 * z czego wynika też ograniczenie do obsługi jednego połączenia jednocześnie
 * 
 * 
 * Połączenie opiera się na protokole TCP
 * 
 * @author bruce
 *
 */
public class ServerConnection {
	
	/**
	 * rozmiar tablic znajdujących się w kolejkach
	 */
	public static final int QUEUE_SIZE = 10;
	
	/**
	 * referencja do serwera
	 */
	Server server;
	
	/**
	 * pula 3 wątków do obsługi połączenia, każdy wątek będzie przypisany do jednego z poniższych zadań:
	 * 1. Obsługa strumieni wyjścia (wysyłanie danych)
	 * 2. Obsługa strumieni wejścia (odbiór danych);
	 * 3. Wykonywanie pozostałych czynności
	 */
	private final ExecutorService threads = Executors.newFixedThreadPool(3);
	
	/**
	 * logger przypisany do tej klasy
	 */
	private Logger logger = Logger.getLogger(ServerConnection.class.getName());
	
	/*
	 * kolejki wejscia i wyjscia
	 * ich zadaniem jest umożliwienie współpracy różnych wątków
	 */
	private final ArrayBlockingQueue<ReceiveTask> qin = new ArrayBlockingQueue<>(QUEUE_SIZE);
	private final ArrayBlockingQueue<SendTask> qout = new ArrayBlockingQueue<>(QUEUE_SIZE);
	
	//  strumień wejścia din oraz wyjscia dout
	private final DataInputStream din;
	private final DataOutputStream dout;
	
	/**
	 * połączenie z klientem
	 */
	private final Socket s;
	
	
	/**
	 * konstruktor
	 * @throws IOException 
	 */
	public ServerConnection(Server server, Socket s) throws IOException {
		this.server = server;
		this.s = s;
		
		// "dekorujemy" prosty obiekt InputStream oraz OutputStream by łatwiej było wysyłać i odbierać dane
		din = new DataInputStream(s.getInputStream());
		dout = new DataOutputStream(s.getOutputStream());
	
	
	}
	
	public void sendLoop() throws InterruptedException {
		while(true) {
			// pobierz następne zadanie SendTask z kolejki qin, jeśli zadania nie ma, czekaj
			qout.take().send(this.dout);
		}
	}
	
	public void receiveLoop() throws InterruptedException  {
		while(true) {
			qout.take().send(this.dout);
			
		}
	}
	
	public void workLoop()throws InterruptedException  {
		while(true) {
			qout.take().send(this.dout);
		}
	}
	/**
	 * przekazuje zadania wątkom z puli threads i je uruchamia
	 */
	public void start() {
		threads.execute(() -> {
			try {
				sendLoop();
			} catch (InterruptedException e) {
				disconnect();
			}
		});
		threads.execute(() -> {
			try {
				receiveLoop();
			} catch (InterruptedException e) {
				disconnect();
			}
		});
		threads.execute(() -> {
			try {
				workLoop();
			} catch (InterruptedException e) {
				disconnect();
			}
		});
	}
	
	public synchronized void disconnect()  {
		try {
			s.close();
			threads.shutdownNow();
			server.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
}
