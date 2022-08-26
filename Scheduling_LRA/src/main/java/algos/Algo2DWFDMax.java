package algos;

import binpacklib.Bin;
import binpacklib.Instance;

import java.util.Comparator;

public class Algo2DWFDMax extends Algo2DBFDMax {
    public Algo2DWFDMax(Instance instance) {
        super(instance);
    }

    @Override
    void sortBins() {
        Bin.bubbleBinDown(getBin2DList(), getCurrentBinIndex(), getBin2DList().size(), Comparator.comparingDouble(Bin::getMeasure).reversed());
    }
}
