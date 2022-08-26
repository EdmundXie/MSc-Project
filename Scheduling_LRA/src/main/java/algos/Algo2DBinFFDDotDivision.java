package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.*;

public class Algo2DBinFFDDotDivision extends Algo2DBinFFDDotProduct {
    public Algo2DBinFFDDotDivision(Instance instance) {
        super(instance);
    }

    public void computeMeasures(List<Application> list, int startIndex, int endIndex, Bin bin) {
        for (int i = startIndex; i != endIndex; i++) {
            Application application = list.get(i);
            double measure = (application.getNormCpus() * bin.getMaxCpuCapacity()) / bin.getAvaCpuCapacity();
            measure += (application.getNormMem() * bin.getMaxMemCapacity()) / bin.getAvaMemCapacity();
            application.setMeasure(measure);
        }
    }
}
