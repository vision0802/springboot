package org.vision.github.springboot.tools.schedule.lock;

/**
 * @author ganminghui
 * <p>
 *     1. 数据库乐观锁实现
 *     2. redis分布式锁实现
 * </p>
 */

public interface Lockable {
    /**
     * 尝试加锁
     * @param lockKey 对lockKey加锁
     * @return 返回加锁标识
     */
    Long tryLock(String lockKey);

    /**
     *  尝试解锁
     *  @param lockKey 对lockKey解锁
     */
    void releaseLock(String lockKey);
}