package com.springcloud.standard.distrubute.lock;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

public interface LockService {

    /**
     * 取得zookeeper 分布式锁
     * @param key
     * @return
     */
   InterProcessMutex getZookeeperLock(String key);
}
