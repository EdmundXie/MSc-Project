package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DSpreadWFDSurrogate extends Algo2DSpreadWFDAvgExpo {
    public Algo2DSpreadWFDSurrogate(Instance instance) {
        super(instance);
    }

    public void updateBinMeasure(Bin bin) {
        double lambda = ((double) getTotalResidualCpu()) / (getTotalResidualCpu() + getTotalResidualMem());
        for (int i = 0; i < getBin2DList().size(); i++) {
            double measure = lambda * ((double) getBin2DList().get(i).getAvaCpuCapacity() / getCpuCapacityOfBin())
                    + (1 - lambda) * ((double) getBin2DList().get(i).getAvaMemCapacity() / getMemoryCapacityOfBin());
            getBin2DList().get(i).setMeasure(measure);
        }
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getMaxSize).reversed());
    }


}
