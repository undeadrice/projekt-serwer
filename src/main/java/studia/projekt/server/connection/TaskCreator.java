package studia.projekt.server.connection;

public class TaskCreator {

	public static final byte loginByte = 0x01;
	public static final byte createByte = 0x02;
	public static final byte getMeasurementsByte = 0x03;
	public static final byte addMeasurementByte = 0x04;
	public static final byte editMeasurementByte = 0x05;
	public static final byte removeMeasurementByte = 0x06;
	public static final byte logoutByte = 0x07;
	
	
	
	
	public TaskCreator() {
		
		
	}
	
	
	
	public void getTask(byte b) {
		switch(b) {
		case loginByte:
			break;
		case createByte:
			break;
		case getMeasurementsByte:
			break;
		case addMeasurementByte:
			break;
		case editMeasurementByte:
			break;
		case removeMeasurementByte:
			break;
		case logoutByte:
			break;
		
		}
		
		
	}
	
	
	
}
