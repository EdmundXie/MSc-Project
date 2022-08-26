package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AlgoFit2D {
    private String instanceName;
    private int cpuCapacityOfBin;
    private int memoryCapacityOfBin;
    private int totalinstances;
    private int totalCpus;
    private int totalMems;
    private double normCpus;
    private double normMems;
    private int nextBinIndex;
    private int currentBinIndex;
    private List<Application> app2DList = Lists.newArrayList();
    private List<Bin> binList = Lists.newArrayList();
    private boolean solved;

    public int solveInstance() {
        if (isSolved()) {
            return getSolutions();
        }
        allocateBatch(getApp2DList());
        solved = true;
        return getSolutions();
    }

    public int solvePerBatch(int batchSize, int bins) {
        if (solved) {
            return getSolutions();
        }
        int currentIndex = 0;
        int endIndex = 0;
        int remainingApps = getApp2DList().size();
        while (remainingApps > batchSize) {
            currentIndex = endIndex;
            endIndex = currentIndex + batchSize;
            allocateBatch(getApp2DList().subList(currentIndex, endIndex));
            remainingApps -= batchSize;
        }
        allocateBatch(getApp2DList().subList(endIndex, getApp2DList().size() - 1));
        solved = true;
        return getSolutions();
    }

    public void clearSolutions() {
        solved = false;
        binList.clear();
        nextBinIndex = 0;
    }

    public int getSolutions() {
        return getBin2DList().size();
    }

    List<Bin> getCopyOfBinList() {
        List<Bin> newList = new ArrayList<>(getBin2DList().size());
        CollectionUtils.addAll(newList, new Object[getBin2DList().size()]);
        Collections.copy(newList, getBin2DList());
        return newList;
    }

    public int getTotalinstances() {
        return totalinstances;
    }

    public void setTotalinstances(int totalinstances) {
        this.totalinstances = totalinstances;
    }

    public int getTotalCpus() {
        return totalCpus;
    }

    public void setTotalCpus(int totalCpus) {
        this.totalCpus = totalCpus;
    }

    public int getTotalMems() {
        return totalMems;
    }

    public void setTotalMems(int totalMems) {
        this.totalMems = totalMems;
    }

    public double getNormCpus() {
        return normCpus;
    }

    public void setNormCpus(double normCpus) {
        this.normCpus = normCpus;
    }

    public double getNormMems() {
        return normMems;
    }

    public void setNormMems(double normMems) {
        this.normMems = normMems;
    }

    public int getNextBinIndex() {
        return nextBinIndex;
    }

    public void setNextBinIndex(int nextBinIndex) {
        this.nextBinIndex = nextBinIndex;
    }

    public int getCurrentBinIndex() {
        return currentBinIndex;
    }

    public void setCurrentBinIndex(int currentBinIndex) {
        this.currentBinIndex = currentBinIndex;
    }

    public List<Application> getApp2DList() {
        return app2DList;
    }

    public void setApp2DList(List<Application> app2DList) {
        this.app2DList = app2DList;
    }

    public List<Bin> getBin2DList() {
        return binList;
    }

    public void setBin2DList(List<Bin> binList) {
        this.binList = binList;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
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

    public void setSolution(List<Bin> binList) {
        clearSolutions();
        this.binList = binList;
        solved = true;
    }

    public AlgoFit2D(Instance instance) {
        this.instanceName = instance.getId();
        this.cpuCapacityOfBin = instance.getCpuCapacityOfBin();
        this.memoryCapacityOfBin = instance.getMemoryCapacityOfBin();
        this.totalinstances = instance.getTotalinstances();
        this.totalCpus = instance.getTotalCPU();
        this.totalMems = instance.getTotalMemory();
        this.normCpus = (double) this.totalCpus / this.cpuCapacityOfBin;
        this.normMems = (double) this.totalMems / this.memoryCapacityOfBin;
        this.app2DList = instance.getAppList();
        this.binList = new ArrayList<>();
    }

    void createNewBin() {
        getBin2DList().add(new Bin(nextBinIndex, cpuCapacityOfBin, memoryCapacityOfBin));
        nextBinIndex++;
    }


    void allocateBatch(List<Application> app2DList) {
        Bin currentBin = null;
        boolean allocated = false;
        sortApps(app2DList);
        int currentIndex = 0;
        while (currentIndex != app2DList.size()) {
            Application application = app2DList.get(currentIndex);
            setCurrentBinIndex(0);
            for (int j = 0; j < application.getinstanceNums(); ++j) {
                sortBins();
                allocated = false;
                while (!allocated) {
                    if (getCurrentBinIndex() >= getNextBinIndex()) {
                        createNewBin();
                        if (getBin2DList().size() > totalinstances) {
                            return;
                        }
                    }
                    currentBin = getBin2DList().get(getCurrentBinIndex());
                    if (checkItemToBin(application, currentBin)) {
                        addItemToBin(application, j, currentBin);
                        allocated = true;
                    } else {
                        currentBinIndex += 1;
                    }
                }
            }
            currentIndex++;
        }

    }

    abstract void sortBins();

    abstract void sortApps(List<Application> app2DList);

    abstract boolean checkItemToBin(Application application, Bin bin);

    abstract void addItemToBin(Application application, int instanceId, Bin bin);

    public static AlgoFit2D createAlgo2D(String algoName, Instance instance) {
        switch (algoName) {
            case "FF":
                return new Algo2DFF(instance);
            case "FFD-Degree":
                return new Algo2DFFDDegree(instance);
            case "FFD-Avg":
                return new Algo2DFFDAvg(instance);
            case "FFD-Max":
                return new Algo2DFFDMax(instance);
            case "FFD-AvgExpo":
                return new Algo2DFFDAvgExpo(instance);
            case "FFD-Surrogate":
                return new Algo2DFFDSurrogate(instance);
            case "FFD-ExtendedSum":
                return new Algo2DFFDExtendedSum(instance);
            case "BFD-Avg":
                return new Algo2DBFDAvg(instance);
            case "BFD-Max":
                return new Algo2DBFDMax(instance);
            case "BFD-AvgExpo":
                return new Algo2DBFDAvgExpo(instance);
            case "BFD-Surrogate":
                return new Algo2DBFDSurrogate(instance);
            case "BFD-ExtendedSum":
                return new Algo2DBFDExtendedSum(instance);
            case "WFD-Avg":
                return new Algo2DWFDAvg(instance);
            case "WFD-Max":
                return new Algo2DWFDMax(instance);
            case "WFD-AvgExpo":
                return new Algo2DWFDAvgExpo(instance);
            case "WFD-Surrogate":
                return new Algo2DWFDSurrogate(instance);
            case "WFD-ExtendedSum":
                return new Algo2DWFDExtendedSum(instance);
            case "NodeCount":
                return new Algo2DNodeCount(instance);
            case "NCD-DotProduct":
                return new Algo2DBinFFDDotProduct(instance);
            case "NCD-DotDivision":
                return new Algo2DBinFFDDotDivision(instance);
            case "NCD-L2Norm":
                return new Algo2DBinFFDL2Norm(instance);
            case "NCD-Fitness":
                return new Algo2DBinFFDFitness(instance);
            default:
                return null;
        }
    }

    public static Algo2DSpreadWFDAvg createSpreadAlgo(String algoName, Instance instance) {
        if (algoName.equals("SpreadWFD-Avg")) {
            return new Algo2DSpreadWFDAvg(instance);
        } else if (algoName.equals("SpreadWFD-Max")) {
            return new Algo2DSpreadWFDMax(instance);
        } else if (algoName.equals("SpreadWFD-AvgExpo")) {
            return new Algo2DSpreadWFDAvgExpo(instance);
        } else if (algoName.equals("SpreadWFD-Surrogate")) {
            return new Algo2DSpreadWFDSurrogate(instance);
        } else if (algoName.equals("SpreadWFD-ExtendedSum")) {
            return new Algo2DSpreadWFDExtendedSum(instance);
        } else if (algoName.equals("RefineWFD-Avg-5")) {
            return new Algo2DRefineWFDAvg(instance, 0.05);
        } else if (algoName.equals("RefineWFD-Avg-3")) {
            return new Algo2DRefineWFDAvg(instance, 0.03);
        } else if (algoName.equals("RefineWFD-Avg-2")) {
            return new Algo2DRefineWFDAvg(instance, 0.02);
        } else {
            return null; // This should never happen
        }
    }
}
