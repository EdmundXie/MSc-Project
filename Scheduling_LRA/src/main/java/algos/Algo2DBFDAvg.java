package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DBFDAvg extends AlgoFit2D {
    public Algo2DBFDAvg(Instance instance) {
        super(instance);
    }

    @Override
    void createNewBin() {

    }

    @Override
    void allocateBatch(List<Application> app2DList) {

    }

    @Override
    void sortBins() {
        Bin.bubbleBinUp(getBin2DList(), getCurrentBinIndex(), getBin2DList().size(), Comparator.comparingDouble(Bin::getMeasure));
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getAvgSize).reversed());
    }

    @Override
    boolean checkItemToBin(Application application, Bin bin) {
        return (bin.ifItemFit(application.getCpuOfinstance(), application.getMemOfinstance())) && (bin.isAffinityCompliant(application));
    }

    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
        updateBinMeasure(bin);
    }

    protected void updateBinMeasure(Bin bin) {
        double measure = ((double) bin.getAvaCpuCapacity() / bin.getMaxCpuCapacity()) + ((double) bin.getAvaMemCapacity() / bin.getMaxMemCapacity());
        bin.setMeasure(measure);
    }
}
