package studia.projekt.server.connection;

import java.io.DataInputStream;

public interface ReceiveTask {
	
	void receive(DataInputStream in);

}
