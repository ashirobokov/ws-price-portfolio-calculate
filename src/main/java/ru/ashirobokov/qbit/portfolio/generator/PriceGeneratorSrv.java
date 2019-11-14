package ru.ashirobokov.qbit.portfolio.generator;

import io.advantageous.qbit.reactive.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;
import ru.ashirobokov.qbit.portfolio.price_receiver.PriceReceiver;
import ru.ashirobokov.qbit.portfolio.price_receiver.PriceReceiverSrv;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PriceGeneratorSrv {

    private final Logger LOG = LoggerFactory.getLogger(PriceGeneratorSrv.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("price-generator-thread");
                return thread;
            });
    private final PriceReceiver proxy = PriceReceiverSrv.getInstanse().getProxy();

    private Callback<Boolean> callbackAddPrices = new Callback<Boolean>() {
        @Override
        public void accept(Boolean aBoolean) {
            LOG.info("[Callback<Boolean>.addPrices] to PriceRsvSrv...{}", aBoolean.booleanValue() ? "added OK" : "failed");
        }
    };

    private Callback<Integer> callbackGetInstrPricesSize = new Callback<Integer>() {
        @Override
        public void accept(Integer integer) {
            LOG.info("[Callback<Integer>.getInstrPricesSize] size of instrumentPrices map = {}", integer);
        }
    };

    public void generatePrices(CountDownLatch latch, List<InstrumentPrice> instrumentPrices) {
        LOG.info("PriceGeneratorSrv.generatePrices...for list size={}", instrumentPrices.size());
        final Runnable generator = new Runnable() {
            public void run() {
                Long timestamp = System.currentTimeMillis();
                instrumentPrices.forEach(instrument -> {
//  Генерирует случайное BigDecimal в диапазоне 100.00 -- 200.00
                    BigDecimal price = new BigDecimal(100.00 + Math.random() * 100);
                    instrument.setPrice(price.setScale(2, BigDecimal.ROUND_CEILING));
//                        LOG.info("...generatedPrices...instrument id={} price={}", instrument.getInstrumentId(), getPrice(instrument.getPrice()));
                });
                LOG.info("...generatedPrices...{}", instrumentPrices.toString());
                proxy.addPrices(callbackAddPrices, timestamp, instrumentPrices);
            }
        };
        final ScheduledFuture<?> generateHandler =
                scheduler.scheduleAtFixedRate(generator, 10, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                generateHandler.cancel(true);
                proxy.getInstrPricesSize(callbackGetInstrPricesSize);
                latch.countDown();
            }
        }, 60 * 1, SECONDS); // Задержка должна быть равна времени работы сервисов
    }

    private String getPrice(BigDecimal price) {
        return price.toString();
    }

    public void stopScheduler() {
        LOG.info("PriceGeneratorSrv.stopScheduler...scheduler stopped");
        scheduler.shutdown();
    }

    public void stopTasks() {
        LOG.info("PriceGeneratorSrv.stopTasks...stopping tasks");
        if (!scheduler.isTerminated()) {
            List<Runnable> tasks = scheduler.shutdownNow();
            if (null != tasks) {
                LOG.info("stopTasks.stopTasks : {} tasks were awaiting", tasks.stream().count());
                tasks.forEach(task -> {
                    LOG.info("...awaiting task {}", task.toString());
                });
            }
        } else {
            LOG.info("PriceGeneratorSrv.stopTasks : all tasks stopped");
        }
        LOG.info("/PriceGeneratorSrv.stopTasks");
    }

}
