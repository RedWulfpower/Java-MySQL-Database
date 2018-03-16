package de.redwulfpower.Database.SelectResult.types;

import java.util.HashMap;
import java.util.Map;

public class ResultKeyArray {

	private final String[] headlines;
	private final String[][] data;
	
	private int rowid;
	
	public ResultKeyArray(String[] headlines, String[][] data){
		
		this.headlines = headlines;
		this.data = data;
		rowid = -1;
		
	}
	public String[] get_headlines(){
		return headlines;
	}
	
	
	public void first(){
		rowid = 0;
	}
	public void last(){
		rowid = data.length-1;
	}

	public void set(int row){
		
		if(row >= 0 && row < data.length){
			rowid = row;
		}
	}
	
	public String[] getRow(int row){
		if(row >= 0 && row < data.length){
			rowid = row;
			return data[row];
		}
		return null;
	}

	public Map<String, String> get(){
		if(rowid < 0)rowid = 0;
		
		Map<String, String> map = new HashMap<String, String>();
		
		for(int i=0;i<data[rowid].length;i++){
			map.put(headlines[i],data[rowid][i]);
		}
		
		return map;
	}
	
	public boolean next(){
		
		if(rowid+1 >= data.length){
			return false;
		}
		rowid++;
		return true;
	}
	
	
}
