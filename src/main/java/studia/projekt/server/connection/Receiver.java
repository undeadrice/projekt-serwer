package studia.projekt.server.connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class Receiver {

	private final ArrayBlockingQueue<Bundle> workQueue;
	private final DataInputStream din;
	private final ServerConnection con;

	public Receiver(ServerConnection con, ArrayBlockingQueue<Bundle> workQueue, DataInputStream din) {
		super();
		this.con = con;
		this.workQueue = workQueue;
		this.din = din;
	}

	
	/**
	 * odczytuje identyfikujący bajt i na jego podstawie wywołuje odpowiednią metodę
	 * każda z tych metod przekazuje zadanie dalej przez obiekt Bundle do kolejki workQueue
	 */
	public void performNextRec() throws IOException, SQLException, InterruptedException {
		System.out.println("waiting");
		byte b = din.readByte();
		System.out.println("rec: " + b);
		switch (b) {
		case ServerConnection.loginByte:
			onLoginRec();
			break;
		case ServerConnection.createByte:
			onCreateAccountRec();
			break;
		case ServerConnection.getMeasurementsByte:
			onGetMeasurementsByteRec();
			break;
		case ServerConnection.addMeasurementByte:
			onAddMeasurementByteRec();
			break;
		case ServerConnection.editMeasurementByte:
			onEditMeasurementByteRec();
			break;
		case ServerConnection.removeMeasurementByte:
			onRemoveMeasurementByteRec();
			break;
		case ServerConnection.logoutByte:
			onLogoutByteRec();
			break;

		}
	}

	/**
	 * odbiera login oraz hasło, następnie przekazuje je przez obiekt bundle do kolejki pracy w celu walidacji
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void onLoginRec() throws IOException, InterruptedException {
		String[] arr = new String[2];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = din.readUTF();
		}
		Bundle b = new Bundle(ServerConnection.loginByte);
		b.putString("login", arr[0]);
		b.putString("password", arr[1]);
		workQueue.put(b);
	}

	/**
	 * pobiera informacje dot. nowego konta jak login, imie itp i przekazuje zadanie dalej
	 */
	private void onCreateAccountRec() throws IOException, SQLException, InterruptedException {
		Bundle b = new Bundle(ServerConnection.createByte);
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

	/**
	 * 
	 * @throws InterruptedException
	 */
	private void onGetMeasurementsByteRec() throws InterruptedException {
		Bundle b = new Bundle(ServerConnection.getMeasurementsByte);
		workQueue.put(b);
	}

	/**
	 * odbiera datę od klienta dla której ma być utworzony wpis, i przekazuje zadanie dalej
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void onAddMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(ServerConnection.addMeasurementByte);
		b.putLong("date", din.readLong());
		workQueue.put(b);

	}

	/**
	 * pobiera wszystkie informacje dotyczące wpisu, i przekazuje zadanie dalej
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void onEditMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(ServerConnection.editMeasurementByte);

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
		System.out.println("wrok done");
	}

	/**
	 * pobiera id wpisu które należy usunąć
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void onRemoveMeasurementByteRec() throws IOException, InterruptedException {
		Bundle b = new Bundle(ServerConnection.removeMeasurementByte);
		b.putInt("id", din.readInt());
		workQueue.put(b);
	}

	/**
	 * 
	 * @throws InterruptedException
	 */
	private void onLogoutByteRec() throws InterruptedException {
		Bundle b = new Bundle(ServerConnection.logoutByte);
		workQueue.put(b);

	}
}
