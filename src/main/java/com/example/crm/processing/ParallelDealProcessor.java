package com.example.crm.processing;

import com.example.crm.exception.DealProcessingException;
import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Обрабатывает сделки в нескольких потоках (ExecutorService). Каждый поток
 * работает со своей сделкой, поэтому общих изменяемых данных нет.
 */
@Component
public class ParallelDealProcessor implements DealProcessor {

    private static final Logger log = LoggerFactory.getLogger(ParallelDealProcessor.class);
    private static final long WORK_TIME_MS = 200;

    private final int threads;

    public ParallelDealProcessor(@Value("${crm.worker-threads:3}") int threads) {
        if (threads < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть не меньше 1");
        }
        this.threads = threads;
    }

    @Override
    public Set<String> process(List<Deal> deals) {
        Set<String> threadNames = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(threads, new NamedThreadFactory("deal-worker-"));
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (Deal deal : deals) {
                futures.add(executor.submit(() -> processOne(deal, threadNames)));
            }
            waitForAll(futures);
        } finally {
            // Прерывает оставшиеся задачи и при ошибке, и при отмене запроса.
            executor.shutdownNow();
        }
        return threadNames;
    }

    private void waitForAll(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DealProcessingException("Ожидание обработки сделок прервано.", e);
            } catch (ExecutionException e) {
                throw new DealProcessingException("Ошибка в потоке обработки сделки.", e.getCause());
            }
        }
    }

    private void processOne(Deal deal, Set<String> threadNames) {
        String threadName = Thread.currentThread().getName();
        threadNames.add(threadName);
        log.info("Поток {} начал обработку сделки #{}", threadName, deal.getId());
        try {
            Thread.sleep(WORK_TIME_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DealProcessingException("Поток обработки сделки прерван.", e);
        }
        if (deal.getStatus() == DealStatus.NEW) {
            deal.setStatus(DealStatus.IN_PROGRESS);
        }
        log.info("Поток {} закончил сделку #{}, статус: {}", threadName, deal.getId(), deal.getStatus());
    }
}
