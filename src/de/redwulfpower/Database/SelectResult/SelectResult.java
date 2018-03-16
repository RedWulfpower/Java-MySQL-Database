package de.redwulfpower.Database.SelectResult;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import de.redwulfpower.Database.SelectResult.types.ResultArray;
import de.redwulfpower.Database.SelectResult.types.ResultKeyArray;

public class SelectResult {

	
	public final int num_rows;

	private String[] Headlines;
	private ArrayList<ArrayList<String>> data;
	
	public SelectResult(ResultSet rs){

		int count = 0;
		
		try {
			
			
			//Headlines
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int numberofColums = rsmd.getColumnCount();
		
			ArrayList<String> headlines = new ArrayList<String>();
			
			for(int a=1; a<=numberofColums;a++){
				headlines.add(rsmd.getColumnLabel(a));
			}
			
			this.Headlines = new String[headlines.size()];
			for(int i=0;i<headlines.size();i++){
				Headlines[i] = headlines.get(i);
			}
			
			
			this.data = new ArrayList<ArrayList<String>>();
			ArrayList<String> tmp = new ArrayList<String>();
			
			while(rs.next()){
				for(int b=1; b<=numberofColums;b++){
					tmp.add(rs.getString(b));
				}
				data.add(tmp);
				tmp = new ArrayList<String>();
				count++;
			}
			
			rs.close();
			
		} catch (SQLException e1) {
			this.data = null;
			this.Headlines = null;
			e1.printStackTrace();
		}
		
		num_rows = count;
		
	}

	public String[] get_Headlines(){
		return Headlines;
		
	}

	public ResultArray fetch_array(){
		return new ResultArray(get_Headlines(), fetch_array2d());
	}
	
	public ResultKeyArray fetch_keyarr(){
		return new ResultKeyArray(get_Headlines(), fetch_array2d());
	}
	
	public String[][] fetch_array2d(){
		if(data.size() == 0){
			return null;
		}
		
		String[][] arr = new String[data.size()][data.get(0).size()];

		for(int i=0;i<data.size();i++){
			for(int j=0;j<data.get(i).size();j++){
				arr[i][j] = data.get(i).get(j);
			}
		}
		return arr;
	}

	
	
}
