package algos;

import binpacklib.Bin;
import binpacklib.Instance;
import util.CommonUtil;

import java.util.Comparator;

public class Algo2DWFDExtendedSum extends Algo2DBFDExtendedSum {
    public Algo2DWFDExtendedSum(Instance instance) {
        super(instance);
    }

    @Override
    void sortBins() {
        setBin2DList(CommonUtil.partSort(getBin2DList(), getCurrentBinIndex(), Comparator.comparingDouble(Bin::getMeasure).reversed()));
    }
}
