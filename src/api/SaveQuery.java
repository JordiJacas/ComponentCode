package api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class SaveQuery implements PropertyChangeListener{
	
	private static List<Query> querys = new ArrayList<Query>();
	
	public SaveQuery() {}
	
	private static void addQuery(Query query) {
		querys.add(query);
	}
	
	protected static void searchDatabaseUser(String database, String user) {
		
		for (Query query : querys) {
			if(query.getDatabase().toLowerCase().equals(database.toLowerCase()) && query.getUserName().toLowerCase().equals(user.toLowerCase())) {
				System.out.println("Query: " + query.getQuery() + " - Time: " + query.getDate() + " - Type query: " + query.getTypeQuery());
			}
		}
	}
	
	protected static void searchDatabaseUserType(String database, String user, String type) {
		
		for (Query query : querys) {
			if(query.getDatabase().toLowerCase().equals(database.toLowerCase()) 
					&& query.getUserName().toLowerCase().equals(user.toLowerCase()) &&
					query.getTypeQuery().toLowerCase().equals(type.toLowerCase())) {
				
				System.out.println("Query: " + query.getQuery() + "- Time: " + query.getDate());
			}
		}
	}
	
	protected static void searchDatabaseType(String database, String type) {
		
		for (Query query : querys) {
			if(query.getDatabase().toLowerCase().equals(database.toLowerCase()) && query.getTypeQuery().toLowerCase().equals(type.toLowerCase())) {
				System.out.println("Query: " + query.getQuery() + " - Time: " + query.getDate() + " - User: " + query.getUserName());
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		addQuery((Query) e.getNewValue());
	}

}
