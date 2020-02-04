package com.example.data;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Reader {

    public static List<Record> readFromFile(String filePath) throws WrongDataFormatException {
        Path path = Paths.get(filePath);
        List<Record> records = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(path);
            lines = lines.subList(1, lines.size());
            for(String line : lines) {
                String[] recordParams = line.split(",");
                if(recordParams.length == 7) {
                    records.add(new Record.Builder()
                            .ticker(recordParams[0])
                            .date(new SimpleDateFormat("yyyyMMdd").parse(recordParams[1]))
                            .open(new BigDecimal(recordParams[2]))
                            .high(new BigDecimal(recordParams[3]))
                            .low(new BigDecimal(recordParams[4]))
                            .close(new BigDecimal(recordParams[5]))
                            .vol(Integer.parseInt(recordParams[6]))
                            .build());
                } else {
                    throw new WrongDataFormatException(String.format("Too few or too many params. 7 != %d", recordParams.length));
                }
            }
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }

        return records
                .stream()
                .sorted(Comparator.comparing(Record::getDate))
                .collect(Collectors.toList());
    }

    public static List<Record> getLatestNRecordsList(List<Record> records, int period) {
        return records.stream()
                .filter(record -> records.indexOf(record) >= records.size() - period*2)
                .collect(Collectors.toList());
    }

    public static List<Record> getRecordWithHistory(List<Record> list, Record currentRecord, int periods) {
        return list.stream().filter(
                record -> list.indexOf(record) - list.indexOf(currentRecord) <= periods &&
                        list.indexOf(record) >= list.indexOf(currentRecord)
        ).collect(Collectors.toList());
    }
}
