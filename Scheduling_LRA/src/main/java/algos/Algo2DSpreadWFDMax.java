package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DSpreadWFDMax extends Algo2DSpreadWFDAvg {
    public Algo2DSpreadWFDMax(Instance instance) {
        super(instance);
    }

    public void updateBinMeasure(Bin bin) {
        double measure = Math.max((double) bin.getAvaCpuCapacity() / bin.getMaxCpuCapacity(), (double) bin.getAvaMemCapacity() / bin.getMaxMemCapacity());
        bin.setMeasure(measure);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getMaxSize).reversed());
    }
}
