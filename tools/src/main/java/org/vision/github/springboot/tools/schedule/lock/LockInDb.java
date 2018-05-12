package org.vision.github.springboot.tools.schedule.lock;

/** @author ganminghui */
public class LockInDb implements Lockable {

    @Override public Long tryLock(String lockKey) { return null; }

    @Override public void releaseLock(String lockKey) { }
}