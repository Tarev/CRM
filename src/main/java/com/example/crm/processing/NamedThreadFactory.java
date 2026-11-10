package com.example.crm.processing;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Создает потоки с именами вида prefix1, prefix2, ...
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;
    private final AtomicInteger counter = new AtomicInteger(1);

    public NamedThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, prefix + counter.getAndIncrement());
    }
}
