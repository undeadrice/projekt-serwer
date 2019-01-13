package studia.projekt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import studia.projekt.server.connection.ServerConnection;

/**
 * <b>Klasa główna serwera</b>
 * 
 * Założeniami serwera są: - zdolność do obsługi wyłącznie jednego połączenia w
 * danej chwili - serwer opiera się na blokującym IO, dlatego dla każdego
 * połączenia utwarzane są 3 dedykowane im wątki
 * 
 * - protokół TCP
 * 
 * <b> Algorytm serwera </b> 1. Serwer, zajmując główny wątek oczekuje na
 * połączenie klienta, dopóki to nie nastąpi wątek serwera jest zablokowany 2.
 * Po ustaleniu połączenia, utwórz obiekt Socket, następnie przekaż go nowo
 * utworzonemu obiektowi <b>ServerConnection<b/> 3. Serwer uruchamia 3 wątki i
 * przekazuje im odpowiednie zadania 4. Po zerwaniu połączenia (wylogowanie ,
 * lub nie rozpoznanie klienta) wątki skojarzone z obiektem ServerConnection są
 * zatrzymane, a referencja servera do obiektu zostaje zamieniona na <b>null</b>
 * 
 * @author bruce
 *
 */
public class Server {

	/**
	 * instancja Server. Ze względu na to że będziemy obiekt typu Server tworzyć w
	 * metodzie głównej deklarujemy tą zmienną jako statyczną
	 */
	private static Server server;

	/**
	 * PORT który zajmuje serwer
	 */
	public static final int PORT = 8080;

	/**
	 * logger przypisany do tej klasy
	 */
	private Logger logger = Logger.getLogger(ServerConnection.class.getName());

	/**
	 * gniazdo serwera, do przyjmowania połączeń i tworzenia obiektów typu Socket
	 * potrzebnych do utworzenia obiektu ServerConnection
	 */
	private ServerSocket ss;

	/**
	 * połączenie i jego obsługa
	 */
	private ServerConnection sCon;

	public static void main(String[] args) throws IOException {
		server = new Server();
		server.waitForConnection();

	}

	/**
	 * Po wywołaniu tej metody serwer oczekuje na połączenie. W przypadku gdy chcemy
	 * by serwer po nawiązaniu i przerwaniu jednego połączenia oczekiwał na
	 * następne, zawsze należy po rozłączeniu wywołać tą metodę
	 * 
	 * @throws IOException
	 *             W przypadku gdy nie udało się utworzyć obiektu SS, np. gdy port
	 *             jest już zajęty
	 */
	public void waitForConnection() throws IOException {
		logger.log(Level.INFO, "Serwer startuje");
		if (sCon == null) {
			ss = new ServerSocket(PORT);
			logger.log(Level.INFO, "Serwer oczekuje na połączenie");
			// oczekuj na połączenie
			Socket s = ss.accept();
			logger.log(Level.INFO, "Połączono z klientem");

			// po połączeniu utwórz obiekt ServerConnection
			sCon = new ServerConnection(this, s);
			sCon.start();
			// od tego czasu obiekt ten kontroluje w zasadzie cały serwer i to co się na nim
			// dzieje
		}

	}

	/**
	 * resetuje połączenie Utworzony zostaje nowy pojedyńczy wątek na którym serwer
	 * oczekuje na nowe połączenie
	 */
	public void reset() {
		if (sCon != null) {
			logger.log(Level.INFO, "restart");
			sCon = null;
			Thread starter = new Thread(() -> {
				try {
					waitForConnection();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			starter.start();
		}
	}
}
