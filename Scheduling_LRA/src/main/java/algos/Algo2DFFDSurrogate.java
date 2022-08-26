package algos;

import binpacklib.Application;
import binpacklib.Instance;

import java.util.Comparator;
import java.util.List;

public class Algo2DFFDSurrogate extends Algo2DFF {
    public Algo2DFFDSurrogate(Instance instance) {
        super(instance);
    }

    @Override
    void sortApps(List<Application> app2DList) {
        app2DList.sort(Comparator.comparingDouble(Application::getSurrogateSize).reversed());
    }
}
