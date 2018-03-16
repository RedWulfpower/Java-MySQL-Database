package de.redwulfpower.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.redwulfpower.Database.MySQLUtils.MySQLUtils;
import de.redwulfpower.Database.SelectResult.SelectResult;


public class Database{
	
	private Connection 	con;
	private Statement 	stmt;
	private String		prefix;
	private int			LastInsertid;
	
	
	private String 		ip;
	private String 		Database;
	private String 		user;
	private String 		pw;

	public Database(String ip, String Database, String user, String pw, String prefix){
		try {
			Class.forName( "org.gjt.mm.mysql.Driver" );
		} catch (ClassNotFoundException e) {
		//	System.out.println("Kann den MySql Treiber nicht laden!");
		//	e.printStackTrace();
			try {
				Class.forName( "com.mysql.jdbc.Driver" );
			} catch (ClassNotFoundException f) {
				System.out.println("Kann den MySql Treiber nicht laden!");
			//	f.printStackTrace();
			}
		}
		this.ip = ip;
		this.Database = Database;
		this.user = user;
		this.pw = pw;
		
		this.connect();
		this.prefix = prefix;
	}
	private void connect(){
		
		this.con = null;

		try {
			// "jdbc:mysql://localhost:3306/itknow","root",""
			this.con = DriverManager.getConnection("jdbc:mysql://"+ this.ip +"/"+ this.Database + "?AutoReconnect=yes", this.user, this.pw);
			this.stmt = con.createStatement();
			
		} catch (SQLException e) {
			System.err.println("Fehler bei der Verbindung zum Mysql Server");
			e.printStackTrace();
		}
		
	}
	
	
	public void close(){
		if ( this.con != null ){
			try {
				this.con.close(); 
			} catch ( SQLException e ) { 
				System.err.println("kann die MySql Verbindung nicht schliessen!");
				//e.printStackTrace(); 
			}
		}
	}
	public String real_escape_string(String str)
	{
		try {
			return MySQLUtils.mysql_real_escape_string(this.con, str);
		} catch (Exception e) {
			System.out.println("Fehler bei mysql_real_escape_string!");
			e.printStackTrace();
		}
		return str;
	}
	
	public int getInsertid(){
		return this.LastInsertid;
	}
	
	public String getprefix(){
		return this.prefix;
	}
	public String[] runSQLQuery(String Sql, String art){
		if(this.con != null){
			
			try {
				if(!con.isValid(2)){
					this.connect();
				}
			} catch (SQLException e1) {
				this.connect();
				e1.printStackTrace();
			}
			
			try {
				this.stmt.executeUpdate(Sql);
			} catch (SQLException e) {
				System.out.print("Fehler bei SELECT! sql: " + Sql);
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	/*
	 * 2 d Array
	 */
	private String getWhere(String[][] where){
		if(where == null){
			return "";
		}
		
		StringBuilder Sql = new StringBuilder(" WHERE ");

		if(where.length > 0 && where[0].length > 0){
			for(int i=0;i<where.length;i++){
				
				if(i != 0){
					if(where[i].length == 1){
						Sql.append(" "+where[i][0]+" ");
					
					}else if(where[i-1].length != 1){
						Sql.append(" AND ");
					}
				}
				if(where[i].length >1){
					if(where[i].length == 2){
						Sql.append(" BINARY `"+this.real_escape_string(where[i][0])+"` = '"+this.real_escape_string(where[i][1])+"'");
						
					}else{
						Sql.append(" BINARY `"+this.real_escape_string(where[i][0])+"` "+this.real_escape_string(where[i][1])+" '"+this.real_escape_string(where[i][2])+"'");
					}
				}		
			}
		}
		return Sql.toString();
	}
	
	

	/*
	 * Select nur mit Query String
	 * */
	public SelectResult Select(String query){
		
		try {
			if(stmt.isClosed()){
				this.connect();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		try {
			return new SelectResult(stmt.executeQuery(query));
				
		} catch (SQLException e) {
			System.out.print("Fehler bei SELECT! sql: "+query);
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * Select String FROM String
	 * */
	public SelectResult Select(String was, String von){

		if(was.equals("*")){
			
			return this.Select("SELECT "+ this.real_escape_string(was)+""
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " );
			
		}else{

			return this.Select("SELECT `"+ this.real_escape_string(was)+"`"
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " );
			
		}
	}
	public SelectResult Select(String was, String von, String[][] where){

		if(was.equals("*")){
			
			return this.Select("SELECT "+ this.real_escape_string(was)+""
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " 
					+ this.getWhere(where));
			
		}else{

			return this.Select("SELECT `"+ this.real_escape_string(was)+"`"
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " 
					+ this.getWhere(where));
			
		}
	}
	/*
	 * Select String FROM String ORDER/GROUB BY / ..
	 * */
	public SelectResult Select(String was, String von, String[][] where, String sonst){
		
		if(was.equals("*")){
			
			return this.Select("SELECT "+ this.real_escape_string(was)+""
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " 
					+ this.getWhere(where) + " " + sonst);
			
		}else{
			
			return this.Select("SELECT `"+ this.real_escape_string(was)+"`"
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " 
					+ this.getWhere(where) + " " + sonst);
			
		}
		
		
	}
	/*
	 * Select String FROM String ORDER/GROUB BY / ..
	 * */
	public SelectResult Select(String was, String von, String whereKey, String whereVal){
		
		if(was.equals("*")){
			
			return this.Select("SELECT "+ this.real_escape_string(was)+""
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " 
					+ this.real_escape_string(whereKey) + " = '" + this.real_escape_string(whereVal) + "'");
			
		}else{
			
			return this.Select("SELECT `"+ this.real_escape_string(was)+"`"
					+ " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " 
					+ this.real_escape_string(whereKey) + " = '" + this.real_escape_string(whereVal) + "'");
			
		}
		
		
	}
	/*
	 * Select Array FROM String
	 * */
	public SelectResult Select(String[] was, String von){
		String query = "SELECT ";
		
		for(int i=0;i<was.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " `"+this.real_escape_string(was[i])+"` ";
		}
		
		return this.Select(query + " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` ");
	}
	public SelectResult Select(String[] was, String von, String[][] where){
		String query = "SELECT ";
		
		for(int i=0;i<was.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " `"+this.real_escape_string(was[i])+"` ";
		}
		
		return this.Select(query + " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " + this.getWhere(where));
	}
	/*
	 * Select Array FROM String ORDER/GROUB BY / ..
	 * */
	public SelectResult Select(String[] was, String von, String[][] where, String sonst){
		String query = "SELECT ";
		
		for(int i=0;i<was.length;i++){
			if(i != 0){
				query += ",";
			}
			query += " `"+this.real_escape_string(was[i])+"` ";
		}
		
		return this.Select(query + " FROM `"+ this.real_escape_string(this.getprefix()+von) +"` " + this.getWhere(where) + " " + sonst);
	}
	
	
	
	/*
	 * Delet nur mit Query String
	 * */
	public int Delete(String query){
		try {
			if(stmt.isClosed()){
				this.connect();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
				return stmt.executeUpdate(query);
				
		} catch (SQLException e) {
			System.out.println("Fehler beim DELEAT! Sql: "+ query);
			e.printStackTrace();
		}
		return 0;
	}
	/*
	 * Delete FROM String
	 * */
	public int Delete(String wo, String[][] where){
		
		return this.Delete("DELETE FROM "+this.real_escape_string(this.getprefix()+wo)+" "+this.getWhere(where));
	}
	/*
	 * Delet FROM String ORDER/GROUB BY / ..
	 * */
	public int Delete(String wo, String[][] where, String sonst){
		
		return this.Delete("DELETE FROM "+this.real_escape_string(this.getprefix()+wo)+" "+this.getWhere(where) + " " + sonst);
	}
	
	
	
	/*
	 * INSERT INTO Query
	 */
	public int Insert(String query){
		try {
			if(stmt.isClosed()){
				this.connect();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			int exU = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()){
				this.LastInsertid=rs.getInt(1);
			}
			rs.close();
			

			return exU;
		} catch (SQLException e) {
			System.out.println("Fehler beim Insert! Sql: "+ query);
			e.printStackTrace();
		}
		return 0;
	}
	/*
	 * INSERT INTO String  VALUES(String)
	 */
	public int Insert(String wo, String value){
		
		return this.Insert("INSERT INTO "+this.real_escape_string(this.getprefix()+wo)+" ");
	}
	/*
	 * INSERT INTO String  VALUES(String)  ORDER/GROUB BY / ..
	 */
	public int Insert(String wo, String value, String[][] where){
		
		return this.Insert("INSERT INTO "+this.real_escape_string(this.getprefix()+wo)
		+" VALUES('" +this.real_escape_string(value)+"') "+this.getWhere(where));
	}
	/*
	 * INSERT INTO String (String)  VALUES(String) 
	 */
	public int Insert(String wo, String headlins, String value){

		return this.Insert("INSERT INTO "+this.real_escape_string(this.getprefix()+wo)
		+"(`"+this.real_escape_string(headlins)+"`) VALUES('" +this.real_escape_string(value)+"') ");
	}
	/*
	 * INSERT INTO String (String)  VALUES(String) ORDER/GROUB BY / ..
	 */
	public int Insert(String wo, String headlins, String value, String[][] where){

		return this.Insert("INSERT INTO "+this.real_escape_string(this.getprefix()+wo)
		+"(`"+this.real_escape_string(headlins)+"`) VALUES('" +this.real_escape_string(value)+"') "+this.getWhere(where));
	}
	/*
	 * INSERT INTO String VALUES(Array)
	 */
	public int Insert(String wo, String[] value){
		
		String query = "INSERT INTO "+this.real_escape_string(this.getprefix()+wo)+" VALUES( ";
		
		for(int i=0;i<value.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " '"+this.real_escape_string(value[i])+"' ";
		}
		
		return this.Insert(query +" ) ");
	}
	/*
	 * INSERT INTO String VALUES(Array) ORDER/GROUB BY / ..
	 */
	public int Insert(String wo, String[] value, String[][] where){
		
		String query = "INSERT INTO "+this.real_escape_string(this.getprefix()+wo)+" VALUES( ";
		
		for(int i=0;i<value.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " '"+this.real_escape_string(value[i])+"' ";
		}
		
		return this.Insert(query +" ) "+this.getWhere(where));
	}
	/*
	 * INSERT INTO String (Array) VALUES(Array)
	 */
	public int Insert(String wo, String[] headlins, String[] value){
		
		String query = "INSERT INTO "+this.real_escape_string(this.getprefix()+wo)+" (";
		
		for(int i=0;i<headlins.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " `"+this.real_escape_string(headlins[i])+"` ";
		}
		
		query += ") VALUES( ";
		for(int i=0;i<value.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " '"+this.real_escape_string(value[i])+"' ";
		}
		
		return this.Insert(query +" ) ");
	}
	/*
	 * INSERT INTO String (Array) VALUES(Array) ORDER/GROUB BY / ..
	 */
	public int Insert(String wo, String[] headlins, String[] value, String[][] where){
		
		String query = "INSERT INTO "+this.real_escape_string(this.getprefix()+wo)+" ";
		
		for(int i=0;i<headlins.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " `"+this.real_escape_string(headlins[i])+"` ";
		}
		
		query += " VALUES( ";
		for(int i=0;i<value.length;i++){
			if(i != 0){
				query += " , ";
			}
			query += " '"+this.real_escape_string(value[i])+"' ";
		}
		
		return this.Insert(query +" ) "+this.getWhere(where));
	}

	/*
	 * INSERT INTO String (Array 2d)
	 */
	public int Insert(String wo, String[][] headValues){
	
		StringBuilder headlines = new StringBuilder();
		StringBuilder values = new StringBuilder();
		
		for(int i=0;i<headValues.length;i++){
			
			if(headValues[i].length == 2){
				if(i != 0){
					headlines.append(",");
					values.append(",");
				}
				headlines.append("`"+this.real_escape_string(headValues[i][0])+"`");
				values.append("'"+this.real_escape_string(headValues[i][1])+"'");
			}
		}
		String query = "INSERT INTO "+this.real_escape_string(this.getprefix()+wo)+" ("+headlines.toString()+") VALUES ("+values.toString()+") ";
		
		return this.Insert(query);
	}
	
	
	
	
	
	/*
	 * UPDATE Query
	 */
	public int Update(String query){
		try {
			if(stmt.isClosed()){
				this.connect();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			return stmt.executeUpdate(query);
			
		} catch (SQLException e) {
			System.out.println("Fehler beim Update! Sql: "+ query);
			e.printStackTrace();
		}
		return 0;
	}
	/*
	 * UPDATE (String) SET (String = String)
	 */
	public int Update(String wo, String wert, String value){
		
		return this.Update("UPDATE "+this.real_escape_string(this.getprefix()+wo)+" SET `"
				+this.real_escape_string(wert)+"` = '"+this.real_escape_string(value)+"'" );
	}
	/*
	 * UPDATE (String) SET (String = String) WHERE 
	 */
	public int Update(String wo, String wert, String value, String[][] where){
		
		return this.Update("UPDATE "+this.real_escape_string(this.getprefix()+wo)+" SET `"
				+this.real_escape_string(wert)+"` = '"+this.real_escape_string(value)+"'"+this.getWhere(where) );
	}
	/*
	 * UPDATE (String) SET (Array)
	 */
	public int Update(String wo, String[][] value){
		StringBuilder Sql = new StringBuilder("UPDATE "+this.real_escape_string(this.getprefix()+wo)+" SET ");
		
		if(value.length > 0 && value[0].length > 0){
			for(int i=0;i<value.length;i++){
				
				if(i != 0){
					Sql.append(" , ");
				}
				if(value[i].length == 2){
					Sql.append("`"+this.real_escape_string(value[i][0])+"` = '"+this.real_escape_string(value[i][1])+"'");						
				}
			}
		}
		
		return this.Update(Sql.toString());
	}
	/*
	 * UPDATE (String) SET (Array) WHERE 
	 */
	public int Update(String wo, String[][] value, String[][] where){
		StringBuilder Sql = new StringBuilder("UPDATE "+this.real_escape_string(this.getprefix()+wo)+" SET ");
		
		if(value.length > 0 && value[0].length > 0){
			for(int i=0;i<value.length;i++){
				
				if(i != 0){
					Sql.append(" , ");
				}
				if(value[i].length == 2){
					Sql.append("`"+this.real_escape_string(value[i][0])+"` = '"+this.real_escape_string(value[i][1])+"'");						
				}
			}
		}
		
		return this.Update(Sql.toString()+this.getWhere(where));
	}
	
}
