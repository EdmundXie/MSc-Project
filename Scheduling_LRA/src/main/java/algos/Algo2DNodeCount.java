package algos;

import binpacklib.Application;
import binpacklib.Bin;
import binpacklib.Instance;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Algo2DNodeCount extends AlgoFit2D {
    public Algo2DNodeCount(Instance instance) {
        super(instance);
    }

    @Override
    void createNewBin() {

    }

    @Override
    void allocateBatch(List<Application> app2DList) {
        Bin currentBin = null;
        boolean isAllocated = false;
        app2DList.sort(Comparator.comparingInt(Application::getTotalDegree).reversed());
        Map<String, List<Integer>> binCandidates = Maps.newHashMap();
        for (Application application : getApp2DList()) {
            List<Integer> list = new ArrayList<>();
            for (Bin bin : getBin2DList()) {
                if (checkItemToBin(application, bin)) {
                    list.add(bin.getId());
                }
            }
            binCandidates.put(application.getId(), list);
        }

        int currentIndex = 0;
        String currentAppId;
        int endIndex = app2DList.size();
        while (currentIndex != endIndex) {
            currentAppId = app2DList.get(currentIndex).getId();
            List<Bin> binIsSet = Lists.newArrayList();
            int nextIndex = currentIndex + 1;
            int binIndex = 0;
            for (int j = 0; j < app2DList.get(currentIndex).getinstanceNums(); j++) {
                // 先用候选bin试试
                isAllocated = false;
                while ((!isAllocated) && binIndex != binCandidates.get(currentAppId).size()) {
                    currentBin = getBin2DList().get(binIndex);
                    if (checkItemToBin(app2DList.get(currentIndex), currentBin)) {
                        addItemToBin(app2DList.get(currentIndex), j, currentBin);
                        isAllocated = true;
                        if (!binIsSet.contains(currentBin)) {
                            binIsSet.add(currentBin);
                        }
                    } else {
                        binIndex++;
                    }
                }
                // 副本被打包了或者候选bin分配完了
                if (!isAllocated) {
                    currentBin = new Bin(getNextBinIndex(), getCpuCapacityOfBin(), getMemoryCapacityOfBin());
                    getBin2DList().add(currentBin);
                    // 防止内存溢出
                    if (getBin2DList().size() > getTotalinstances()) {
                        return;
                    }
                    // 将这个新建的bin加入到所有剩余app的候选bin中
                    for (int i = nextIndex; i != endIndex; ++i) {
                        binCandidates.get(app2DList.get(i).getId()).add(getNextBinIndex());
                        app2DList.get(i).setMeasure(binCandidates.get(app2DList.get(i).getId()).size());
                    }
                    binCandidates.get(currentAppId).add(getNextBinIndex());
                    setNextBinIndex(getNextBinIndex() + 1);
                    binIndex = binCandidates.get(currentAppId).size() - 1;
                    addItemToBin(app2DList.get(currentIndex), j, currentBin);
                    binIsSet.add(currentBin);
                }
            }
            // 所有副本都已打包
            app2DList.get(currentIndex).setFullyPacked(true);
            for (Map.Entry<String, Integer> entry : app2DList.get(currentIndex).getInMap().entrySet()) {
                Application application = Application.getApp2D(getApp2DList(), entry.getKey());
                if (!application.isFullyPacked()) {
                    for (Bin bin : binIsSet) {
                        List<Integer> binVect = binCandidates.get(entry.getKey());
                        if (!checkItemToBin(application, bin)) {
                            if (binVect.contains(bin.getId())) {
                                binVect.remove(bin.getId());
                                application.setMeasure(binVect.size());
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : app2DList.get(currentIndex).getOutMap().entrySet()) {
                Application application = Application.getApp2D(getApp2DList(), entry.getKey());
                if (!application.isFullyPacked()) {
                    for (Bin bin : binIsSet) {
                        List<Integer> binVect = binCandidates.get(entry.getKey());
                        if (!checkItemToBin(application, bin)) {
                            if (binVect.contains(bin.getId())) {
                                binVect.remove(bin.getId());
                                application.setMeasure(binVect.size());
                            }
                        }
                    }
                }
            }
            Application.bubbleAppUp(app2DList, nextIndex, endIndex, Comparator.comparingDouble(Application::getMeasure));
            currentIndex = nextIndex;
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
