package com.xishi.moon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemCache implements Cache {

    private Map<String,Object> data ;

    public MemCache() {
        data = new ConcurrentHashMap<String, Object>();
    }

    public Object get(String key) {
        return data.get(key);
    }

    public String getString(String key) {
        Object v = get(key);
        if (v instanceof String)
            return (String) v;
        else
            return v.toString();
    }

    public Integer getInt(String key) {
        Object v = get(key);
        if (v instanceof Integer)
            return (Integer) v;
        else
            return Integer.parseInt(v.toString());
    }

    public Long getLong(String key) {
        Object v = get(key);
        if (v instanceof Long)
            return (Long) v;
        else
            return Long.parseLong(v.toString());
    }

    public Double getDouble(String key) {
        Object v = get(key);
        if (v instanceof Double)
            return (Double) v;
        else
            return Double.parseDouble(v.toString());
    }

    public Object put(String key, Object value) {
       return data.put(key,value);
    }

    public Object remove(String key) {
        return data.remove(key);
    }
}
