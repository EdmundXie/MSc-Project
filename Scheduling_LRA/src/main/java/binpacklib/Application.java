package binpacklib;

import org.apache.commons.collections.MapUtils;
import util.AffinitiyMap;

import java.util.*;

public class Application {
    private String id;// 应用id
    private int entityId;// 实体id，自增
    private int instanceNums;// 副本个数
    private int cpuOfinstance;// 每个副本的cpu个数
    private int memOfinstance;// 每个副本的内存个数
    double normCpus;// cpuOfinstance/cpuOfBin
    double normMem;// memOfinstance/memOfBin
    boolean fullyPacked;
    AffinitiyMap outMap = new AffinitiyMap();
    AffinitiyMap inMap = new AffinitiyMap();
    int sizeOfOutMap;
    int totalDegree;
    double avgSize;
    double maxSize;
    double surrogateSize;
    double extSize;
    double avgExpoSize;
    double measure;// 测量值占位符

    public Application(String appId, int entityId, int instanceNums, int cpuOfinstance, int memOfinstance, int sizeOfOutMap, AffinitiyMap outMap) {
        this.id = appId;
        this.entityId = entityId;
        this.instanceNums = instanceNums;
        this.cpuOfinstance = cpuOfinstance;
        this.memOfinstance = memOfinstance;
        this.sizeOfOutMap = sizeOfOutMap;
        this.outMap = outMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getinstanceNums() {
        return instanceNums;
    }

    public void setinstanceNums(int instanceNums) {
        this.instanceNums = instanceNums;
    }

    public int getCpuOfinstance() {
        return cpuOfinstance;
    }

    public void setCpuOfinstance(int cpuOfinstance) {
        this.cpuOfinstance = cpuOfinstance;
    }

    public int getMemOfinstance() {
        return memOfinstance;
    }

    public void setMemOfinstance(int memOfinstance) {
        this.memOfinstance = memOfinstance;
    }

    public double getNormCpus() {
        return normCpus;
    }

    public void setNormCpus(double normCpus) {
        this.normCpus = normCpus;
    }

    public double getNormMem() {
        return normMem;
    }

    public void setNormMem(double normMem) {
        this.normMem = normMem;
    }

    public boolean isFullyPacked() {
        return fullyPacked;
    }

    public void setFullyPacked(boolean fullyPacked) {
        this.fullyPacked = fullyPacked;
    }

    public AffinitiyMap getOutMap() {
        return outMap;
    }

    public void setOutMap(AffinitiyMap outMap) {
        this.outMap = outMap;
    }

    public AffinitiyMap getInMap() {
        return inMap;
    }

    public void setInMap(AffinitiyMap inMap) {
        this.inMap = inMap;
    }

    public int getSizeOfOutMap() {
        return sizeOfOutMap;
    }

    public void setSizeOfOutMap(int sizeOfOutMap) {
        this.sizeOfOutMap = sizeOfOutMap;
    }

    public int getTotalDegree() {
        return totalDegree;
    }

    public void setTotalDegree(int totalDegree) {
        this.totalDegree = totalDegree;
    }

    public double getAvgSize() {
        return avgSize;
    }

    public void setAvgSize(double avgSize) {
        this.avgSize = avgSize;
    }

    public double getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(double maxSize) {
        this.maxSize = maxSize;
    }

    public double getSurrogateSize() {
        return surrogateSize;
    }

    public void setSurrogateSize(double surrogateSize) {
        this.surrogateSize = surrogateSize;
    }

    public double getExtSize() {
        return extSize;
    }

    public void setExtSize(double extSize) {
        this.extSize = extSize;
    }

    public double getAvgExpoSize() {
        return avgExpoSize;
    }

    public void setAvgExpoSize(double avgExpoSize) {
        this.avgExpoSize = avgExpoSize;
    }

    public double getMeasure() {
        return measure;
    }

    public void setMeasure(double measure) {
        this.measure = measure;
    }

    public void initInMap(AffinitiyMap inMap) {
        this.inMap = inMap;
        Set<String> nSet = new HashSet<>();
        if (MapUtils.isEmpty(inMap)) {
            this.inMap = new AffinitiyMap();
            return;
        }
        for (Map.Entry<String, Integer> entry : outMap.entrySet()) {
            nSet.add(entry.getKey());
        }
        for (Map.Entry<String, Integer> entry : inMap.entrySet()) {
            nSet.add(entry.getKey());
        }
        totalDegree = nSet.size();
    }

    public void removeIllegalData(List<String> readyToRemove) {
        for (String appStr : readyToRemove) {
            inMap.remove(appStr);
            outMap.remove(appStr);
        }
        sizeOfOutMap = outMap.size();
    }

    public void init(double totalCpus, double totalMemories, int totalinstances, int cpuCapacityOfBin, int memoryCapacityofBin) {
        normCpus = (double) cpuOfinstance / (double) cpuCapacityOfBin;
        normMem = (double) memOfinstance / (double) memoryCapacityofBin;
        double lambda = totalCpus / (totalCpus + totalMemories); // Lamba (for surrogate)
        double wCpu = totalCpus / (totalinstances * cpuCapacityOfBin); // Avg normalized cpu size
        double wMem = totalMemories / (totalinstances * memoryCapacityofBin); // Avg normalized mem size
        surrogateSize = lambda * normCpus + (1 - lambda) * normMem;
        extSize = ((instanceNums * cpuOfinstance) / totalCpus) + ((instanceNums * memOfinstance) / totalMemories);
        avgSize = normCpus + normMem;
        maxSize = Math.max(normCpus, normMem);
        avgExpoSize = Math.exp(0.01 * wCpu) * normCpus + Math.exp(0.01 * wMem) * normMem;
    }


    public static Application getApp2D(List<Application> app2DList, String appId) {
        for (Application application : app2DList) {
            if (application.getId().equals(appId)) {
                return application;
            }
        }
        return null;
    }

    public static void bubbleAppUp(List<Application> app2DList, int fromIndex, int toIndex, Comparator<Application> comparator) {
        if (fromIndex == toIndex) {
            return;
        }
        --toIndex;
        if (fromIndex == toIndex) {
            return;
        }
        int current = toIndex;
        int previous = toIndex - 1;
        while (fromIndex != previous) {
            if (comparator.compare(app2DList.get(current), app2DList.get(previous)) > 0) {
                Collections.swap(app2DList, current, previous);
            }
            --current;
            --previous;
        }
        if (comparator.compare(app2DList.get(current), app2DList.get(previous)) > 0) {
            Collections.swap(app2DList, current, previous);
        }
    }
}
