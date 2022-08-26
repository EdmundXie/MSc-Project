package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DBFDExtendedSum extends Algo2DBFDAvgExpo {

    public Algo2DBFDExtendedSum(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getExtSize).reversed());
    }

    protected void updateBinMeasure(Bin bin) {
        for (int i = getCurrentBinIndex(); i < getBin2DList().size(); i++) {
            double measure = (double) getBin2DList().get(i).getAvaCpuCapacity() / getTotalResidualCpus() + (double) getBin2DList().get(i).getAvaMemCapacity() / getTotalResidualMems();
            getBin2DList().get(i).setMeasure(measure);
        }
    }
}
