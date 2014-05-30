package com.roman.ppaper.helpers.xmlsql;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;

import com.roman.ppaper.helpers.Node;

public class EntityManager{
	private static final String TAG = "Entity";

	Hashtable<String, Entity> entityMap;
	
	Entity  root;
	Node    xi; 
	private String  rootName; 
	Context context;
	XMLSQLInterface xmlsqlInterface;
	
	//TODO: Multiple instances should be able to use same database Connections
	
	
	public EntityManager(String reference, String name, Context context, String[] content){
		Node _node = openReference(reference, name, context);
		init(_node, name, context, content);
	}
	
	public EntityManager(Node xi, String rootName, Context context, String[] content) {
		init(xi, rootName, context, content);
	}
	
	private void init(Node xi, String rootName, Context context, String[] content){
		this.xi = xi;
		this.setRootName(rootName);
		this.context = context;
		entityMap = new Hashtable<String, Entity>();
		
		
		root = parseEntity(xi, rootName, context);
		xmlsqlInterface = new XMLSQLInterface(context, this);
		
		if (XMLSQLInterface.checkDataBase(rootName)) {
			return;
		}
		
		for (String fileName : content) {
			putContent(fileName);
		}
	}

	public Entity parseEntity(Node xi, String rootName, Context context){
		Entity _entity = get(rootName);
		
		if(_entity == null){
			Log.d(TAG, "made new entry: " + rootName);
			_entity = new Entity(rootName);
			put(_entity);
		}
		
		_entity.add(new Data<Integer>(_entity.idName(), null, false, true));
		
		for (Entry<String, Node> _entry : xi.entrySet()) {
			Node _node = _entry.getValue();
			
			
			String _nodeType = _node.getName();
			if(_nodeType == "entity"){       //FOR ENTITIES
				parseEntity(context, _entity, _node);
				
			}else if(_nodeType == "data"){       //FOR DATA
				parseData(_entity, _node);
				
			}else if(_nodeType == "use"){        //FOR USES(REFERENCES)
				parseUses(_entity, _node);
			
			}else if(_nodeType == "value"){     //For Value
				parseValues(_entity, _node, _nodeType);
			}
		}
		return _entity;
	}

	private void parseData(Entity _entity, Node _node) {
		Data<?> _newData = Data.parseData(_node);
		
		if(_newData != null)
			_entity.add(_newData);
	}

	private void parseValues(Entity _entity, Node _node, String _nodeType) {
		String _name  = _node.getAttribute("name");
		String _value = _node.getValue();
		Data<String> _newData = new Data<String>(_nodeType, _value, String.class, true, false);
		
		_entity.add(_newData);
	}

	private void parseUses(Entity _entity, Node _node) {
		String _ref = _node.getAttribute("ref");
		Entity _refEntity = get(_ref);
		
		if(_refEntity != null)
			_entity.add(_refEntity);
		else{
			put(_refEntity);
			_entity.add(_refEntity);
		}
		
		Data<Integer> _newData = new Data<Integer>(_refEntity.idName(), null, Integer.class);
		_entity.add(_newData);
	}

	private void parseEntity(Context context, Entity _entity, Node _node) {
		String _reference = _node.getAttribute("reference");
		String _name      = _node.getAttribute("name"     );
		String _nested    = _node.getAttribute("nested"   );
		String _type      = _node.getAttribute("type"     );
		
		if(_reference != null){
			_node = openReference(_reference, _name, context);
			Log.d(TAG, _reference + " " + _name);
		}
		
		//ID as primary key
		Entity        _newEntity = parseEntity(_node, _name, context);
		Data<Integer> _newData   = new Data<Integer>(_newEntity.idName(), null, Integer.class);
		
		_entity.add(_newData);
		
		if(_nested != null){
			Integer _id = _entity.getData(_entity.idName()).toInteger();
			_entity.putNested(_entity);
			_newData = new Data<Integer>(_entity.idName(), _id, Integer.class);
			_newEntity.add(_newData);
		}
		
		_entity.add(_newEntity);
	}
	
	public Entity get(Object key) {
		return entityMap.get(key);
	}

	public Entity put(Entity value) {
		return entityMap.put(value.getName(), value);
	}
	
	public String[] createTablesSQL(){
		ArrayList<String> _sql = new ArrayList<String>();
		String[] _sqlArray = new String[1];
		for (Entry<String, Entity> entry : entityMap.entrySet()) {
			Log.d(TAG, "got Entity: " + entry.getKey());
			String sqlString = entry.getValue().createTable().SQLString();
			_sql.add(sqlString);
		}
		return (String[])_sql.toArray(_sqlArray);
	}
	
	public ArrayList<DataSet> selectAll(String entityName, Data<?>[] data) {
		Entity _entity = get(entityName);
		return _entity == null ? null : _entity.selectAll(xmlsqlInterface, data);
	}
	
	public ArrayList<DataSet> selectAll(String entityName, int _id, EntityView _sourceEntityView) {
		Entity _entity       = get(entityName);
		Entity _sourceEntity = _sourceEntityView.entity;
		String where         = _sourceEntity.idName() + "=" + _id;
		
		return _entity == null ? null : _entity.selectAllByWhere(xmlsqlInterface, where);
	}
	
	public void putContent(String _reference){
		InputStream _in      = XMLInterface.openAsset(_reference, context);
		Node        _newNode = XMLInterface.parse(_in);
		
		if(_newNode == null || _newNode.size() < 1){
			Log.d(TAG, "Cannot parse " + _reference);
			return;
		}

		
		ArrayList<DataSet> _parseContent = parseContentList(_newNode.get("content"));
		Log.d(TAG, "Load " + _parseContent.size() + " from " + _reference);
		
		putContent(_parseContent);
	}

	private void putContent(ArrayList<DataSet> _contentList) {
		for (DataSet _content : _contentList) {
			putContent(_content);
		}
	}

	private void putContent(DataSet _content) {
		Entity entity = _content.getEntity();
		entity.insert(_content, getXmlsqlInterface());
	}

	protected ArrayList<DataSet> parseContentList(Node root) {
		ArrayList<DataSet> list = new ArrayList<DataSet>();
		
		for (Entry<String, Node> _rootEntry : root.entrySet()) {
			DataSet _nodeSet = parseContent(_rootEntry);
			
			if(_nodeSet != null)
				list.add(_nodeSet);
		}
		return list;
	}

	private DataSet parseContent(Entry<String, Node> _rootEntry) {
		Node _node = _rootEntry.getValue();
		
		String  _entityName = _node.getName();
		Entity  _entity     = get(_entityName);
		DataSet _nodeSet    = null;
		
		if(_entity != null){
			_nodeSet = _entity.parseContent(_node);
		}
		return _nodeSet;
	}
	
	
	public static Node openReference(String reference, String name, Context context) {
		InputStream _in = XMLInterface.openAsset(reference, context);
		Node _newNode = XMLInterface.parse(_in);
		Log.d(TAG, "open: " + reference + " and entity: entity:" + name);
		
		return _newNode.get("entity:" + name);
	}

	public String getRootName() {
		return rootName;
	}

	protected void setRootName(String rootName) {
		this.rootName = rootName;
	}

	/**
	 * @return the xmlsqlInterface
	 */
	public XMLSQLInterface getXmlsqlInterface() {
		return xmlsqlInterface;
	}

	public EntityView getView(String key, int id) {
		return get(key).makeView(xmlsqlInterface, this, id);
	}

	public int write(DataSet _updates) {
		return (int) _updates.getEntity().update(_updates, xmlsqlInterface);
	}

	public String getId(String _entitiyName) {
		// TODO Auto-generated method stub
		return get(_entitiyName).idName();
	}
}
