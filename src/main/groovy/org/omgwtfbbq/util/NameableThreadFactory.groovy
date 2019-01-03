package org.omgwtfbbq.util

import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/*
    I want a better name than "pool-x-thread-y".
*/

class NameableThreadFactory implements ThreadFactory {
    private static final ThreadFactory REAL_FACTORY = Executors.defaultThreadFactory()

    private final AtomicInteger THREAD_COUNT = new AtomicInteger(0)
    private final String baseName

    NameableThreadFactory(String base) {
        baseName = "$base-"
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = REAL_FACTORY.newThread(r)
        t.setName(baseName + THREAD_COUNT.getAndIncrement())
        return t
    }
}
