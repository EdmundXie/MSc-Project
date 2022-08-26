package binpacklib;

import algos.Algo2DSpreadWFDAvg;
import algos.AlgoFit2D;
import com.google.common.collect.Lists;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static String doOneRun(Instance instance, List<String> algosList) {
        int lb = LBUtil.bpp2DLb(instance);
        StringBuilder row = new StringBuilder(lb);
        int bestSolution = instance.getTotalinstances();
        String bestAlgo = "";
        StringBuilder rowRes = new StringBuilder();
        StringBuilder rowTime = new StringBuilder();
        int solution;
        AlgoFit2D algoFit2D;
        for (String algoName : algosList) {
            algoFit2D = AlgoFit2D.createAlgo2D(algoName, instance);
            if (algoFit2D != null) {
                long startTime = System.currentTimeMillis();
                solution = algoFit2D.solveInstance();
                long stopTime = System.currentTimeMillis();
                long duration = stopTime - startTime;
                if (solution < bestSolution) {
                    bestSolution = solution;
                    bestAlgo = algoName;
                }
                rowRes.append("\t").append(solution);
                rowTime.append("\t").append(duration);
                System.out.println(rowRes.toString());
                System.out.println(rowTime.toString());
            } else {
                System.out.println("can not find the algorithm: " + algoName);
            }
        }
        row.append(lb).append("\t").append(bestSolution).append("\t").append(bestAlgo);
        return row.append(rowRes).append(rowTime).toString();
    }

    public static int doRun(String inputPath, String outFile, List<String> algoList, int cpuCapacityOfBin, int memoryCapacityOfBin) throws IOException {
        File file = new File(outFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        CSVWriter csvWriter = new CSVWriter(new FileWriter(outFile));
        List<String> header = Lists.newArrayList("instance_name", "LB", "best_sol", "best_algo");
        List<String> timeHeader = new ArrayList<>();
        for (String algoName : algoList) {
            header.add(algoName);
            timeHeader.add(algoName + "_time");
        }
        header.addAll(timeHeader);
        csvWriter.writeNext(header.toArray(new String[0]));
        csvWriter.flush();
        String inputFile = inputPath+"processed_data.csv";
        Instance instance = new Instance("processed_data", cpuCapacityOfBin, memoryCapacityOfBin, inputFile);
        String rowStr = doOneRun(instance, algoList);
        List<String> resultList = Lists.newArrayList("processed_data");
        resultList.addAll(Arrays.asList(rowStr.split("\t")));
        csvWriter.writeNext(resultList.toArray(new String[0]));
        csvWriter.flush();
        csvWriter.close();
        return 0;
    }

    public static void main(String[] args) throws IOException {
        int cpuCapacityOfBin = 32;
        int memCapacityOfBin = 64;
        String inputPath = "src/data/input/";
        String outFileName = "src/data/results/"+"result.csv";
        List<String> algos = Lists.newArrayList("FF","FFD-Avg","FFD-AvgExpo","NCD-DotProduct","NCD-L2Norm");
        doRun(inputPath, outFileName, algos, cpuCapacityOfBin, memCapacityOfBin);
        System.out.println("success!");
    }
}
