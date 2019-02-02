package studia.projekt.server.connection;

import java.io.DataInputStream;

import studia.projekt.server.database.DAO;

public interface ReceiveTask {
	
	void receive(ServerConnection con,DAO dao, DataInputStream in) throws Exception;

}
