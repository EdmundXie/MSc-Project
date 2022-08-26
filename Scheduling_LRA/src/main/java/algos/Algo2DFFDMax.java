package algos;

import binpacklib.Application;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DFFDMax extends Algo2DFF {
    public Algo2DFFDMax(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getMaxSize).reversed());
    }
}
