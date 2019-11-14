package ru.ashirobokov.qbit.portfolio.model;

import java.math.BigDecimal;
import java.util.List;

public class Portfolio {
    Long portfolioId;
    List<PortfolioInstrument> instruments;
    /* Стоимость портфеля */
    BigDecimal value;

    public Portfolio(Long portfolioId, List<PortfolioInstrument> instruments) {
        this.portfolioId = portfolioId;
        this.instruments = instruments;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public List<PortfolioInstrument> getInstruments() {
        return instruments;
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
                "id=" + portfolioId +
                ", instruments=" + instruments +
                '}';
    }
}
