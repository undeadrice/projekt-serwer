package studia.projekt.server.connection;

import java.io.DataOutputStream;

public interface SendTask {
	
	void send(DataOutputStream out);
	
}
