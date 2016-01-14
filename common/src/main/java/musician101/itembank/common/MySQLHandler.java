package musician101.itembank.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLHandler
{
	Connection connection;
	
	String database;
	String hostname;
	String password;
	String port;
	String user;
	
	public MySQLHandler(String database, String hostname, String password, String port, String user)
	{
		this.connection = null;
		this.database = database;
		this.hostname = hostname;
		this.password = password;
		this.port = port;
		this.user = user;
	}
	
	public Connection openConnection() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Drive");
		connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, user, password);
		return connection;
	}
	
	public boolean checkConnection()
	{
		return connection != null;
	}
	
	public Connection getConnection()
	{
		return connection;
	}
	
	public void closeConnection() throws SQLException
	{
		if (connection != null)
			connection.close();
	}
	
	public ResultSet querySQL(String query) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		if (checkConnection())
			c = getConnection();
		else
			c = openConnection();
		
		Statement s = c.createStatement();
		ResultSet rset = s.executeQuery(query);
		closeConnection();
		return rset;
	}
	
	public void updateSQL(String update) throws ClassNotFoundException, SQLException
	{
		Connection c = null;
		if (checkConnection())
			c = getConnection();
		else
			c = openConnection();
		
		Statement s = c.createStatement();
		s.executeUpdate(update);
		closeConnection();
	}
}
