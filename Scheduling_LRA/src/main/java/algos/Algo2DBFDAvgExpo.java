package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;
import util.CommonUtil;

import java.util.Comparator;
import java.util.List;

public class Algo2DBFDAvgExpo extends Algo2DBFDAvg {
    private int totalResidualCpus;
    private int totalResidualMems;

    public int getTotalResidualCpus() {
        return totalResidualCpus;
    }

    public void setTotalResidualCpus(int totalResidualCpus) {
        this.totalResidualCpus = totalResidualCpus;
    }

    public int getTotalResidualMems() {
        return totalResidualMems;
    }

    public void setTotalResidualMems(int totalResidualMems) {
        this.totalResidualMems = totalResidualMems;
    }

    public Algo2DBFDAvgExpo(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getAvgExpoSize).reversed());
    }

    protected void updateBinMeasure(Bin bin) {
        double factorCpu = Math.exp(0.01 * totalResidualCpus / (getCpuCapacityOfBin() * getBin2DList().size())) / getCpuCapacityOfBin();
        double factorMem = Math.exp(0.01 * totalResidualMems / (getMemoryCapacityOfBin() * getBin2DList().size())) / getMemoryCapacityOfBin();
        for (int i = getCurrentBinIndex(); i < getBin2DList().size(); i++) {
            double measure = factorCpu * (getBin2DList().get(i).getAvaCpuCapacity()) + factorMem * (getBin2DList().get(i).getAvaMemCapacity());
            getBin2DList().get(i).setMeasure(measure);
        }
    }

    @Override
    void createNewBin() {
        getBin2DList().add(new Bin(getNextBinIndex(), getCpuCapacityOfBin(), getMemoryCapacityOfBin()));
        setNextBinIndex(getNextBinIndex() + 1);
        totalResidualCpus += getCpuCapacityOfBin();
        totalResidualMems += getMemoryCapacityOfBin();
    }

    @Override
    void allocateBatch(List<Application> app2DList) {

    }

    @Override
    void sortBins() {
        setBin2DList(CommonUtil.partSort(getBin2DList(), getCurrentBinIndex(), Comparator.comparingDouble(Bin::getMeasure)));
    }

    @Override
    boolean checkItemToBin(Application application, Bin bin) {
        return (bin.ifItemFit(application.getCpuOfinstance(), application.getMemOfinstance())) && (bin.isAffinityCompliant(application));
    }

    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
        totalResidualCpus -= application.getCpuOfinstance();
        totalResidualMems -= application.getMemOfinstance();
        updateBinMeasure(bin);
    }
}
