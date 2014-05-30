package com.roman.ppaper.helpers.xmlsql;

import java.util.ArrayList;

public class SQLITETable {

	boolean ifNotExists  = false;
	String  databaseName = "";
	String  tableName    = "";
	ArrayList<SQLITECollumn> collumns;
	
	//Foreign Key
	String  foreignTable   = "";
	String  foreignCollumn = "";
	
	public SQLITETable(String tableName) {
		super();
		this.tableName = tableName;
		collumns = new ArrayList<SQLITECollumn>();
	}

	public boolean isIfNotExists() {
		return ifNotExists;
	}

	public SQLITETable setIfNotExists(boolean ifNotExists) {
		this.ifNotExists = ifNotExists;
		return this;
	}
	
	public SQLITETable foreignKey(String foreignTable, String foreignCollumn){
		this.foreignTable   = foreignTable;
		this.foreignCollumn = foreignCollumn;
		return this;
	}

	public SQLITETable add(SQLITECollumn collumn) {
		this.collumns.add(collumn);
		return this;
	}
	
	public String SQLString(){
		String ifNotExists = this.ifNotExists ? "" : "IF NOT EXISTS ";
		String name        = databaseName.isEmpty()  ? tableName + " " : databaseName + "." + tableName + " ";
		String collumns    = this.collumns.size() > 0 ? "(" : "";
		
		String comma = "";
		for (SQLITECollumn collumn : this.collumns) {
			collumns = collumns + comma + collumn.SQLString();
			comma = ", ";
		}
		
		collumns = this.collumns.size() > 0 ? collumns + ")" : "";
		
		return "CREATE TABLE " 
				+ ifNotExists
				+ name
				+ collumns
				+ ";";
	}
	
}