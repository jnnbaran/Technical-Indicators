package com.example.adx;

import com.example.data.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ADX {

    public static List<BigDecimal> positiveDirectionalIndicator(List<Record> list, int periods) {
        List<BigDecimal> positiveDirectionalIndicators = new ArrayList<>();
        List<Record> trendList = getLatestNRecordsList(list, periods);

        for (Record currentRecord : trendList) {
            Stream<Record> previousNRecords = getPreviousNRecordsStream(list, currentRecord, periods);

            BigDecimal positiveDirectionalMovement = previousNRecords
                    .map(Record::getHigh)
                    .reduce(BigDecimal::add)
                    .orElseThrow(IllegalArgumentException::new);

            BigDecimal smoothedPositiveDirectionalMovement = positiveDirectionalMovement
                    .subtract(positiveDirectionalMovement
                            .divide(new BigDecimal(periods), BigDecimal.ROUND_HALF_EVEN))
                    .add(currentRecord.getHigh());

            BigDecimal averageTrueRange = averageTrueRange(list, currentRecord, periods);

            positiveDirectionalIndicators.add(smoothedPositiveDirectionalMovement
                    .divide(averageTrueRange, BigDecimal.ROUND_HALF_EVEN)
                    .multiply(new BigDecimal("100")));
        }
        return positiveDirectionalIndicators;
    }

    public static List<BigDecimal> negativeDirectionalIndicator(List<Record> list, int periods) {
        List<BigDecimal> negativeDirectionalIndicators = new ArrayList<>();
        List<Record> trendList = getLatestNRecordsList(list, periods);

        for (Record currentRecord : trendList) {
            Stream<Record> previousNRecords = getPreviousNRecordsStream(list, currentRecord, periods);

            BigDecimal negativeDirectionalMovement = previousNRecords
                    .map(Record::getLow)
                    .reduce(BigDecimal::add)
                    .orElseThrow(IllegalArgumentException::new);

            BigDecimal smoothedNegativeDirectionalMovement = negativeDirectionalMovement
                    .subtract(negativeDirectionalMovement
                            .divide(new BigDecimal(periods), BigDecimal.ROUND_HALF_EVEN))
                    .add(currentRecord.getLow());

            BigDecimal averageTrueRange = averageTrueRange(list, currentRecord, periods);

            negativeDirectionalIndicators.add(smoothedNegativeDirectionalMovement
                    .divide(averageTrueRange, BigDecimal.ROUND_HALF_EVEN)
                    .multiply(new BigDecimal("100")));
        }
        return negativeDirectionalIndicators;
    }


    public static List<Record> getLatestNRecordsList(List<Record> list, int periods) {
        return list.stream()
                .sorted(Comparator.comparing(Record::getDate))
                .filter(record -> list.indexOf(record) > periods)
                .collect(Collectors.toList());
    }

    public static Stream<Record> getPreviousNRecordsStream(List<Record> list, Record currentRecord, int periods) {
        return list.stream().filter(
                record -> list.indexOf(currentRecord) - list.indexOf(record) < periods &&
                        list.indexOf(currentRecord) > list.indexOf(record)
        );
    }

    public static BigDecimal averageTrueRange(List<Record> list, Record currentRecord, int periods) {
        return getPreviousNRecordsStream(list, currentRecord, periods)
                .map(record -> {
                    BigDecimal highMinusLow = record.getHigh().subtract(record.getLow());
                    if (list.indexOf(record) > 0) {
                        BigDecimal highMinusPreviousCloseAbs = record.getHigh()
                                .subtract(list.get(list.indexOf(record) - 1).getClose()).abs();
                        BigDecimal LowMinusPreviousCloseAbs = record.getLow().
                                subtract(list.get(list.indexOf(record) - 1).getClose()).abs();
                        return highMinusPreviousCloseAbs.max(LowMinusPreviousCloseAbs).max(highMinusLow);
                    }
                    return highMinusLow;
                })
                .reduce(BigDecimal::add)
                .orElseThrow(IllegalArgumentException::new)
                .divide(new BigDecimal(periods), BigDecimal.ROUND_HALF_EVEN);
    }

    public static List<BigDecimal> directionalIndex(List<Record> list, int period) {
        List<Record> latestRecords = getLatestNRecordsList(list, period);
        List<BigDecimal> positiveDirectionalIndicator = positiveDirectionalIndicator(list,period);
        List<BigDecimal> negativeDirectionalIndicator = negativeDirectionalIndicator(list,period);
        List<BigDecimal> directionalIndexes = new ArrayList<>();

        for (Record record : latestRecords) {
            directionalIndexes.add(
                    positiveDirectionalIndicator.get(latestRecords.indexOf(record))
                    .subtract(negativeDirectionalIndicator.get(latestRecords.indexOf(record))).abs()
                    .divide(positiveDirectionalIndicator.get(latestRecords.indexOf(record))
                            .add(negativeDirectionalIndicator.get(latestRecords.indexOf(record)))
                            .abs(), BigDecimal.ROUND_HALF_EVEN)
                    .multiply(new BigDecimal("100"))
            );
        }
        return directionalIndexes;
    }

    public static List<BigDecimal> averageDirectionalIndex() {
        return null;
    }
}