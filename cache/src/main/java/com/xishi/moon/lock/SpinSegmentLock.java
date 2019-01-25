package com.xishi.moon.lock;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class SpinSegmentLock extends SegmentLock {

    private AtomicIntegerArray locks;

    public SpinSegmentLock(int size) {
        super(size);
        this.locks = new AtomicIntegerArray(size);
    }

    @Override
    void unlock(int i) {
        locks.set(i,0);
    }

    @Override
    void lock(int i) {
        while (true){
            if (locks.compareAndSet(i,0,1)){
                break;
            }
        }
    }
}
