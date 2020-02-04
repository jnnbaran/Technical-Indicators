package com.example;

import com.example.adx.ADX;
import com.example.data.Reader;
import com.example.data.Record;
import com.example.data.WrongDataFormatException;
import com.example.ppo.EMA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {
    public static void main(String[] args) {
        try{
            List<Record> recordList = Reader.readFromFile("src/main/resources/data/MBANK.mst");
            System.out.println(EMA.percentagePriceOscillator(recordList));
            System.out.println(ADX.countAverageDirectionalIndex(recordList, 14));
        } catch(WrongDataFormatException e){
            System.out.println("Couldn't read file due to wrong format... \n" + e.getMessage());
        }
    }


    private static void runADXForAllFiles() {
        try (Stream<Path> pathsStream = Files.walk(Paths.get("src/main/resources/data"))) {
            List<Path> paths = pathsStream.filter(Files::isRegularFile).collect(Collectors.toList());

            for (Path path : paths) {
                System.out.println("counting for " + path.toString());
                List<Record> recordList = Reader.readFromFile(path.toString());
                System.out.println(ADX.countAverageDirectionalIndex(recordList, 14));
            }
        } catch (IOException e) {
            System.out.println("IOException");
        } catch(WrongDataFormatException e){
            System.out.println("Couldn't read file due to wrong format... \n" + e.getMessage());
        }
    }
}

