
public class mysqltoolkitconnectdemo {
	public static void main(String[] args) {
		MySQLToolkit db = new MySQLToolkit("experiment");
		db.testConnection();
	}
}
