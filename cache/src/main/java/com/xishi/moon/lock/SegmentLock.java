package com.xishi.moon.lock;

public abstract class SegmentLock implements WriteLock {


    protected int size;

    public SegmentLock(int size) {
        this.size = size;
    }

    /**
     * 这个是全局锁，
     */
    @Override
    public Object write(WriteScript script) {
        return null;
    }

    /**
     * 分段写入
     */
    public Object write(int i, WriteScript script) {
        try {
            lock(i);
            return script.script();
        } finally {
            unlock(i);
        }
    }

    abstract void unlock(int i);

    abstract void lock(int i);
}
