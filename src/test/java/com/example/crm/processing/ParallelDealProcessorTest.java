package com.example.crm.processing;

import com.example.crm.model.Deal;
import com.example.crm.model.DealStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParallelDealProcessorTest {

    private List<Deal> createDeals(int count) {
        List<Deal> deals = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Deal deal = new Deal("Deal " + i, 1L, 1L, BigDecimal.TEN);
            deal.setId((long) i);
            deals.add(deal);
        }
        return deals;
    }

    @Test
    void allNewDealsBecomeInProgress() {
        List<Deal> deals = createDeals(6);

        new ParallelDealProcessor(3).process(deals);

        for (Deal deal : deals) {
            assertEquals(DealStatus.IN_PROGRESS, deal.getStatus());
        }
    }

    @Test
    void dealsWithOtherStatusAreNotChanged() {
        List<Deal> deals = createDeals(1);
        deals.get(0).setStatus(DealStatus.WON);

        new ParallelDealProcessor(2).process(deals);

        assertEquals(DealStatus.WON, deals.get(0).getStatus());
    }

    @Test
    void workIsDoneInSeveralNamedThreads() {
        Set<String> names = new ParallelDealProcessor(3).process(createDeals(3));

        assertEquals(3, names.size());
        for (String name : names) {
            assertTrue(name.startsWith("deal-worker-"));
        }
    }

    @Test
    void emptyListDoesNothing() {
        assertTrue(new ParallelDealProcessor(2).process(new ArrayList<>()).isEmpty());
    }

    @Test
    void zeroThreadsIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ParallelDealProcessor(0));
    }
}
