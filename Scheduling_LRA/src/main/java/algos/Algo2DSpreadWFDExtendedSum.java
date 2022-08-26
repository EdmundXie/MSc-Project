package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DSpreadWFDExtendedSum extends Algo2DSpreadWFDAvgExpo {
    public Algo2DSpreadWFDExtendedSum(Instance instance) {
        super(instance);
    }

    public void updateBinMeasure(Bin bin) {
        for (int i = 0; i < getBin2DList().size(); i++) {
            double measure = (double) getBin2DList().get(i).getAvaCpuCapacity() / getTotalResidualCpu() + (double) getBin2DList().get(i).getAvaMemCapacity() / getTotalResidualMem();
            getBin2DList().get(i).setMeasure(measure);
        }
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getMaxSize).reversed());
    }


}
