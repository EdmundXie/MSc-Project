package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.List;

public class Algo2DFF extends AlgoFit2D {
    public Algo2DFF(Instance instance) {
        super(instance);
    }

    @Override
    void sortBins() {

    }

    @Override
    void sortApps(List<Application> app2DList) {

    }


    @Override
    boolean checkItemToBin(Application application, Bin bin) {
        return bin.ifItemFit(application.getCpuOfinstance(), application.getMemOfinstance())
                && bin.isAffinityCompliant(application);
    }


    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
    }
}
