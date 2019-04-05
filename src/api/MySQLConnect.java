package api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MySQLConnect implements Serializable{

	private String timezone = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private String ip;
	private String database;
	private String user;
	private String password;
	private boolean connect;
	private Statement stmt = null;
	private ResultSet rs = null;
	private transient PropertyChangeSupport propertyChangeListener = new PropertyChangeSupport(this);
	
	
	public MySQLConnect() {
		this.addPropertyChangeListener(new SaveQuery());
	}

	public MySQLConnect(String ip, String database, String user, String password) {
		
		this.ip = ip;
		this.database = database;
		this.user = user;
		this.password = password;
		this.addPropertyChangeListener(new SaveQuery());
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public List<List> executeQuery(String query) {
		
		Date today = Calendar.getInstance().getTime();
		String typeQuery = query.split(" ")[0].toLowerCase();
		String stringQuery = query;
		int numColumns = 0;
		
		switch(typeQuery) {
		  case "select":
			   return select(stringQuery, typeQuery, today, numColumns);
		case "insert":
			  insert(typeQuery, stringQuery, today);
		    break;
		  case "delete":
			    delete(typeQuery, stringQuery, today, numColumns);
			    break;
		  case "update":
			    update(typeQuery, stringQuery, today, numColumns);
			    break;
		  case "call":
			    call(stringQuery, typeQuery, today, numColumns);
			    break;
		  default:
		    // code block
		}		
		
		return null;
	}
	
	private List<List> select(String query, String typeQuery, Date today, int numColumns) {

		List<List> result = new ArrayList<List>();
		
		try{  
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			
			Connection con=DriverManager.getConnection("jdbc:mysql://" +this.ip+ "/"+this.database+this.timezone, this.user, this.password);
	
			stmt = con.createStatement();
		    rs = stmt.executeQuery(query);

		    if (stmt.execute(query)) {
		        rs = stmt.getResultSet();
	
		        rs.last();
		        numColumns = rs.getRow();
		        rs.beforeFirst();
		        
		        while(rs.next()) {
		    		List<String> row = new ArrayList<String>();
			        for  (int i = 1; i<= rs.getMetaData().getColumnCount(); i++){
			            //System.out.println(rs.getMetaData().getColumnName(i) + ": " + rs.getString(rs.getMetaData().getColumnName(i)));
			            row.add(rs.getString(rs.getMetaData().getColumnName(i)));
			        }
			        
			        result.add(row);			        
		        }
		    }
		    
		    rs.close();
		    con.close();
		    
		    Query newQuery = new Query(this.user, typeQuery, query, today, numColumns, this.database);
		    propertyChangeListener.firePropertyChange("Query", "", newQuery);
		    
		    return result;
		    
			}catch(Exception e){ 
				System.out.println(e);
				return null;
			}
	}
	
	private void insert(String typeQuery, String query, Date today) {
		try
	    {

	      Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	      Connection con=DriverManager.getConnection("jdbc:mysql://" +this.ip+ "/"+this.database+this.timezone, this.user, this.password);
	      
	      stmt = con.createStatement();

	      stmt.executeUpdate(query);

	      con.close();
	      
		    Query newQuery = new Query(this.user, typeQuery, query, today, 1, this.database);
		    propertyChangeListener.firePropertyChange("Query", "", newQuery);
	    }
	    catch (Exception e)
	    {
	    	System.out.println(e);
	    }
	}
	
	private void update(String typeQuery, String query, Date today, int numColumns) {
		try
	    {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		    Connection con=DriverManager.getConnection("jdbc:mysql://" +this.ip+ "/"+this.database+this.timezone, this.user, this.password);
		      
		    stmt = con.createStatement();
	   
		    stmt.executeUpdate(query);
		    
		    numColumns = stmt.getUpdateCount();
	      
		    con.close();
		    
		    Query newQuery = new Query(this.user, typeQuery, query, today, numColumns, this.database);
		    propertyChangeListener.firePropertyChange("Query", "", newQuery);
		    
	    }
	    catch (Exception e)
	    {
	    	System.out.println(e);
	    }
	}
	
	private void delete(String typeQuery, String query, Date today, int numColumns) {
		try
	    {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		    Connection con=DriverManager.getConnection("jdbc:mysql://" +this.ip+ "/"+this.database+this.timezone, this.user, this.password);
	
		    PreparedStatement preparedStmt = con.prepareStatement(query);
		    preparedStmt.execute();
		    
		    numColumns = preparedStmt.getUpdateCount();
		      
		    con.close();
		      
		    Query newQuery = new Query(this.user, typeQuery, query, today, numColumns, this.database);
		    propertyChangeListener.firePropertyChange("Query", "", newQuery);
	    }
	    catch (Exception e)
	    {
	    	System.out.println(e);
	    }
	}
	
private void call(String query, String typeQuery, Date today, int numColumns) {
		
		try{  
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			
			Connection con=DriverManager.getConnection("jdbc:mysql://" +this.ip+ "/"+this.database+this.timezone, this.user, this.password);
	
			stmt = con.createStatement();
		    rs = stmt.executeQuery(query);
		    
	        numColumns = rs.getRow();
		    
		    rs.close();
		    con.close();
		    
		    Query newQuery = new Query(this.user, typeQuery, query, today, numColumns, this.database);
		    propertyChangeListener.firePropertyChange("Query", "", newQuery);
		    
			}catch(Exception e){ 
				System.out.println(e);
			}
	}

	public void searchDatabaseUserType(String database, String user, String type) {
		SaveQuery.searchDatabaseUserType(database, user, type);
	}
	
	public void searchDatabaseUser(String database, String user) {
		SaveQuery.searchDatabaseUser(database, user);
	}
	
	public void searchDatabaseType(String database, String type) {
		SaveQuery.searchDatabaseType(database, type);
	}
	
	protected synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListener.removePropertyChangeListener(l);
	}
	
	protected synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListener.addPropertyChangeListener(l);
	}
}
