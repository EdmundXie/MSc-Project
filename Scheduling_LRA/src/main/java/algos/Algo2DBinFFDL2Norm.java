package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.List;

public class Algo2DBinFFDL2Norm extends Algo2DBinFFDDotProduct {
    public Algo2DBinFFDL2Norm(Instance instance) {
        super(instance);
    }

    public void computeMeasures(List<Application> list, int startIndex, int endIndex, Bin bin) {
        for (int i = startIndex; i != endIndex; i++) {
            Application application = list.get(i);
            double a = ((double) bin.getAvaCpuCapacity() / bin.getMaxCpuCapacity()) - application.getNormCpus();
            double b = ((double) bin.getAvaMemCapacity() / bin.getMaxMemCapacity()) - application.getNormMem();
            double measure = a * a + b * b;
            application.setMeasure(-measure);
        }
    }
}
