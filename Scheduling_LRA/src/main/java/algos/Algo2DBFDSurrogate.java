package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DBFDSurrogate extends Algo2DBFDAvgExpo {

    public Algo2DBFDSurrogate(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getSurrogateSize).reversed());
    }

    protected void updateBinMeasure(Bin bin) {
        double lambda = ((double) getTotalResidualCpus()) / (getTotalResidualMems() + getTotalResidualCpus());
        for (int i = getCurrentBinIndex(); i < getBin2DList().size(); i++) {
            double measure = lambda * ((double) getBin2DList().get(i).getAvaCpuCapacity() / getCpuCapacityOfBin())
                    + (1 - lambda) * ((double) getBin2DList().get(i).getAvaMemCapacity() / getMemoryCapacityOfBin());
            getBin2DList().get(i).setMeasure(measure);
        }
    }
}
