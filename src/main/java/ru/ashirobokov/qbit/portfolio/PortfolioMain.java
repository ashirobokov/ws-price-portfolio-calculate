package ru.ashirobokov.qbit.portfolio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.generator.PriceGeneratorSrv;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PortfolioMain {

    public final static Logger LOG = LoggerFactory.getLogger(PortfolioMain.class);
    public final static Long[] instruments = {1L, 2L, 3L, 4L, 5L,
            11L, 12L, 13L, 14L, 15L,
            122L, 123L, 124L, 125L, 126L};
    public final static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        LOG.info("PortfolioMain...starting");
        List<InstrumentPrice> instrumentPrices = new ArrayList<>();
        Arrays.asList(instruments).forEach(instrument -> {
            instrumentPrices.add(new InstrumentPrice(instrument, null));
        });
        PriceGeneratorSrv priceGenerator = new PriceGeneratorSrv();
        priceGenerator.generatePrices(latch, instrumentPrices);

        try {
            latch.await();
        } catch (Exception e) {
            LOG.error("PortfolioMain...latch awaiting error {}", e.getMessage());
        }
// Останавливаем задачи если они не завершены и также останавливаем сам ScheduledExecutorService
        priceGenerator.stopTasks();
        priceGenerator.stopScheduler();
        LOG.info("/PortfolioMain...FINISHED");
    }
}
