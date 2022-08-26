package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.List;

public class Algo2DBinFFDFitness extends Algo2DBinFFDDotProduct {
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

    public Algo2DBinFFDFitness(Instance instance) {
        super(instance);
    }

    @Override
    public Bin createNewBinRet() {
        Bin bin = new Bin(getNextBinIndex(), getCpuCapacityOfBin(), getMemoryCapacityOfBin());
        setNextBinIndex(getNextBinIndex() + 1);
        getBin2DList().add(bin);
        totalResidualCpu += getCpuCapacityOfBin();
        totalResidualMem += getMemoryCapacityOfBin();
        return bin;
    }

    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
        totalResidualCpu -= application.getCpuOfinstance();
        totalResidualMem -= application.getMemOfinstance();
    }

    public void computeMeasures(List<Application> list, int startIndex, int endIndex, Bin bin) {
        for (int i = startIndex; i != endIndex; i++) {
            Application application = list.get(i);
            double a = ((double) application.getNormCpus() * bin.getAvaCpuCapacity()) / (getNormCpus() * totalResidualCpu);
            double b = ((double) application.getNormMem() * bin.getMaxMemCapacity()) - application.getNormMem();
            application.setMeasure(a + b);
        }
    }
}
