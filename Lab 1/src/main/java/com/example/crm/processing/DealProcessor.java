package com.example.crm.processing;

import com.example.crm.model.Deal;

import java.util.List;
import java.util.Set;

public interface DealProcessor {

    /**
     * Обрабатывает сделки: новые (NEW) переводит в работу (IN_PROGRESS).
     *
     * @return имена потоков, которые выполняли обработку
     */
    Set<String> process(List<Deal> deals);
}
