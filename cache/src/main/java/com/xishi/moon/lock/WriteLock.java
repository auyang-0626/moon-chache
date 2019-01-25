package com.xishi.moon.lock;

public interface WriteLock {

    Object write(WriteScript script);

}
