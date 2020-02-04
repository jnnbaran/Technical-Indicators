package com.example.ppo;

import com.example.data.Record;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EMA {
    // PPO= ( 12-period EMA−26-period EMA / 26-period EMA ) ×100
    public static BigDecimal percentagePriceOscillator(List<Record> allRecords) {
        List<Record> period12RecordsRange = getRecords(allRecords, 12);
        List<Record> period26RecordsRange = getRecords(allRecords, 26);

        BigDecimal period12CurrentEMA = countCurrentEMA(period12RecordsRange, 12);
        BigDecimal period26CurrentEMA = countCurrentEMA(period26RecordsRange, 26);

        return period12CurrentEMA
                .subtract(period26CurrentEMA)
                .divide(period26CurrentEMA, BigDecimal.ROUND_HALF_EVEN)
                .multiply(new BigDecimal("100"));
    }

    private static BigDecimal countCurrentEMA(List<Record> periodPricesRange, int period) {
        BigDecimal prevEMA = simpleMovingAverage(periodPricesRange, period);
        for (Record price : periodPricesRange.subList(period + 1, periodPricesRange.size())) {
            prevEMA = exponentialMovingAverage(prevEMA, price.getClose(), period);
        }
        return prevEMA;
    }

    private static List<Record> getRecords(List<Record> records, int period) {
            return records.stream()
                    .filter(record -> records.indexOf(record) >= records.size() - period*2)
                    .collect(Collectors.toList());
    }


    private static BigDecimal simpleMovingAverage(List<Record> records, int period) {
        return records.stream()
                .filter(closePrice -> records.indexOf(closePrice) < period)
                .map(Record::getClose)
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalArgumentException::new)
                .divide(new BigDecimal(period), BigDecimal.ROUND_HALF_EVEN);
    }

    private static BigDecimal exponentialMovingAverage(BigDecimal prevEMA, BigDecimal todayClosePrice, int period) {
        BigDecimal multiplier = new BigDecimal("2.000")
                .divide(new BigDecimal(period).add(BigDecimal.ONE), BigDecimal.ROUND_HALF_EVEN);

        return todayClosePrice.subtract(prevEMA).multiply(multiplier).add(prevEMA);
    }
}
