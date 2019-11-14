package ru.ashirobokov.qbit.portfolio.model;

import java.math.BigDecimal;

public class PortfolioInstrument {
    Long instrumentId;
    Long number;
/* Стоимость инструмента в портфеле цена*количество  */
    BigDecimal value;

    public PortfolioInstrument(Long instrumentId, Long number) {
        this.instrumentId = instrumentId;
        this.number = number;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public Long getNumber() {
        return number;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{" +
                " instrumentId=" + instrumentId +
                ", number=" + number +
                ", value=" + value +
                '}';
    }
}
