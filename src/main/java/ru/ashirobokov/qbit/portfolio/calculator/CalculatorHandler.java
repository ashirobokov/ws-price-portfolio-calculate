package ru.ashirobokov.qbit.portfolio.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;
import ru.ashirobokov.qbit.portfolio.model.Portfolio;
import ru.ashirobokov.qbit.portfolio.model.PortfolioInstrument;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class CalculatorHandler {
    private final Logger LOG = LoggerFactory.getLogger(CalculatorHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("portfolio-calculate-handler-thread");
                return thread;
            });
    private final PortfolioCalculatorImpl portfolioCalculator;
    private TreeSet<Long> calculatedTimestamps = new TreeSet<>();

    public CalculatorHandler(PortfolioCalculatorImpl portfolioCalculator) {
        this.portfolioCalculator = portfolioCalculator;
    }

    public void calculating() {
        LOG.info("CalculatorHandler...portfolio calculating...");
        try {
            final Runnable calculator = new Runnable() {
                public void run() {
/*
                    ConcurrentNavigableMap<Long, List<InstrumentPrice>> processedPrices =
                            calculatedTimestamps.isEmpty() ? portfolioCalculator.getInstrumentPrices() :
                                    portfolioCalculator.getInstrumentPrices().tailMap(calculatedTimestamps.last(), false);
*/
/* Concurrent map is changed for just Sorted Map */
                    SortedMap<Long, List<InstrumentPrice>> processedPrices = new TreeMap<>(
                            calculatedTimestamps.isEmpty() ? portfolioCalculator.getInstrumentPrices() :
                                    portfolioCalculator.getInstrumentPrices().tailMap(calculatedTimestamps.last(), false)
                    );
                    LOG.info("CalculatorHandler...processed Prices = {}", processedPrices);
                    processedPrices.forEach((timestamp, list) -> {
                        LOG.info("CalculatorHandler...timestamp = {}", timestamp);
                        calculatedTimestamps.add(timestamp);
                        /* расчет портфелей ценных бумаг по ценам инструментов*/
                        portfolioCalculator.getPortfolios().forEach(item -> {
                            item.setValue(new BigDecimal(0L));
                            List<PortfolioInstrument> instruments = item.getInstruments();
                            LOG.info("CalculatorHandler...portfolio = {} instruments {}", item.getPortfolioId(), item.getInstruments());
                            instruments.forEach(instrument -> {
                                Optional<InstrumentPrice> instr = list.stream().filter(p -> p.getInstrumentId() == instrument.getInstrumentId()).findFirst();
                                if (instr.isPresent()) {
                                    int index = list.indexOf(instr.get());
                                    BigDecimal price = list.get(index).getPrice();
                                    LOG.info("...........new price = {} for instrument {}", price, instrument.getInstrumentId());
                                    if (null != price) {
/*
                                        BigDecimal calcPrice = null == item.getValue() ?
                                                price.multiply(new BigDecimal(instrument.getNumber())) :
                                                item.getValue().add(price.multiply(new BigDecimal(instrument.getNumber())));
*/
                                        BigDecimal calcValueByInstrument =  price.multiply(new BigDecimal(instrument.getNumber()));
                                        LOG.info("........... calculated....value = {} for instrument {} number {}", calcValueByInstrument, instrument.getInstrumentId(), instrument.getNumber());
                                        instrument.setValue(calcValueByInstrument);
                                        item.setValue(item.getValue().add(calcValueByInstrument));
                                    }

                                } else {
                                    if (null != instrument.getValue()) {
                                        item.setValue(item.getValue().add(instrument.getValue()));
                                    }
                                }
                            });
                            LOG.info("Value for portfolio id [{}] = {}", item.getPortfolioId(), item.getValue().toString());
                        });
                    });
                    LOG.info("CalculatorHandler...calculatedTimestamps set {}", calculatedTimestamps);
                }
            };
            final ScheduledFuture<?> calculatorHandler =
                    scheduler.scheduleAtFixedRate(calculator, 30, 10, SECONDS);
            scheduler.schedule(new Runnable() {
                public void run() {
                    calculatorHandler.cancel(true);
                }
            }, 60 * 1, SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("ERROR {}", e.getMessage());
        }
    }

    public void init() {
        List<PortfolioInstrument> instrSet1 = new ArrayList<>();
        instrSet1.add(new PortfolioInstrument(1L, 125L));
        instrSet1.add(new PortfolioInstrument(2L, 128L));
        instrSet1.add(new PortfolioInstrument(3L, 120L));

        List<PortfolioInstrument> instrSet2 = new ArrayList<>();
        instrSet2.add(new PortfolioInstrument(11L, 225L));
        instrSet2.add(new PortfolioInstrument(12L, 228L));
        instrSet2.add(new PortfolioInstrument(13L, 212L));

        List<PortfolioInstrument> instrSet3 = new ArrayList<>();
        instrSet3.add(new PortfolioInstrument(122L, 325L));
        instrSet3.add(new PortfolioInstrument(123L, 328L));
        instrSet3.add(new PortfolioInstrument(124L, 312L));

        portfolioCalculator.getPortfolios().add(new Portfolio(1L, instrSet1));
        portfolioCalculator.getPortfolios().add(new Portfolio(2L, instrSet2));
        portfolioCalculator.getPortfolios().add(new Portfolio(3L, instrSet3));
    }

}
