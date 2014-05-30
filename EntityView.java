package com.roman.ppaper.helpers.xmlsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.roman.ppaper.helpers.Node;

public class EntityView {
	static final String TAG = "EtityView";
	Entity          entity;
	XMLSQLInterface sqlInterface;
	EntityManager   manager;
	int             id;
	Context         context;
	
	DataSet    set, updates;
	HashMap<String, EntityView> views;
	
	EntityView      parent;

	private void init(boolean loadData) {
		views   = new HashMap<String, EntityView>();
		//updates = makeEmptyDataSet();
		
		if(loadData){
			loadData();
			Collection<Entity> _entitiesOfViews = getEntity().entities.values();
			
			for (Entity _entity : _entitiesOfViews) {
				if(_entity == null) continue;
				
				String     _name   = _entity.getName();
				String     _idName = _entity.idName ();
				Integer    _id     = getAsInteger   (_idName);
				if(_id == null)
					continue;
				EntityView _view   = new EntityView (_name, _id, context);
				
				views.put(_idName, _view);
			}
		}
		makeNewUpdates();
	}
	
	public EntityView(String _referenceFile, String[] _contentFiles,
			String _entitiyName, int id, Context _context) {
		super();
		this.context = _context;
		this.manager = initManager(_context, _referenceFile, _contentFiles);
		this.entity  = getManager().get(_entitiyName);
		this.id      = id;
		init(true);
	}
	
	public EntityView(String _referenceFile, String[] _contentFiles,
			String _entitiyName, DataSet set, Context _context) {
		super();
		this.context = _context;
		this.manager = initManager(_context, _referenceFile, _contentFiles);
		this.entity  = set.getEntity();
		this.id      = getId();
		init(true);
		
	}

	private Integer getId() {
		return this.getAsInteger(idName());
	}
	
	private EntityView(String _entitiyName, int id, Context _context) {
		super();
		this.context = _context;
		this.manager = getManager();
		this.entity  = getManager().get(_entitiyName);
		this.id      = id;
		init(true);
		
	}
	
	private EntityView(DataSet set, Context _context) {
		super();
		this.context = _context;
		this.manager = getManager();
		this.entity  = set.getEntity();
		this.id      = set.getAsInteger(idName());
		this.set     = set;
		
		init(false);
	}



	public EntityView(Entity entity2, XMLSQLInterface sqlInterface2,
			EntityManager manager2, int id2) {
		super();
		this.context = manager2.context;
		this.manager = manager2;
		this.entity  = entity2;
		this.id      = id2;
		
		init(true);
	}

	////DATABASE
	public void update() {
		loadData();
		//updates.update();
		//TODO: Write them!
	}
	
	public int write(){
		int rowsAffected = getManager().write(updates);
		
		loadData();
		makeNewUpdates();
		id = id == -1 ? rowsAffected : id;
		set(idName(), id);
			
		return id;
	}

	/**
	 * 
	 */
	private void makeNewUpdates() {
		try {
			updates = (DataSet)set.clone();
		} catch (CloneNotSupportedException e) {
			updates = makeEmptyDataSet();
			Log.d(TAG, "can't Clone " + getEntityName() + "!");
		}
	}
	
	private void loadData() {
		//TODO: Asynchrones Laden
		sqlInterface = getManager().xmlsqlInterface;
		if(id == -1 )
			set = makeEmptyDataSet();
		else
			set = entity.get(id, getManager(), sqlInterface);
		
		Set<Entry<String, Entity>> _entrySet = entity.entities.entrySet();
		
		for (Entry<String, Entity> _entry : _entrySet) {
			Entity _entity = _entry.getValue();
			if(id == -1){
				for (Entry<String, Entity> entry : _entrySet) {
					Entity _actualEntity = entry.getValue();
					String _entityNameIdName   = _actualEntity.idName();
					set.put(_entityNameIdName, -1);
				}
			} else {
				String  _idName    = _entity.idName      ();
				Integer _asInteger = set   .getAsInteger(_idName);
				if(_asInteger != null) //This piece ist quite unuseful
					entity.get(_asInteger, manager, sqlInterface);
			}
		}
	}
	///\DATABASE
	/**
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#idName()
	 */
	public String idName() {
		return entity.idName();
	}
	/**
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#getName()
	 */
	public String getEntityName() {
		return entity.getName();
	}
	/**
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#getNested()
	 */
	public HashMap<String, EntityView> getNested() {
		return views;	
	}
	/**
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#makeDataSet()
	 */
	public DataSet makeEmptyDataSet() {
		return entity.makeDataSet();
	}
	/**
	 * @param sqlInterface
	 * @param datas
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#selectAll(com.roman.ppaper.helpers.xmlsql.XMLSQLInterface, com.roman.ppaper.helpers.xmlsql.Data[])
	 */
	public ArrayList<DataSet> selectAll(Data[] datas) {
		return entity.selectAll(sqlInterface, datas);
	}
	
	public ArrayList<DataSet> selectAllById(String _nestedEntity) {
		Integer _id = getAsInteger(manager.getId(_nestedEntity));
		return manager.selectAll(_nestedEntity, _id.intValue(), this);
	}
	
	public ArrayList<DataSet> selectAll() {
		return entity.selectAllByWhere(sqlInterface, null);
	}
	/**
	 * @param _rootNode
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#parseContent(com.roman.ppaper.helpers.Node)
	 */
	public DataSet parseContent(Node _rootNode) {
		return entity.parseContent(_rootNode);
	}
	/**
	 * @param _rootNode
	 * @param _rootSet
	 * @param _rootEntity
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#parseContent(com.roman.ppaper.helpers.Node, com.roman.ppaper.helpers.xmlsql.DataSet, com.roman.ppaper.helpers.xmlsql.Entity)
	 */
	public DataSet parseContent(Node _rootNode, DataSet _rootSet,
			Entity _rootEntity) {
		return entity.parseContent(_rootNode, _rootSet, _rootEntity);
	}
	/**
	 * @param _content
	 * @param sqlInterface
	 * @see com.roman.ppaper.helpers.xmlsql.Entity#insert(com.roman.ppaper.helpers.xmlsql.DataSet, com.roman.ppaper.helpers.xmlsql.XMLSQLInterface)
	 */
	public void putContent(DataSet _content) {
		entity.insert(_content, sqlInterface);
	}
	
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#get(java.lang.String)
	 */
	public Object get(String key) {
		return set.get(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsString(java.lang.String)
	 */
	public String getAsString(String key) {
		return set.getAsString(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsLong(java.lang.String)
	 */
	public Long getAsLong(String key) {
		return set.getAsLong(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsInteger(java.lang.String)
	 */
	public Integer getAsInteger(String key) {
		return set.getAsInteger(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsShort(java.lang.String)
	 */
	public Short getAsShort(String key) {
		return set.getAsShort(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsByte(java.lang.String)
	 */
	public Byte getAsByte(String key) {
		return set.getAsByte(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsDouble(java.lang.String)
	 */
	public Double getAsDouble(String key) {
		return set.getAsDouble(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsFloat(java.lang.String)
	 */
	public Float getAsFloat(String key) {
		return set.getAsFloat(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsBoolean(java.lang.String)
	 */
	public Boolean getAsBoolean(String key) {
		return set.getAsBoolean(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsByteArray(java.lang.String)
	 */
	public byte[] getAsByteArray(String key) {
		return set.getAsByteArray(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsDataSet(java.lang.String)
	 */
	public DataSet getAsDataSet(String key) {
		return set.getAsDataSet(key);
	}
	/**
	 * @param key
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#getAsArrayList(java.lang.String)
	 */
	public ArrayList<DataSet> getAsArrayList(String key) {
		return set.getAsArrayList(key);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#put(java.lang.String, java.lang.String)
	 */
	public void set(String key, String value) {
		updates.put(key, value);
	}
	/**
	 * @param other
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#putAll(com.roman.ppaper.helpers.xmlsql.DataSet)
	 */
	public void setAll(DataSet other) {
		updates.putAll(other);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Byte)
	 */
	public void set(String key, Byte value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Short)
	 */
	public void set(String key, Short value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Integer)
	 */
	public void set(String key, Integer value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Long)
	 */
	public void set(String key, Long value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Float)
	 */
	public void set(String key, Float value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Double)
	 */
	public void set(String key, Double value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Boolean)
	 */
	public void set(String key, Boolean value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, byte[])
	 */
	public void set(String key, byte[] value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, java.lang.Object)
	 */
	public void set(String key, Object value) {
		updates.put(key, value);
	}
	/**
	 * @param key
	 * @param value
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#set(java.lang.String, com.roman.ppaper.helpers.xmlsql.DataSet)
	 */
	public void set(String key, DataSet value) {
		updates.put(key, value);
	}
	
	public void set(DataSet set){
		
	}
	
	
	EntityManager getManager(){
		return manager != null ? manager : EntityView.getManager(context);
	}
	
	
	//TODO: Oh dear. we're only able to open up one EntityManager at a time.
	
	protected static EntityManager ENTITY_MANAGER;
	static String   REFERENCE;
	static String[] CONTENT;
	
	
	public static void reinitDatabase(Context context){
		context.deleteDatabase(getManager(context).getRootName());
		EntityView.ENTITY_MANAGER = null;
		getManager(context);
	}
	
	public static EntityManager getManager(Context _context){
		return initManager(_context, REFERENCE, CONTENT);
	}
	
	public static EntityManager initManager(Context _context, String _reference,
			String[] _content) {
		
		REFERENCE = _reference;
		CONTENT   = _content;
		
		if (EntityView.ENTITY_MANAGER == null) {
			EntityView.ENTITY_MANAGER = new EntityManager(_reference,
					"character", _context, _content);
		}
		return EntityView.ENTITY_MANAGER;
	}



	public static EntityManager getManager(String _entityDataXml,
			String[] _contentDataXml, Context _context) {
		
		return initManager(_context, _entityDataXml, _contentDataXml);
	}



	public EntityView createEntityView(String _entitiyName) {
		String _entityIdName = manager.getId(_entitiyName);
		Integer _id = set.getAsInteger(_entityIdName);
		return new EntityView(_entitiyName, _id, context);
	}
	
	public EntityView createNew(String _entitiyName) {
		EntityView entityView = new EntityView(_entitiyName, -1, context);
		
		return entityView;
	}
	
	public EntityView createEntityView(DataSet set) {
		Entity entity2 = set.getEntity();
		String id2 = manager.getId(entity2.getName());
		Integer _id = set.getAsInteger(id2);
		return new EntityView(set, context);
	}
	
	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the entity
	 */
	protected Entity getEntity() {
		return entity;
	}
	
	public void set(EntityView view){
		views.put(view.getEntityName(), view);
	}
	

	public static void linkEntityInstances(Context _context, EntityView viewOne,
			EntityView viewTwo, String linkViewName) {
		EntityView linkView = getManager(_context).getView(linkViewName, -1);
		linkView.set(viewOne.idName(), viewOne.getId());
		linkView.set(viewTwo.idName(), viewTwo.getId());
	}

	/**
	 * @return
	 * @see com.roman.ppaper.helpers.xmlsql.DataSet#valueSet()
	 */
	private Set<Entry<String, Object>> valueSet() {
		return set.valueSet();
	}
	
	public ArrayList<Data<?>> getList(){
		ArrayList<Data<?>> list = new ArrayList<Data<?>>();
		for (Entry<String, Object> _entry : valueSet()) {
			Object   _value = _entry.getValue();
			String   _name  = _entry.getKey();
			Class<?> _type  = _value.getClass();
			Data <?> _data  = Data.makeData(_name, _value, _type);
			
			list.add(_data);
		}
		return list;
	}

	/**
	 * @return the parent
	 */
	protected EntityView getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	protected void setParent(EntityView parent) {
		this.parent = parent;
	}
	
	public ArrayList<DataSet> getNested(String name){
		return getEntity().getNested().get(name).selectAllByWhere(sqlInterface, idName() + "=" + id);
	}
	
	@Override
	public String toString() {
		return TAG + ": " + entity.getName();
	}
}
