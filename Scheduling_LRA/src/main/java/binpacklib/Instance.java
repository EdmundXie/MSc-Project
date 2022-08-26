package binpacklib;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.apache.commons.collections.CollectionUtils;
import util.AffinitiyMap;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Instance {
    private String id;
    private int cpuCapacityOfBin;
    private int memoryCapacityOfBin;
    private List<Application> appList = Lists.newArrayList();

    private int totalMemory;
    private int totalCPU;
    private int totalinstances;

    public Instance(String id, int cpuCapacity, int memoryCapacity, String fileName) {
        this.id = id;
        this.cpuCapacityOfBin = cpuCapacity;
        this.memoryCapacityOfBin = memoryCapacity;
        List<String> readyToRemove = Lists.newArrayList();
        Map<String, AffinitiyMap> inputMap = Maps.newHashMap();
        readFromCsv(fileName, readyToRemove, inputMap);
        filterApplication2DList(readyToRemove, inputMap);
    }

    private void readFromCsv(String fileName, List<String> readyToRemove, Map<String, AffinitiyMap> inputMap) {
        List<InputData> inputDataList;
        int entityId = 0;
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName), StandardCharsets.UTF_8)) {
            HeaderColumnNameMappingStrategy<InputData> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(InputData.class);
            CsvToBean<InputData> csvToBean = new CsvToBeanBuilder<InputData>(reader).withType(InputData.class).withMappingStrategy(strategy).withSeparator('\t').withIgnoreLeadingWhiteSpace(true).build();
            inputDataList = csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (InputData inputData : inputDataList) {
            if (inputData.getCore() <= this.cpuCapacityOfBin && inputData.getMemory() <= this.memoryCapacityOfBin) {
                AffinitiyMap outMap = new AffinitiyMap();
                outMap = outMap.fromStr(inputData.getInterAff());
                for (Map.Entry<String, Integer> entry : outMap.entrySet()) {
                    if (inputMap.containsKey(entry.getKey())) {
                        AffinitiyMap affinitiyMap = inputMap.get(entry.getKey());
                        affinitiyMap.put(inputData.getAppId(), entry.getValue());
                    } else {
                        AffinitiyMap affinitiyMap = new AffinitiyMap();
                        affinitiyMap.put(inputData.getAppId(), entry.getValue());
                        inputMap.put(entry.getKey(), affinitiyMap);
                    }
                }
                appList.add(new Application(inputData.getAppId(), entityId, inputData.getNbInstances(), inputData.getCore(),
                        inputData.getMemory(), inputData.getInterDegree(), outMap));
                entityId++;
                totalCPU += inputData.getCore() * inputData.getNbInstances();
                totalMemory += inputData.getMemory() * inputData.getNbInstances();
                totalinstances += inputData.getNbInstances();
            } else {
                readyToRemove.add(inputData.getAppId());
            }
        }
    }

    private void filterApplication2DList(List<String> readyToRemove, Map<String, AffinitiyMap> inputMap) {
        if (CollectionUtils.isEmpty(this.appList)) {
            return;
        }
        for (Application application : this.appList) {
            application.initInMap(inputMap.get(application.getId()));
            application.removeIllegalData(readyToRemove);
            application.init(totalCPU, totalMemory, totalinstances, cpuCapacityOfBin, memoryCapacityOfBin);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCpuCapacityOfBin() {
        return cpuCapacityOfBin;
    }

    public void setCpuCapacityOfBin(int cpuCapacityOfBin) {
        this.cpuCapacityOfBin = cpuCapacityOfBin;
    }

    public int getMemoryCapacityOfBin() {
        return memoryCapacityOfBin;
    }

    public void setMemoryCapacityOfBin(int memoryCapacityOfBin) {
        this.memoryCapacityOfBin = memoryCapacityOfBin;
    }

    public List<Application> getAppList() {
        return appList;
    }

    public void setAppList(List<Application> appList) {
        this.appList = appList;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getTotalCPU() {
        return totalCPU;
    }

    public void setTotalCPU(int totalCPU) {
        this.totalCPU = totalCPU;
    }

    public int getTotalinstances() {
        return totalinstances;
    }

    public void setTotalinstances(int totalinstances) {
        this.totalinstances = totalinstances;
    }

    // 输入csv文件映射对象
    public static class InputData {
        @CsvBindByName(column = "app_id")
        private String appId;
        @CsvBindByName(column = "instance_num")
        private int nbInstances;
        @CsvBindByName(column = "cpu")
        private int core;
        @CsvBindByName(column = "memory")
        private int memory;
        @CsvBindByName(column = "conflict_degree")
        private int interDegree;
        @CsvBindByName(column = "conflict")
        private String interAff;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public int getNbInstances() {
            return nbInstances;
        }

        public void setNbInstances(int nbInstances) {
            this.nbInstances = nbInstances;
        }

        public int getCore() {
            return core;
        }

        public void setCore(int core) {
            this.core = core;
        }

        public int getMemory() {
            return memory;
        }

        public void setMemory(int memory) {
            this.memory = memory;
        }

        public int getInterDegree() {
            return interDegree;
        }

        public void setInterDegree(int interDegree) {
            this.interDegree = interDegree;
        }

        public String getInterAff() {
            return interAff;
        }

        public void setInterAff(String interAff) {
            this.interAff = interAff;
        }
    }
}
