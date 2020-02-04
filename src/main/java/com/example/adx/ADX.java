package com.example.adx;

import com.example.adx.exc.NotEnoughDataForGivenRange;
import com.example.data.Reader;
import com.example.data.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ADX {

    public static BigDecimal countAverageDirectionalIndex(List<Record> allRecords, int period) {
        List<List<Record>> recordsWithHistory = new ArrayList<>();
        List<BigDecimal> directionalIndexes = new ArrayList<>();
        List<Record> latestRecords = Reader.getLatestNRecordsList(allRecords, period);

        latestRecords.stream()
                .filter(record -> allRecords.size() - allRecords.indexOf(record) > period)
                .forEach(
                currentRecord -> recordsWithHistory.add(Reader.getRecordWithHistory(allRecords, currentRecord, period))
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

    private static BigDecimal countPositiveDirectionalIndicator(List<Record> records) {
        List<BigDecimal> directionalMovement = new ArrayList<>();

        for(int i=1; i<records.size(); ++i) {
            if(records.get(i).getHigh().subtract(records.get(i-1).getHigh()).compareTo(records.get(i-1).getLow().subtract(records.get(i).getLow())) > 0) {
                directionalMovement.add(records.get(i).getHigh().subtract(records.get(i-1).getHigh()));
            } else {
                directionalMovement.add(BigDecimal.ZERO);
            }
        }

        BigDecimal smoothedPositiveDirectionalMovement = countSmoothedDirectionalMovement(directionalMovement);
        return countDirectionalIndicator(records.subList(1,records.size()), smoothedPositiveDirectionalMovement);
    }

    private static BigDecimal countNegativeDirectionalIndicator(List<Record> records) {
        List<BigDecimal> directionalMovement = new ArrayList<>();

        for(int i=1; i<records.size(); ++i) {
            if(records.get(i-1).getLow().subtract(records.get(i).getLow()).compareTo(records.get(i).getHigh().subtract(records.get(i-1).getHigh())) > 0) {
                directionalMovement.add(records.get(i-1).getLow().subtract(records.get(i).getLow()));
            } else {
                directionalMovement.add(BigDecimal.ZERO);
            }
        }

        BigDecimal smoothedNegativeDirectionalMovement = countSmoothedDirectionalMovement(directionalMovement);
        return countDirectionalIndicator(records.subList(1,records.size()), smoothedNegativeDirectionalMovement);
    }

    private static BigDecimal countDirectionalIndicator(List<Record> records, BigDecimal smoothedDirectionalIndicator) {
        BigDecimal averageTrueRange = countAverageTrueRange(records);
        return smoothedDirectionalIndicator
                .divide(averageTrueRange, BigDecimal.ROUND_HALF_EVEN)
                .multiply(new BigDecimal("100"));
    }

    private static BigDecimal countSmoothedDirectionalMovement(List<BigDecimal> records) {
        BigDecimal directionalMovement = records.stream().reduce(BigDecimal::add).orElseThrow(IllegalArgumentException::new);
        return directionalMovement.subtract(directionalMovement
                .divide(new BigDecimal(records.size()), BigDecimal.ROUND_HALF_EVEN))
                .add(records.get(records.size() - 1));
    }

    private static BigDecimal countAverageTrueRange(List<Record> list) {
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

    private static BigDecimal countDirectionalIndex(BigDecimal positiveDirectionalIndicator, BigDecimal negativeDirectionalIndicator) {
        return positiveDirectionalIndicator
                .subtract(negativeDirectionalIndicator).abs()
                .divide(
                        positiveDirectionalIndicator.add(negativeDirectionalIndicator).abs(),
                        BigDecimal.ROUND_HALF_EVEN
                )
                .multiply(new BigDecimal("100"));
    }
}