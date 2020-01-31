package com.example.adx;

import com.example.adx.exc.NotEnoughDataForGivenRange;
import com.example.data.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ADX {

    public static BigDecimal countAverageDirectionalIndex(List<Record> allRecords, int range, int period) {
        List<List<Record>> recordsWithHistory = new ArrayList<>();
        List<BigDecimal> directionalIndexes = new ArrayList<>();
        List<Record> latestRecords = getLatestNRecordsList(allRecords, range);

        latestRecords.stream()
                .filter(record -> allRecords.size() - allRecords.indexOf(record) >= period + 1)
                .forEach(
                currentRecord -> recordsWithHistory.add(getRecordsWithHistory(allRecords, currentRecord, period))
        );

        for(List<Record> records : recordsWithHistory) {
            BigDecimal pdi = countPositiveDirectionalIndicator(records);
            BigDecimal ndi = countNegativeDirectionalIndicator(records);
            BigDecimal di = countDirectionalIndex(pdi, ndi);
            directionalIndexes.add(di);
        }

        return directionalIndexes.stream()
                .reduce(BigDecimal::add)
                .orElseThrow(NotEnoughDataForGivenRange::new)
                .divide(new BigDecimal(directionalIndexes.size()), BigDecimal.ROUND_HALF_EVEN);
    }

    public static List<Record> getLatestNRecordsList(List<Record> list, int range) {
        List<Record> records = list.stream()
                .sorted(Comparator.comparing(Record::getDate))
                .filter(record -> list.indexOf(record) < range)
                .collect(Collectors.toList());
        Collections.reverse(records);
        return records;
    }

    public static List<Record> getRecordsWithHistory(List<Record> list, Record currentRecord, int periods) {
        return list.stream().filter(
                record -> list.indexOf(record) - list.indexOf(currentRecord) <= periods &&
                          list.indexOf(record) >= list.indexOf(currentRecord)
        ).collect(Collectors.toList());
    }

    public static BigDecimal countPositiveDirectionalIndicator(List<Record> records) {
        BigDecimal smoothedPositiveDirectionalMovement = countSmoothedDirectionalMovement(
                records.stream()
                        .map(Record::getHigh)
                        .collect(Collectors.toList())
        );
        BigDecimal averageTrueRange = averageTrueRange(records);
        return smoothedPositiveDirectionalMovement
                .divide(averageTrueRange, BigDecimal.ROUND_HALF_EVEN)
                .multiply(new BigDecimal("100"));
    }

    public static BigDecimal countNegativeDirectionalIndicator(List<Record> records) {
            BigDecimal smoothedNegativeDirectionalMovement = countSmoothedDirectionalMovement(
                    records.stream()
                            .map(Record::getLow)
                            .collect(Collectors.toList())
            );
            BigDecimal averageTrueRange = averageTrueRange(records);
            return smoothedNegativeDirectionalMovement
                    .divide(averageTrueRange, BigDecimal.ROUND_HALF_EVEN)
                    .multiply(new BigDecimal("100"));
    }

    public static BigDecimal countSmoothedDirectionalMovement(List<BigDecimal> records) {
        BigDecimal directionalMovement = records.stream().reduce(BigDecimal::add).orElseThrow(IllegalArgumentException::new);
        return directionalMovement.subtract(directionalMovement
                .divide(new BigDecimal(records.size() - 1), BigDecimal.ROUND_HALF_EVEN));
    }

    public static BigDecimal averageTrueRange(List<Record> list) {
        return list.stream()
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
                .divide(new BigDecimal(list.size()), BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal countDirectionalIndex(BigDecimal positiveDirectionalIndicator, BigDecimal negativeDirectionalIndicator) {
        return positiveDirectionalIndicator
                .subtract(negativeDirectionalIndicator).abs()
                .divide(
                        positiveDirectionalIndicator.add(negativeDirectionalIndicator).abs(),
                        BigDecimal.ROUND_HALF_EVEN
                )
                .multiply(new BigDecimal("100"));
    }
}