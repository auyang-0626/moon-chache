package com.xishi.moon;

import com.xishi.moon.lock.SegmentLock;
import com.xishi.moon.lock.SpinSegmentLock;
import com.xishi.moon.node.Node;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HashCache implements Map<String,Object> {

    private static int DEFAULT_INITIAL_CAPACITY = 1024;
    private static final int MAXIMUM_CAPACITY = 1 << 30;


    private static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Returns a power of two size for the given target capacity.
     */
    private static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }


    private transient Node[] table;
    private transient SegmentLock segmentLock;
    private int threshold;

    public HashCache(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        this.threshold = tableSizeFor(initialCapacity);
        this.table = new Node[this.threshold];
        this.segmentLock = new SpinSegmentLock(this.threshold);
        log.debug("cache容量:{}", this.threshold);
    }

    public HashCache() {
        this(DEFAULT_INITIAL_CAPACITY);
    }


    public Object get(Object key) {
        if (key == null) return null;

        for (Node e = tabAt(table, getPos(hash(key))); e != null; e = e.getNext()) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return null;
    }

    public Object put(String key, Object value) {
        return putVal(hash(key), key, value, false, true);
    }

    private final Object putVal(int hash, String key, Object value, boolean onlyIfAbsent,
                        boolean evict) {
        if (key == null) {
            throw new RuntimeException("key 不允许为空~");
        }
        int i = getPos(hash);

        return segmentLock.write(i, () -> {
            //while (table)
            Node p, e;
            if ((e = tabAt(table, i)) == null) {
                table[i] = new Node(hash, key, value, null);
                return null;
            }

            for (p = e; (e = e.getNext()) != null; p = e) {
                if (e.getKey().equals(key)) {
                    return e.setValue(value);
                }
            }
            p.setNext(new Node(hash, key, value, null));
            return null;
        });
    }

    private int getPos(int hash) {
        int n = table.length;
        return (n - 1) & hash;
    }


    @SuppressWarnings("unchecked")
    static final Node tabAt(Node[] tab, int i) {
        return (Node) U.getObjectVolatile(tab, ((long) i << ASHIFT) + ABASE);
    }

    private static final sun.misc.Unsafe U;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            U = getUnsafeInstance();

            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static Unsafe getUnsafeInstance() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }


    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Object> values() {
        return null;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return null;
    }
}
