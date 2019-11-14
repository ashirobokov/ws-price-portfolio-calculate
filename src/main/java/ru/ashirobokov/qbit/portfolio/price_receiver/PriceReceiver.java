package ru.ashirobokov.qbit.portfolio.price_receiver;

import io.advantageous.qbit.reactive.Callback;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;

import java.util.List;

public interface PriceReceiver {
    void addPrices(Callback<Boolean> callback, Long timestamp, List<InstrumentPrice> instrumentPrices);
    void getInstrPricesSize(Callback<Integer> callback);
}
