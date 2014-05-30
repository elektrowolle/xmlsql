package com.roman.ppaper.helpers.xmlsql;

import java.net.IDN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.R.integer;
import android.content.ContentQueryMap;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseArray;

import com.roman.ppaper.helpers.Node;


public class Entity {
	private static final String TAG = "EntityView";
	HashMap<String, Data<?>> data;
	HashMap<String, Entity>  entities;
	
	Data<Integer> primaryKey;
	
	String name;
	HashMap<String, Entity> nestedIn;

	public Entity(String name) {
		data      = new HashMap<String, Data<?>>();
		entities  = new HashMap<String, Entity>();
		nestedIn  = new HashMap<String, Entity>();
		this.name = name;
	}
	
	protected String idName() {
		return primaryKey == null ? (getName() + "_id") : primaryKey.getName();
	}

	public void add(Data<?> object) {
		data.put(object.getName(), object);
	}

	protected void add(Entity object) {
		entities.put(object.getName(), object);
	}	
	
	public Data<?> getData(Object key) {
		return data.get(key);
	}
	
	public Entity getEntity(Object key) {
		return entities.get(key);
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<String, Entity> getNested() {
		return nestedIn;
	}
	
	public Entity getNested(String _name){
		return nestedIn.get(_name);
	}
	

	public Entity putNested(Entity _entity) {
		this.nestedIn.put(_entity.getName(), _entity);
		return this;
	}
	
	public DataSet makeDataSet(){
		DataSet values = new DataSet(this);
		
		Set<Entry<String, Data<?>>> dataSet   = data    .entrySet();
		Set<Entry<String, Entity>>  entitySet = entities.entrySet();
		
		for (Entry<String, Data<?>> entry : dataSet) {
			String key = entry.getKey();
			values.put(key, data.get(key).value);
		}
		return values;
	}

	protected SQLITETable createTable() {
		SQLITETable newTable = new SQLITETable(name);
		
		for (Entry<String, Data<?>> entry : data.entrySet()) {
			Data<?> date = entry.getValue();
			
			newTable.add(date.createColumn());
		}
		
		return newTable;
	}
	
	public ArrayList<DataSet> selectAll(XMLSQLInterface sqlInterface, Data[] datas){
		return selectAll(sqlInterface, datas, null, this);
	}
	
	public ArrayList<DataSet> selectAllByWhere(XMLSQLInterface sqlInterface, String where){
		return selectAll(sqlInterface, null, where, this);
	}
	
	public static ArrayList<DataSet> selectAll(XMLSQLInterface sqlInterface, Data[] datas, String _selection, Entity _entity) {
		String[] _columns       = null;
		String[] _selectionArgs = null;
		String   _groupBy       = null;
		String   _having        = null;
		String   _orderBy       = null;
		
		ArrayList<DataSet> _result = new ArrayList<DataSet>();
		
		SparseArray<DataSet> _selectedData = select(sqlInterface, _columns, _selection, _selectionArgs,
				_groupBy, _having, _orderBy, _entity);
		
		
		for(int _i = 0; _i < _selectedData.size(); _i++){
			int     _key = _selectedData.keyAt(_i);
			DataSet _set = _selectedData.get  (_key);
			if(_set != null){
			
				_result.add(_set);
			}
		}
		
		return _result;
	}

	private SparseArray<DataSet> select(XMLSQLInterface sqlInterface,
			String[] _columns, String _selection, String[] _selectionArgs,
			String _groupBy, String _having, String _orderBy) {
		return select(sqlInterface, _columns, _selection, _selectionArgs, _groupBy, _having, _orderBy, this);
	}
	
	private static SparseArray<DataSet> select(XMLSQLInterface sqlInterface,
			String[] _columns, String _selection, String[] _selectionArgs,
			String _groupBy, String _having, String _orderBy, Entity _entity) {
		Cursor          _cursor;
		ContentQueryMap _map;
		String          _entityName = _entity.idName();
		
		//HashMap<Integer, DataSet> _rootSet =  new HashMap<Integer, DataSet>(); 
		SparseArray<DataSet> _rootSet = new SparseArray<DataSet>();
		
		
		try {
			_cursor = sqlInterface.query(
					_entity.getName(),
					_columns, 
					_selection, 
					_selectionArgs, 
					_groupBy, 
					_having, 
					_orderBy);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		
		_map     = new ContentQueryMap (_cursor, _entityName, false, null);
		sqlInterface.close();
		
		for (Entry<String, ContentValues> _entry : _map.getRows().entrySet()) {
			String        _key   = _entry .getKey   ();
			ContentValues _value = _entry .getValue ();
			Integer       _id    = DataSet.toInteger(_key);
			DataSet       _set   = new DataSet(_value, _entity);
			
			
			_set    .put(_entityName, _id);
			_rootSet.put(_id, _set);
			
		}
		
		return _rootSet;
	}

	public DataSet parseContent(Node _rootNode) {
		return parseContent(_rootNode, null, null);
	}

	public DataSet parseContent(Node _rootNode, DataSet _rootSet, Entity _rootEntity) {
		DataSet _rootNodeSet = makeDataSet  ();
		String  _value       = _rootNode.getValue();
		
		Set<Entry<String, Node>> _nodes = _rootNode.entrySet();
		
		//Attribute
		for (Entry<String, Object> _entry : _rootNodeSet.valueSet()) {
			String  _key            = _entry.getKey();
			boolean _isInnerContent = this.getData(_key).isInnerContent();
			String  _attribute      = _rootNode.getAttribute(_key);
			
			_rootNodeSet.put(_key, _isInnerContent ? _value : _attribute);
		}
		
		//Verschachtelte Entitäten
		for (Entry<String, Node> _entry : _nodes) {
			Node    _actualNode = _entry.getValue     ();
			String  _entityName = _actualNode.getName ();
			Entity  _entity     = getEntity           (_entityName);
			boolean _nested     = nestedIn.containsKey(_entity);
			String  _key        = "entity:" + _entityName;
			DataSet _nodeSet    = null;
			
			ArrayList<DataSet> _list = _rootNodeSet.getAsArrayList(_key);
			
			if(_entity != null){
				_nodeSet = _entity.parseContent(_actualNode, _rootNodeSet, this);
				if(_nested)
					_nodeSet.put(_rootEntity.getName(), _rootNodeSet);
			}
			
			if(_nodeSet != null){
				_rootNodeSet.put(_key, _nodeSet);
			}
		}
		
		return _rootNodeSet;
	}
	
	@SuppressWarnings("unchecked")
	public long insert(DataSet _content, XMLSQLInterface sqlInterface) {
		
		String        _table          = getName();
		String        _nullColumnHack = null;
		ContentValues _values         = new ContentValues();
		Entity        _entity         = _content.getEntity();
		String        _idName         = _entity.idName();
		long         _id;
		
		
		writeAttributes(_content, sqlInterface, _values);
		_id = sqlInterface.insert(_table, _nullColumnHack, _values);
		writeNested(_content, sqlInterface, _id);
		
		return _id;
	}
	
	@SuppressWarnings("unchecked")
	public long update(DataSet _updates, XMLSQLInterface sqlInterface) {
		if(_updates.get(idName()) == null || _updates.get(idName()).equals("-1")){
			return insert(_updates, sqlInterface);
		}
		
		
		String        _table       = getName();
		ContentValues _values      = new ContentValues();
		Integer       _id          = _updates.getAsInteger(idName());
		String        _whereClause = idName() + "=" + _id;
		String[]      _whereArgs   = null;
		Entity        _entity      = _updates.getEntity();
		
		writeAttributes(_updates, sqlInterface, _values);
		long update = sqlInterface.update(_table, _values, _whereClause, _whereArgs);
		writeNested(_updates, sqlInterface,update);
		
		return update;
		
	}
	
	/**
	 * @param _content
	 * Die Inhalte die in die Datenbank geschrieben werden sollen.
	 * @param sqlInterface
	 * @param _values
	 * Inhalte die Zurückgeliefert werden (vor allem die primary keys der angeschlossenen Klassen)
	 */
	
	//TODO: statt _values einen Standard Return einbauen
	
	private void writeAttributes(DataSet _content, XMLSQLInterface sqlInterface,
			ContentValues _values) {
		Collection<Data<?>> _dataValues   = data    .values();
		Collection<Entity>  _entityValues = entities.values();
		Collection<Entity>  _nestedValues = nestedIn.values();

		for (Entry<String, Data<?>> _entry : data.entrySet()) {   //Attributes
			Data<?> _attribute = _entry.getValue();
			_attribute.put(_values, _content);
		}

		for (Entity _entity : _entityValues) {  //Entities
			String  _entityKey = "entity:" + _entity.getName();
			DataSet _entityDataSet;
			
			if (!_content.containsKey(_entityKey))
				continue;

			_entityDataSet = _content.getAsDataSet(_entityKey);

			String _idName = _entity.idName();
			long _id = _entityDataSet.containsKey(_idName) ? -1
					: _entityDataSet.getAsLong(_idName);

			_id = _entity.update(_entityDataSet, sqlInterface);
			
			
			_values.put(_entityDataSet.getEntity().idName(), _id);
		}
		
		for (Entity _entity : _nestedValues) {  //Entities
			String  _entityKey = "entity:" + _entity.getName();
			DataSet _entityDataSet;
			
			if (!_content.containsKey(_entityKey))
				continue;

			_entityDataSet = _content.getAsDataSet(_entityKey);

			String _idName = _entity.idName();
			long _id = _entityDataSet.containsKey(_idName) ? -1
					: _entityDataSet.getAsLong(_idName);

			_id = _entity.update(_entityDataSet, sqlInterface);
			
			
			_values.put(_entityDataSet.getEntity().idName(), _id);
		}
		
		
	}
	
	private void writeNested(DataSet _content, XMLSQLInterface sqlInterface,
			long _id) {
		Collection<Entity>  _nestedValues = nestedIn.values();
		
		for (Entity _nestedEentity : _nestedValues) {  //Entities
			if (!_content.containsKey(_nestedEentity.getName()))
				continue;
			
			String  _entityKey = "entity:" + _nestedEentity.getName();
			DataSet _entityDataSet;
			String  _idName;
			
			HashMap<Entity, Long> _ids = new HashMap<Entity, Long>();
			
			_entityDataSet = _content.getAsDataSet(_entityKey);

			_idName = _nestedEentity.idName();
			long _theirId  = _entityDataSet.containsKey(_idName) ? -1
					: _entityDataSet.getAsLong(_idName);
			
			for (Entity _entity : _nestedEentity.entities.values()) {
				if(_entity != this)
				_ids.put(_entity, _entity.update(_entityDataSet, sqlInterface));
			}
			
			_entityDataSet.put(idName(), _id);
			
			for (Entry<Entity, Long> _entry : _ids.entrySet()) {
				Entity _actualEntity = _entry.getKey();
				_entityDataSet.put(_actualEntity.idName(), _entry.getValue());
			}
			
			_theirId = _nestedEentity.update(_entityDataSet, sqlInterface);
			
		}
		
		
	}

	public DataSet get(int id, EntityManager manager, XMLSQLInterface xmlsqlInterface) {
		DataSet _set      = makeDataSet();
		String _selection = idName() + "=\"" + id + "\"";

		DataSet _dataSetFromDataBase = select(xmlsqlInterface, null,
				_selection, null, null, null, null).get(id);

		return _dataSetFromDataBase == null ? _set : _dataSetFromDataBase;
	}

	public EntityView makeView(XMLSQLInterface sqlInterface, EntityManager manager, int id) {
		EntityView view = new EntityView(this, sqlInterface, manager, id);
		return view;
	}
	
	public EntityView makeView(EntityManager manager, int id) {
		XMLSQLInterface xmlsqlInterface = manager.getXmlsqlInterface();
		EntityView view = new EntityView(this, xmlsqlInterface, manager, id);
		return null;
	}

	@Override
	public String toString() {
		return TAG + ": " + getName();
	}
}
