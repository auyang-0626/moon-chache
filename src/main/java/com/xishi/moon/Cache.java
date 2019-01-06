package com.xishi.moon;

public interface Cache {

    Object get(String key);

    String getString(String key);

    Integer getInt(String key);

    Long getLong(String key);

    Double getDouble(String key);

    Object put(String key, Object value);

    Object remove(String key);
}
