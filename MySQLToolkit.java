import java.sql.*;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * <p>This class is created to simplify connecting and querying to MySQL or MariaDB database.<br><br>
 * FAQ: What is binder?<br>
 * Answer: By nature, it a Object-type Vector variable.<br>
 * Since it is Object-type, you can add any type of data to the Vector.<br>
 * Binder is needed for creating a SQL injection-proof PreparedStatement.<br>
 * For more info, research about what SQL injection is and ways to prevent it.<br><br>
 * For questions and clarifications, please chat me on Facebook Messenger: m.me/luchmewep.<br>
 * Required: JDBC
 * </p>
 * @author James Carlo Luchavez
 * @version 1
 */
public class MySQLToolkit {
	private String db_host, db_name, db_username, db_password;
	private Connection con;
	private PreparedStatement pst;
	private ResultSetMetaData rsmd;
	@SuppressWarnings("rawtypes")
	private Vector tblHeader, columnData, rowData;
	@SuppressWarnings("rawtypes")
	private Vector<Vector> tblRows;

	/**
	 * <p>No default database info.<br>
	 * Provide values for all parameters.
	 * </p>
	 * @param db_host (String)
	 * @param db_name (String)
	 * @param db_username (String)
	 * @param db_password (String)
	 */
	public MySQLToolkit(String db_host, String db_name, String db_username, String db_password) {
		this.db_host = db_host;
		this.db_name = db_name;
		this.db_username = db_username;
		this.db_password = db_password;
	}

	/**
	 * <p>Default Host: "localhost"<br>
	 * Default Username: "root"<br>
	 * Provide values for other parameters.
	 * </p>
	 * @param db_name (String)
	 * @param db_password (String)
	 */
	public MySQLToolkit(String db_name, String db_password) {
		this.db_host = "localhost";
		this.db_name = db_name;
		this.db_username = "root";
		this.db_password = db_password;
	}

	/**
	 * <p>Default Host: "localhost"<br>
	 * Default Username: "root"<br>
	 * Default Password: ""<br>
	 * Provide values for other parameters.
	 * </p>
	 * @param db_name (String)
	 */
	public MySQLToolkit(String db_name) {
		this.db_host = "localhost";
		this.db_name = db_name;
		this.db_username = "root";
		this.db_password = "";
	}

	public void testConnection() {
		openDatabase();
		System.out.println("Connected successfully.");
		closeDatabase();
	}
	
	/**
	 * Returns PreparedStament (does not require binder)
	 * @param sql (String)
	 * @return PreparedStatement
	 */
	public PreparedStatement getPST(String sql) {
		return getPST(sql, null);
	}

	/**
	 * Returns PreparedStatement (binder required)
	 * @param sql (String)
	 * @param binder (Object-type Vector)
	 * @return PreparedStatement
	 */
	@SuppressWarnings("rawtypes")
	public PreparedStatement getPST(String sql, Vector binder) {
		openDatabase();
		try {
			pst = con.prepareStatement(sql);
			if(binder != null) {
				for (int i = 0; i < binder.size(); i++) {
					pst.setObject(i+1, binder.get(i));
				}
			}
			return pst;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns true for success or false for fail from PreparedStatement
	 * @param pst
	 * @return boolean
	 */
	public boolean executePST(PreparedStatement pst) {
		try {
			if(pst.executeUpdate() == 1) {
				return true;
			}
			else return false;
		} catch (Exception e) {
			System.err.println("Error @executePST: "+e.getMessage());
			return false;
		}
	}

	/**
	 * Returns ResultSet from PreparedStatement
	 * @param pst (PreparedStatement)
	 * @return ResultSet
	 */
	public ResultSet getRS(PreparedStatement pst) {
		try {
			return pst.executeQuery();
		} catch (Exception e) {
			System.err.println("Error @getRS: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns one row from the ResultSet
	 * @param rs (ResultSet)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector getRow(ResultSet rs) {
		try {
			rowData = new Vector<>();
			int columns = rs.getMetaData().getColumnCount();
			while(rs.next()) {
				for (int i = 1; i <= columns; i++) {
					rowData.add(rs.getObject(i));
				}
				break;
			}
			return rowData;
		} catch (Exception e) {
			System.err.println("Error @executeRow: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns one column from the ResultSet
	 * @param rs (ResultSet)
	 * @param columnName (String)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" } )
	public Vector getColumn(ResultSet rs, String columnName) {
		try {
			columnData = new Vector<>();
			while(rs.next()) {
				columnData.add(rs.getObject(columnName));
			}
			return columnData;
		} catch (Exception e) {
			System.err.println("Error @executeColumn: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns column names from the ResultSet
	 * @param rs (ResultSet)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getColumnNames(ResultSet rs){
		try {
			tblHeader = new Vector<>();
			rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				tblHeader.add(rsmd.getColumnName(i));	
			}
			return tblHeader;
		} catch (Exception e) {
			System.err.println("Error @getColumnNames: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns column labels from the ResultSet
	 * @param rs (ResultSet)
	 * @return Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getColumnLabels(ResultSet rs){
		try {
			tblHeader = new Vector<>();
			rsmd = rs.getMetaData();
			int columns = rsmd.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				tblHeader.add(rsmd.getColumnLabel(i));				
			}
			return tblHeader;
		} catch (Exception e) {
			System.err.println("Error @getColumnLabels: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns all rows from the ResultSet
	 * @param rs (ResultSet)
	 * @return 2D Vector (Object-type)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector<Vector> getRows(ResultSet rs){
		try {
			tblRows = new Vector<>();
			int columns = rs.getMetaData().getColumnCount();
			int row = 0;
			while (rs.next()) {
				tblRows.add(new Vector<>());
				for (int i = 1; i <= columns; i++) {
					tblRows.get(row).add(rs.getObject(i));
				}
				row++;
			}
			return tblRows;
		} catch (Exception e) {
			System.err.println("Error @getRows: "+e.getMessage());
			return null;
		}
	}

	/**
	 * Returns JTable model from a ResultSet
	 * @param rs (ResultSet)
	 * @return DefaultTableModel for JTable
	 */
	public DefaultTableModel getTableModel(ResultSet rs) {
		//Instantiate Vectors
		tblHeader = new Vector<>();
		tblRows = new Vector<>();

		//Get Column Names
		tblHeader = getColumnLabels(rs);

		//Get Rows from Result Set
		tblRows = getRows(rs);

		return new DefaultTableModel(tblRows, tblHeader);
	}

	/**
	 * Returns JComboBox model from an Object-type Vector
	 * @param v (Object-type Vector)
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultComboBoxModel getComboBoxModel(Vector v) {
		return new DefaultComboBoxModel<>(v);
	}

	//On and Off Switch for Database

	private void openDatabase() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+db_host+":3306/"+db_name+"?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&allowMultiQueries=true", db_username, db_password);
		} catch (Exception e) {
			System.err.println("Error @openDatabase: "+e.getMessage());
		}
	}
	private void closeDatabase() {
		try {
			con.close();
		} catch (Exception e) {
			System.err.println("Error @closeDatabase: "+e.getMessage());
		}
	}
}

