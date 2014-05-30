package com.roman.ppaper.helpers.xmlsql;


public class SQLITECollumn {
	public enum Type{
		TEXT,
		INTEGER,
		REAL,
		NUMERIC,
		DEFAULT
	}
	String  collumnName;
	Type    type;
	
	//Constraints
	boolean primaryKey;
	boolean autoIncrement;
	
	public SQLITECollumn(String collumnName, Type type, boolean primaryKey,
			boolean autoIncrement) {
		super();
		this.collumnName   = collumnName;
		this.type          = type;
		this.primaryKey    = primaryKey;
		this.autoIncrement = autoIncrement;
	}
	
	public String SQLString(){
		String typeName      = type == Type.DEFAULT ? "" : " " + type.name();
		String primaryKey    = !this.primaryKey     ? "" : " PRIMARY KEY " ;
		String autoincrement = !autoIncrement       ? "" : " AUTOINCREMENT ";
		
		return collumnName + " " 
				+ typeName
				+ primaryKey
				+ autoincrement;
	}
	
}

