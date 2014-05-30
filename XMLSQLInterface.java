package com.roman.ppaper.helpers.xmlsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.UiThreadTest;
import android.util.Log;


public class XMLSQLInterface  {
	
	private static final String TAG = "XMLSQLInterface";
	private Context context;
	private EntityManager manager;
	
	private openHelper     openHelper;
	private SQLiteDatabase db;
	
	
	public XMLSQLInterface(Context context, EntityManager manager)  {
		super();
		this.context = context;
		this.manager = manager;
		
		
		open();  //Datenbank öffnen und vielleicht auch erstellen
		close();
		
	}

	public void open() {
		try{
			openHelper = new openHelper();
			db = openHelper.getWritableDatabase();
			//Log.d("sql", db.)
			Log.d(TAG, "interface ist da");
		}catch(Exception e){
			e.printStackTrace();
			Log.d(TAG, "kein SQL");
		}
	}
	
	
	//Unsafe
	Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		open();
		Cursor _cursor = db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		Log.d(TAG, "made query in: " + table + " (" + _cursor.getColumnNames() + " | " + selection + ")");
		
		
		return _cursor;
	}
	
	
	
	/**
	 * @param table
	 * @param nullColumnHack
	 * @param values
	 * @return
	 * @see android.database.sqlite.SQLiteDatabase#insert(java.lang.String,
	 *      java.lang.String, android.content.ContentValues)
	 */
	public long insert(String table, String nullColumnHack, ContentValues values) {
		open();
		long _id = db.insert(table, nullColumnHack, values);
		close();
		Log.d(TAG, "(" + _id + ")insert in: " + table + " with data "
				+ values.toString());
		return _id;
	}


	/**
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 * @see android.database.sqlite.SQLiteDatabase#update(java.lang.String, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		open();
		int _id = db.update(table, values, whereClause, whereArgs);
		close();
		return _id;
	}

	public void close() {
		db.close();
	}

	class openHelper extends SQLiteOpenHelper{
		
		public openHelper() {
			super(context, manager.getRootName(), null, 2);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String[] _sql = manager.createTablesSQL();
			for (String string : _sql) {
				try {
					db.execSQL(string);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d(TAG, "executed: " + string);
			}
			
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			super.onOpen(db);			
		}
	}
	
	public static boolean checkDataBase(String name) {
	    SQLiteDatabase checkDB = null;
	    try {
	        checkDB = SQLiteDatabase.openDatabase(name, null,
	                SQLiteDatabase.OPEN_READONLY);
	        checkDB.close();
	    } catch (SQLiteException e) {
	        // database doesn't exist yet.
	    }
	    return checkDB != null ? true : false;
	}
}
