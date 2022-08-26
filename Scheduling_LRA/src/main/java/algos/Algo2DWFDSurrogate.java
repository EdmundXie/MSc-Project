package algos;

import binpacklib.Bin;
import binpacklib.Instance;
import util.CommonUtil;

import java.util.Comparator;

public class Algo2DWFDSurrogate extends Algo2DBFDSurrogate {
    public Algo2DWFDSurrogate(Instance instance) {
        super(instance);
    }

    @Override
    void sortBins() {
        setBin2DList(CommonUtil.partSort(getBin2DList(), getCurrentBinIndex(), Comparator.comparingDouble(Bin::getMeasure).reversed()));
    }
}
