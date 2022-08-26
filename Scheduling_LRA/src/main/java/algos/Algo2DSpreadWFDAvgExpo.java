package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DSpreadWFDAvgExpo extends Algo2DSpreadWFDAvg {
    private int totalResidualCpu;
    private int totalResidualMem;

    public int getTotalResidualCpu() {
        return totalResidualCpu;
    }

    public void setTotalResidualCpu(int totalResidualCpu) {
        this.totalResidualCpu = totalResidualCpu;
    }

    public int getTotalResidualMem() {
        return totalResidualMem;
    }

    public void setTotalResidualMem(int totalResidualMem) {
        this.totalResidualMem = totalResidualMem;
    }

    public Algo2DSpreadWFDAvgExpo(Instance instance) {
        super(instance);
    }

    public void updateBinMeasure(Bin bin) {
        double factorCpu = Math.exp(0.01 * totalResidualCpu / (getCpuCapacityOfBin() * getBin2DList().size())) / getCpuCapacityOfBin();
        double factorMem = Math.exp(0.01 * totalResidualMem / (getMemoryCapacityOfBin() * getBin2DList().size())) / getMemoryCapacityOfBin();
        for (int i = 0; i < getBin2DList().size(); i++) {
            double measure = factorCpu * (getBin2DList().get(i).getAvaCpuCapacity()) + factorMem * (getBin2DList().get(i).getAvaMemCapacity());
            getBin2DList().get(i).setMeasure(measure);
        }
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getAvgExpoSize).reversed());
    }

    @Override
    public void createBins(int binNums) {
        for (int i = 0; i < binNums; i++) {
            Bin bin = new Bin(i, getCpuCapacityOfBin(), getMemoryCapacityOfBin());
            getBin2DList().add(bin);
        }
        totalResidualCpu = binNums * getCpuCapacityOfBin();
        totalResidualMem = binNums * getMemoryCapacityOfBin();
    }

    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
        totalResidualCpu -= application.getCpuOfinstance();
        totalResidualMem -= application.getMemOfinstance();
    }

}
