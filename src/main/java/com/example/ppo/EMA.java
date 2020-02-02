package com.example.ppo;

import com.example.data.Record;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EMA {

    public static BigDecimal percentagePriceOscillator(List<Record> allRecords, int period) {
        // PPO= ( 12-period EMA−26-period EMA / 26-period EMA ) ×100
        List<BigDecimal> period12 = getClosePrices(allRecords, 12);
        List<BigDecimal> period26 = getClosePrices(allRecords, 26);


        //    simpleMovingAverage()


        //  BigDecimal Ema12 = exponentialMovingAverage(BigDecimal SMA, )

        return null;
    }

    private static List<BigDecimal> getClosePrices(List<Record> list, int period) {
        List<BigDecimal> closePrices =
                list.stream()
                        .sorted(Comparator.comparing(Record::getDate).reversed())
                        .limit(period)
                        .map(record -> record.getClose())
                        .collect(Collectors.toList());
        return closePrices;
    }


    public static BigDecimal simpleMovingAverage(List<BigDecimal> closePrices, int period) {
        return closePrices.stream()
                .limit(period)
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalArgumentException::new)
                .divide(new BigDecimal(period));
    }

    // formula: Close - previous EMA] * (2 / n+1) + previous EMA.
    public static BigDecimal exponentialMovingAverage(BigDecimal prevEMA, BigDecimal todayClosePrice, int period) {
        BigDecimal multiplier = new BigDecimal("2")
                .divide(new BigDecimal(period).add(BigDecimal.ONE));

        BigDecimal EMA = todayClosePrice.multiply(multiplier)
                .add(prevEMA.multiply(BigDecimal.ONE.subtract(multiplier)));
        return EMA;
    }

}
