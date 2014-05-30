package com.roman.ppaper.helpers.xmlsql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.util.Log;

public class DataSet  implements Cloneable{


	private static final String TAG = "DataSet";
	Entity entity;
	/** Holds the actual values */
    private HashMap<String, Object> mValues;
	
	/**
	 * @return the entity
	 */
	public Entity getEntity() {
		return entity;
	}

    /**
     * Creates an empty set of values using the default initial size
     */
    public DataSet() {
        // Choosing a default size of 8 based on analysis of typical
        // consumption by applications.
        mValues = new HashMap<String, Object>(8);
    }
    
    public DataSet(Entity entity) {
        this();
        this.entity = entity;
    }

    /**
     * Creates an empty set of values using the given initial size
     *
     * @param size the initial size of the set of values
     */
    public DataSet(int size) {
        mValues = new HashMap<String, Object>(size, 1.0f);
    }

    /**
     * Creates a set of values copied from the given set
     *
     * @param from the values to copy
     */
    public DataSet(DataSet from) {
        mValues = new HashMap<String, Object>(from.mValues);
    }

    /**
     * Creates a set of values copied from the given HashMap. This is used
     * by the Parcel unmarshalling code.
     *
     * @param values the values to start with
     * {@hide}
     */
    private DataSet(HashMap<String, Object> values) {
        mValues = values;
    }

    public DataSet(ContentValues _value, Entity _entity) {
    	this(_entity);
    	
		Set<Entry<String, Object>> set = _value.valueSet();
		
		for (Entry<String, Object> entry : set) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
    public boolean equals(Object object) {
        if (!(object instanceof ContentValues)) {
            return false;
        }
        return mValues.equals(((DataSet) object).mValues);
    }

    @Override
    public int hashCode() {
        return mValues.hashCode();
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, String value) {
        mValues.put(key, value);
    }

    /**
     * Adds all values from the passed in ContentValues.
     *
     * @param other the ContentValues from which to copy
     */
    public void putAll(DataSet other) {
        mValues.putAll(other.mValues);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Byte value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Short value) {
        mValues.put(key, value);
    }
    
    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Integer value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Long value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Float value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Double value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Boolean value) {
        mValues.put(key, value);
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, byte[] value) {
        mValues.put(key, value);
    }
    
    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, Object value) {
        mValues.put(key, value);
    }
    
    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public void put(String key, DataSet value) {
        mValues.put(key, value);
    }

    /**
     * Adds a null value to the set.
     *
     * @param key the name of the value to make null
     */
    public void putNull(String key) {
        mValues.put(key, null);
    }

    /**
     * Returns the number of values.
     *
     * @return the number of values
     */
    public int size() {
        return mValues.size();
    }

    /**
     * Remove a single value.
     *
     * @param key the name of the value to remove
     */
    public void remove(String key) {
        mValues.remove(key);
    }

    /**
     * Removes all values.
     */
    public void clear() {
        mValues.clear();
    }

    /**
     * Returns true if this object has the named value.
     *
     * @param key the value to check for
     * @return {@code true} if the value is present, {@code false} otherwise
     */
    public boolean containsKey(String key) {
        return mValues.containsKey(key);
    }

    /**
     * Gets a value. Valid value types are {@link String}, {@link Boolean}, and
     * {@link Number} implementations.
     *
     * @param key the value to get
     * @return the data for the value
     */
    public Object get(String key) {
        return mValues.get(key);
    }

    /**
     * Gets a value and converts it to a String.
     *
     * @param key the value to get
     * @return the String for the value
     */
    public String getAsString(String key) {
        Object value = mValues.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Gets a value and converts it to a Long.
     *
     * @param key the value to get
     * @return the Long value, or null if the value is missing or cannot be converted
     */
    public Long getAsLong(String key) {
        Object value = mValues.get(key);
        return toLong(value);
    }

	public static Long toLong(Object value) {
		try {
            return value != null ? ((Number) value).longValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Long.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Long value for " + value);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + value + " to a Long: " + value, e);
                return null;
            }
        }
	}

    /**
     * Gets a value and converts it to an Integer.
     *
     * @param key the value to get
     * @return the Integer value, or null if the value is missing or cannot be converted
     */
    public Integer getAsInteger(String key) {
        Object value = mValues.get(key);
        return toInteger(value);
    }

	public static Integer toInteger(Object value) {
		try {
            return value != null ? ((Number) value).intValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Integer.valueOf(cleanValue(value.toString()));
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Integer value for " + value);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value to a Integer: " + value, e);
                return null;
            }
        }
	}

    /**
     * Gets a value and converts it to a Short.
     *
     * @param key the value to get
     * @return the Short value, or null if the value is missing or cannot be converted
     */
    public Short getAsShort(String key) {
        Object value = mValues.get(key);
        return toShort(value);
    }

	public static Short toShort(Object value) {
		try {
            return value != null ? ((Number) value).shortValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Short.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Short value for " + value);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + value + " to a Short: ", e);
                return null;
            }
        }
	}

    /**
     * Gets a value and converts it to a Byte.
     *
     * @param key the value to get
     * @return the Byte value, or null if the value is missing or cannot be converted
     */
    public Byte getAsByte(String key) {
        Object value = mValues.get(key);
        return toByte(value);
    }

	public static Byte toByte(Object value) {
		try {
            return value != null ? ((Number) value).byteValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Byte.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Byte value for " + value);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + value + " to a Byte: ", e);
                return null;
            }
        }
	}

    /**
     * Gets a value and converts it to a Double.
     *
     * @param key the value to get
     * @return the Double value, or null if the value is missing or cannot be converted
     */
    public Double getAsDouble(String key) {
        Object value = mValues.get(key);
        return toDouble(value);
    }

	public static Double toDouble(Object value) {
		try {
            return value != null ? ((Number) value).doubleValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Double.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Double value for " + value + " at key ");
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value for " + value + " to a Double: ", e);
                return null;
            }
        }
	}

    /**
     * Gets a value and converts it to a Float.
     *
     * @param key the value to get
     * @return the Float value, or null if the value is missing or cannot be converted
     */
    public Float getAsFloat(String key) {
        Object value = mValues.get(key);
        return toFloat(value);
    }

	public static Float toFloat(Object value) {
		try {
            return value != null ? ((Number) value).floatValue() : null;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Float.valueOf(cleanValue(value.toString()));
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "Cannot parse Float value for " + value);
                    return null;
                }
            } else {
                Log.e(TAG, "Cannot cast value to a Float: " + value, e);
                return null;
            }
        }
	}

    /**
     * Gets a value and converts it to a Boolean.
     *
     * @param key the value to get
     * @return the Boolean value, or null if the value is missing or cannot be converted
     */
    public Boolean getAsBoolean(String key) {
        Object value = mValues.get(key);
        return toBoolean(value);
    }

	public static Boolean toBoolean(Object value) {
		try {
            return (Boolean) value;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                return Boolean.valueOf(value.toString());
            } else if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            } else {
                Log.e(TAG, "Cannot cast value for " + value + " to a Boolean: ", e);
                return null;
            }
        }
	}

    /**
     * Gets a value that is a byte array. Note that this method will not convert
     * any other types to byte arrays.
     *
     * @param key the value to get
     * @return the byte[] value, or null is the value is missing or not a byte[]
     */
    public byte[] getAsByteArray(String key) {
        Object value = mValues.get(key);
        return toByteArray(value);
    }

	public static byte[] toByteArray(Object value) {
		if (value instanceof byte[]) {
            return (byte[]) value;
        } else {
            return null;
        }
	}
    
    public DataSet getAsDataSet(String key) {
        Object value = mValues.get(key);
        return toDataSet(value);
    }

	public static DataSet toDataSet(Object value) {
		if (value instanceof DataSet) {
            return (DataSet) value;
        } else {
            return null;
        }
	}

    /**
     * Returns a set of all of the keys and values
     *
     * @return a set of all of the keys and values
     */
    public Set<Map.Entry<String, Object>> valueSet() {
        return mValues.entrySet();
    }

    /**
     * Returns a set of all of the keys
     *
     * @return a set of all of the keys
     */
    public Set<String> keySet() {
        return mValues.keySet();
    }

	public ArrayList<DataSet> getAsArrayList(String key) {
		return (ArrayList<DataSet>) get(key);
	}

	public static String cleanValue(String _value) {
		String _cleanValue = _value == null ? null : _value.replaceAll(
				"[A-Za-z_ \t\r\n\f:\\<>]", "");
		return _cleanValue;
	}
	
	public String toString(){
		String _string = "[" + entity.getName() + ": ";
		Set<Entry<String, Object>> _entrySet = mValues.entrySet();
		for (Entry<String, Object> _entry : _entrySet) {
			_string = _string + _entry.getKey() + ":" + getAsString(_entry.getKey()) + "; ";
		}
		_string = _string + "]";
		return _string;
	}
	
	@Override
	public DataSet clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return (DataSet)super.clone();
	}
	
	public Collection<Object> values() {
		return mValues.values();
	}
}
