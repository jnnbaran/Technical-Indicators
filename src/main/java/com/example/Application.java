package com.example;

import com.example.adx.ADX;
import com.example.data.Reader;
import com.example.data.Record;
import com.example.data.WrongDataFormatException;

import java.util.List;
import java.util.stream.Collectors;

public class Application {
    public static void main(String[] args) {
        try {
            System.out.println("Starting....");
            List<Record> recordList = Reader.readFromFile("src/main/resources/data/2CPARTNER.mst");
            System.out.println(ADX.countAverageDirectionalIndex(recordList, 1,14));
        } catch (WrongDataFormatException e) {
            System.out.println("Couldn't read file... \n" + e.getMessage());
        }
    }


}
