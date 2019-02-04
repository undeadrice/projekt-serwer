package studia.projekt.server.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class Sender {

	private final ArrayBlockingQueue<Bundle> sendQueue;
	private final DataOutputStream dout;
	private final ServerConnection con;

	public Sender(ServerConnection con, ArrayBlockingQueue<Bundle> sendQueue, DataOutputStream dout) {
		super();
		this.con = con;
		this.sendQueue = sendQueue;
		this.dout = dout;
	}
	/**
	 * pobiera nastepne zadanie z kolejki sendQueue i je wykonuje
	 * odpowiednia metoda jest wywoływana poprzez identyfikujący bajt
	 */
	public void performNextSend() throws InterruptedException, IOException, SQLException {
		Bundle b = sendQueue.take();
		switch (b.getHeader()) {
		case ServerConnection.loginByte:
			onLoginSend(b);
			break;
		case ServerConnection.createByte:
			onCreateAccountSend(b);
			break;
		case ServerConnection.getMeasurementsByte:
			onGetMeasurementsByteSend(b);
			break;
		case ServerConnection.addMeasurementByte:
			onAddMeasurementByteSend(b);
			break;
		case ServerConnection.editMeasurementByte:
			onEditMeasurementByteSend(b);
			break;
		case ServerConnection.removeMeasurementByte:
			onRemoveMeasurementByteSend(b);
			break;
		case ServerConnection.logoutByte:
			onLogoutByteSend(b);
			break;

		}

	}
	/**
	 * wysyła spowrotem do klienta status logowania, oraz niektóre dane
	 */
	private void onLoginSend(Bundle b) throws IOException, SQLException, InterruptedException {
		dout.writeByte(b.getHeader());
		dout.writeBoolean(b.getBool("status"));
		dout.writeUTF(b.getString("login"));
		dout.writeUTF(b.getString("name"));
		dout.writeUTF(b.getString("surname"));
	}

	/**
	 * wysyła spowrotem do klienta status tworzenia konta oraz login
	 */
	private void onCreateAccountSend(Bundle b) throws IOException, SQLException, InterruptedException {
		dout.writeByte(b.getHeader());
		dout.writeBoolean(b.getBool("status"));
		dout.writeUTF(b.getString("login"));
	}

	/**
	 * wysyła spowrotem do klienta wszystkie wpisy które są związane z obecnie połączonym kontem
	 */
	private void onGetMeasurementsByteSend(Bundle b) throws SQLException, IOException {
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

	/**
	 * wysyła spowrotem do klienta klucz główny nowego wpisu oraz datę tego wpisu
	 */
	private void onAddMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeInt(b.getInt("id"));
		dout.writeLong(b.getLong("date"));
	}

	private void onEditMeasurementByteSend(Bundle b) throws IOException {
		// nic
	}

	
	/**
	 * wysyła klientowi klucz główny usuniętego wpisu
	 */
	private void onRemoveMeasurementByteSend(Bundle b) throws IOException, SQLException {
		dout.writeByte(b.getHeader());
		dout.writeInt(b.getInt("id"));

	}

	private void onLogoutByteSend(Bundle b) {
		// status
	}
}
