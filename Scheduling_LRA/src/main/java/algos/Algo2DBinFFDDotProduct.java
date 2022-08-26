package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;

import java.util.*;

public class Algo2DBinFFDDotProduct extends Algo2DFF {
    public Algo2DBinFFDDotProduct(Instance instance) {
        super(instance);
    }

    protected boolean isBinFilled(Bin bin) {
        return bin.getAvaCpuCapacity() == 0 || bin.getAvaMemCapacity() == 0;
    }

    public void computeMeasures(List<Application> list, int startIndex, int endIndex, Bin bin) {
        for (int i = startIndex; i != endIndex; i++) {
            Application application = list.get(i);
            double measure = (application.getNormCpus() * bin.getAvaCpuCapacity()) / bin.getMaxCpuCapacity();
            measure += (application.getNormMem() * bin.getAvaMemCapacity()) / bin.getMaxMemCapacity();
            application.setMeasure(measure);
        }
    }

    public Bin createNewBinRet() {
        Bin bin = new Bin(getNextBinIndex(), getCpuCapacityOfBin(), getMemoryCapacityOfBin());
        setNextBinIndex(getNextBinIndex() + 1);
        getBin2DList().add(bin);
        return bin;
    }

    @Override
    void createNewBin() {

    }

    @Override
    void allocateBatch(List<Application> app2DList) {
        Map<String, Integer> nextIdinstances = new HashMap<>(app2DList.size());
        for (Application d : app2DList) {
            nextIdinstances.put(d.getId(), 0);
        }
        Bin currentBin;
        int nextIndex = 0;
        int endIndex = app2DList.size();
        while (nextIndex != endIndex) {
            currentBin = createNewBinRet();
            int currentIndex = nextIndex;
            if (getBin2DList().size() > getTotalinstances()) {
                return;
            }
            boolean isContinueLoop = true;
            while (isContinueLoop && currentIndex != endIndex) {
                computeMeasures(app2DList, currentIndex, endIndex, currentBin);
                Application.bubbleAppUp(app2DList, currentIndex, endIndex, Comparator.comparingDouble(Application::getMeasure).reversed());
                Application application = app2DList.get(currentIndex);
                int instanceId = nextIdinstances.get(application.getId());
                boolean couldPack = true;
                while (instanceId < application.getinstanceNums() && couldPack) {
                    if (checkItemToBin(application, currentBin)) {
                        addItemToBin(application, instanceId, currentBin);
                        instanceId += 1;
                    } else {
                        couldPack = false;
                    }
                }
                nextIdinstances.put(application.getId(), instanceId);
                if (instanceId >= application.getinstanceNums()) {
                    Collections.swap(app2DList, nextIndex, currentIndex);
                    nextIndex++;
                }
                currentIndex++;
                if (isBinFilled(currentBin)) {
                    isContinueLoop = false;
                }
                System.out.println(currentIndex + "  " + nextIndex);
            }
        }
    }

    @Override
    void sortBins() {

    }

    @Override
    void sortApps(List<Application> app2DList) {

    }

    @Override
    boolean checkItemToBin(Application application, Bin bin) {
        return (bin.ifItemFit(application.getCpuOfinstance(), application.getMemOfinstance())) && (bin.isAffinityCompliant(application));
    }

    @Override
    void addItemToBin(Application application, int instanceId, Bin bin) {
        bin.addNewConflict(application);
        bin.addNewItem(application, instanceId);
    }
}
