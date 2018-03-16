package de.redwulfpower.Database.SelectResult.types;

public class ResultArray {

	private final String[] headlines;
	private final String[][] data;
	
	private int rowid;
	
	public ResultArray(String[] headlines, String[][] data){
		
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

	public String[] get(){
		
		if(data == null){
			return null;
		}
		
		if(rowid >= data.length){
			return null;
		}
		// für -> rsk.get();   ohne   rsk.next(); rsk.get();
		if(rowid == -1){
			return data[0];
		}
		return data[rowid];
	}
	public boolean next(){
		if(data == null){
			return false;
		}
		
		if(rowid+1 > data.length){
			return false;
		}
		rowid++;
		return true;
	}
	
}
