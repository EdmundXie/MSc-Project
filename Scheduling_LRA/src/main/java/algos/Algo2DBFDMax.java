package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DBFDMax extends Algo2DBFDAvg {
    public Algo2DBFDMax(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getMaxSize).reversed());
    }

    protected void updateBinMeasure(Bin bin) {
        double measure = Math.max((double) (bin.getAvaCpuCapacity() / bin.getMaxCpuCapacity()),
                ((double) bin.getAvaMemCapacity() / bin.getMaxMemCapacity()));
        bin.setMeasure(measure);
    }
}
