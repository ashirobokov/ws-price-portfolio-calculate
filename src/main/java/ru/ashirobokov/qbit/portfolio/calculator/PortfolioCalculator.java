package ru.ashirobokov.qbit.portfolio.calculator;

import io.advantageous.qbit.reactive.Callback;
import ru.ashirobokov.qbit.portfolio.model.InstrumentPrice;

import java.util.List;

public interface PortfolioCalculator {
    void sendPrices(Callback<Boolean> callback, Long timestamp, List<InstrumentPrice> instrumentPrices);
}
