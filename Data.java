package com.roman.ppaper.helpers.xmlsql;

import android.content.ContentValues;
import android.renderscript.Mesh.Primitive;
import android.util.Log;

import com.roman.ppaper.helpers.Node;
import com.roman.ppaper.helpers.xmlsql.SQLITECollumn.Type;

public class Data<T> {
	private static final String TAG = "Data";
	String   name;
	T        value;
	Class<?> type;
	boolean  primaryKey;
	private Type sQLiteType;
	private boolean innerContent;

	public Data(String name, T value, boolean _innerContent) {
		this(name, value, value == null ? null : value.getClass(), _innerContent, false);
	}
	
	public Data(String name, T value, boolean _innerContent, boolean primaryKey) {
		this(name, value, Integer.class, _innerContent, primaryKey);
	}
	
	public Data(String _name, T _value) {
		this(_name, _value, _value == null ? null : _value.getClass());
	}
	
	public Data(String name, T value, Class<?> type) {
		this(name, value, type, false, false);
	}
	
	public Data(String name, T value, Class<?> type, boolean _innerContent, boolean _primaryKey) {
		super();
		this.name         = name;
		this.value        = value;
		this.type         = type == null ? String.class : type;
		this.innerContent = _innerContent;
		this.primaryKey   = _primaryKey;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		if (this.value instanceof Integer
				|| this.getType().equals(int.class)
				|| this.getType().equals(Integer.class))
			return (T) DataSet.toInteger(value);

		else if (this.value instanceof Float
				|| this.getType().equals(float.class)
				|| this.getType().equals(Float.class))
			return (T) DataSet.toFloat(value);
		else{
			Log.d(TAG, "give unknown Type: " + type.toString());
			return (T) value.toString();
		}
	}

	@SuppressWarnings("unchecked")
	public void setValue(T value) {
		if (this.value instanceof Integer
				|| this.getType().equals(int.class)
				|| this.getType().equals(Integer.class))
			this.value = (T) DataSet.toInteger(value);

		else if (this.value instanceof Float
				|| this.getType().equals(float.class)
				|| this.getType().equals(Float.class))
			this.value = (T) DataSet.toFloat(value);
		else{
			this.value = (T) value;
			Log.d(TAG, "got unknown Type: " + type.toString());
		}
	}

	public void set(Object value) {
		
		Log.d(TAG, "change value (" + getName() + ") to: " + value);
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Type getSQLiteType() {
		if(sQLiteType != null)
			return sQLiteType;
		
		sQLiteType = Type.DEFAULT;

		if (getType() != null) {
			if (this.value instanceof String
					|| this.getType().equals(String.class))
				sQLiteType = Type.TEXT;

			if (this.value instanceof Integer
					|| this.getType().equals(int.class)
					|| this.getType().equals(Integer.class))
				sQLiteType = Type.INTEGER;

			if (this.value instanceof Float
					|| this.getType().equals(float.class)
					|| this.getType().equals(Float.class))
				sQLiteType = Type.REAL;
		}
		return sQLiteType;
	}
	
	public SQLITECollumn createColumn(){
		SQLITECollumn column;
		Type type = getSQLiteType();
		column    = new SQLITECollumn(name, type, primaryKey, primaryKey);
		
		return column;
	}

	public String toString(){
		  return value.toString();
	}
	
	public static Data<?> makeData(String _name, Object _value, Class<?> _type){
		if (_value instanceof Float
				|| _type.equals(float.class)
				|| _type.equals(Float.class))
			return new Data<Float>(_name, (Float) _value, _type);
		
		if (_value instanceof Integer
				|| _type.equals(int.class)
				|| _type.equals(Integer.class))
			return new Data<Integer>(_name, (Integer) _value, _type);
		
		if (_value instanceof String
				|| _type.equals(String.class))
			return new Data<String>(_name, (String) _value, _type);
		
		return new Data<Object>(_name, _value, _type);
	}
	
//	public ArrayList<Date<?>> selectAll(Cursor cursor, HashMap<String, Integer> columnId){
//		ArrayList<Date<?>> _result = new ArrayList<Date<?>>();
//		int _column                = columnId.get(getName());
//		
//		for(int i = 0; i < cursor.getCount(); i++){
//			cursor.moveToPosition(i);
//			Date<T> date = null;
//			if(this.type.equals("string"))
//				date = new Date<T>((T) cursor.getString(_column), this);
//			else if(this.type.equals("integer"))
//				date = new Date<T>((T) new Integer(cursor.getInt(_column)), this);
//			else if(this.type.equals("float"))
//				date = new Date<T>((T) new Float(cursor.getFloat(_column)), this);
//			_result.add(date);
//		}
//		return _result;
//	}
	
//	public Date<T> getEmptyDate(){
//		return new Date<T>(null, this);
//	}

	public static Data<?> parseData(Node node) {
		String name  = node.getAttribute("name");
		String type  = node.getAttribute("type");
		String value = node.getValue();
		String cleanValue = DataSet.cleanValue(value);
		
		Data<?> newData = null;
		
		if(type.equals("int") || type.equals("integer"))
			newData = new Data<Integer>(name, DataSet.toInteger(value), Integer.class);
		
		if(type.equals("string"))
			newData = new Data<String>(name, value, String.class);
		
		if(type.equals("float"))
			newData = new Data<Float>(name, DataSet.toFloat(value), Float.class);
		
		return newData;
	}

	/**
	 * @deprecated Use {@link DataSet#cleanValue(String)} instead
	 */
	public static String cleanValue(String _value) {
		return DataSet.cleanValue(_value);
	}

	public void put(ContentValues _values, DataSet _content) {
		//String _cleanValue = _value.replaceAll("[A-Za-z_ \t\r\n\f]", "");
		String _name = getName();
		
		switch (sQLiteType.TEXT) {
		case INTEGER:
			Integer _asInteger = _content.getAsInteger(_name);
			_values.put(_name, _asInteger);
			break;
			
		case TEXT:
			String _asString = _content.getAsString(_name);
			_values.put(_name, _asString);
			break;
			
		case REAL:
			Float _asFloat = _content.getAsFloat(_name);
			_values.put(_name, _asFloat);
			break;
		default:
			_values.put(_name,_content.getAsString(_name));
			Log.d(TAG, "Can't convert this nor put it.");
		}
	}
	
	public float toFloat(){
		return DataSet.toFloat(value);
	}
	
	public Integer toInteger(){
		return DataSet.toInteger(value);
	}
	
	public Boolean toBoolean(){
		return DataSet.toBoolean(value);
	}
	
	
	/**
	 * @return the innerContent
	 */
	public boolean isInnerContent() {
		return innerContent;
	}
}
