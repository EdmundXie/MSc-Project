package algos;

import binpacklib.Application;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DFFDDegree extends Algo2DFF {
    public Algo2DFFDDegree(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingInt(Application::getTotalDegree).reversed());
    }
}
