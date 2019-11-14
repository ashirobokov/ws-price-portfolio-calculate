package ru.ashirobokov.qbit.portfolio.model;

import java.math.BigDecimal;

public class InstrumentTimePrice extends InstrumentPrice {
    long time_;

    public InstrumentTimePrice(Long instrumentId, BigDecimal price, long time_) {
        super(instrumentId, price);
        this.time_ = time_;
    }

    public long getTime_() {
        return time_;
    }

}
