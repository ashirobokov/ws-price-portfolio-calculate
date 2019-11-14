package ru.ashirobokov.qbit.portfolio.price_receiver;

import io.advantageous.qbit.reactive.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PriceReceiverImpl {
    private final static Logger LOG = LoggerFactory.getLogger(PriceReceiverImpl.class);
    private final ConcurrentNavigableMap<Long, List<InstrumentPrice>> instrumentPrices = new ConcurrentSkipListMap<>();

    public ConcurrentNavigableMap<Long, List<InstrumentPrice>> getInstrumentPrices() {
        return instrumentPrices;
    }

    public void addPrices(Callback<Boolean> callback, Long timestamp, List<InstrumentPrice> instrumentPrices) {
        LOG.info("PriceReceiverSrv...PriceReceiverImpl.addPrices for timestamp {} prices {}", timestamp, instrumentPrices);
        this.instrumentPrices.putIfAbsent(timestamp, new ArrayList<>(instrumentPrices));
        printInstrumentPrices();
        callback.accept(true);
    }

    public void getInstrPricesSize(Callback<Integer> callback) {
        LOG.info("PriceReceiverSrv...PriceReceiverImpl.getSize");
        callback.accept(instrumentPrices.size());
    }

    private void printInstrumentPrices() {
        LOG.info("PriceReceiverSrv... InstrumentPrices full map");
        instrumentPrices.forEach((timestamp, list) -> {
            LOG.info(".... timestamp = {} .... list {}", timestamp, list);
        });
    }

/*
    public void printInstrumentPricesEntries() {
        LOG.info("PriceReceiverSrv..." +
                " InstrumentPrices full map [Entry Set]");
        Set<Map.Entry<Long, List<InstrumentPrice>>> entrySet = instrumentPrices.entrySet();
        for (Map.Entry<Long, List<InstrumentPrice>> entry : entrySet) {
            LOG.info(".... timestamp = {} .... list {}", entry.getKey(), entry.getValue());
        }
    }
*/
}
