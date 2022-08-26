package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DSpreadWFDAvg extends AlgoFit2D {
    public Algo2DSpreadWFDAvg(Instance instance) {
        super(instance);
    }

    @Override
    void createNewBin() {

    }

    @Override
    void allocateBatch(List<Application> app2DList) {
        return;
    }

    @Override
    void sortBins() {
        getBin2DList().sort(Comparator.comparingDouble(Bin::getMeasure).reversed());
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getAvgSize).reversed());
    }

    @Override
    boolean checkItemToBin(Application application, Bin bin) {
        return (bin.ifItemFit(application.getCpuOfinstance(), application.getMemOfinstance())) && bin.isAffinityCompliant(application);
    }

    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
    }


    public int solveInstanceSpread(int lbBins, int ubBins) {
        if (!trySolve(ubBins)) {
            return ubBins;
        }
        List<Bin> bestBins = getCopyOfBinList();
        int bestSolution = ubBins;
        int lowBound = lbBins;
        int targetBins;
        while (lowBound < bestSolution) {
            targetBins = (lowBound + bestSolution) / 2;
            if (trySolve(targetBins)) {
                bestSolution = targetBins;
                bestBins.clear();
                bestBins = getCopyOfBinList();
            } else {
                lowBound = targetBins;
            }
        }
        setSolution(bestBins);
        return bestSolution;
    }

    public boolean trySolve(int binNums) {
        clearSolutions();
        createBins(binNums);
        Bin currentBin = null;
        sortApps(getApp2DList());
        int currentIndex = 0;
        while (currentIndex != getApp2DList().size()) {
            Application application = getApp2DList().get(currentIndex);
            setCurrentBinIndex(0);
            int instanceIndex = application.getinstanceNums() - 1;
            while (instanceIndex >= 0) {
                boolean instancePacked = false;
                int startIndex = getCurrentBinIndex();
                while (!instancePacked) {
                    currentBin = getBin2DList().get(getCurrentBinIndex());
                    if (checkItemToBin(application, currentBin)) {
                        addItemToBin(application, instanceIndex, currentBin);
                        updateBinMeasure(currentBin);
                        instancePacked = true;
                        instanceIndex = -1;
                    }
                    if (getCurrentBinIndex() == binNums - 1) {
                        setCurrentBinIndex(0);
                    } else {
                        setCurrentBinIndex(getNextBinIndex() + 1);
                    }
                    if (!instancePacked && getCurrentBinIndex() == startIndex) {
                        return false;
                    }
                }
            }
            currentIndex++;
            sortBins();
        }
        return true;
    }

    public void createBins(int binNums) {
        for (int i = 0; i < binNums; i++) {
            Bin bin = new Bin(i, getCpuCapacityOfBin(), getMemoryCapacityOfBin());
            updateBinMeasure(bin);
            getBin2DList().add(bin);
        }
    }

    public void updateBinMeasure(Bin bin) {
        double measure = ((double) bin.getAvaCpuCapacity() / bin.getMaxCpuCapacity()) + ((double) bin.getAvaMemCapacity() / bin.getMaxMemCapacity());
        bin.setMeasure(measure);
    }

}
