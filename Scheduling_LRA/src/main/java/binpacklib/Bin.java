package binpacklib;

import com.google.common.collect.Lists;
import util.AllocMap;
import util.ConflictMap;

import java.util.*;

public class Bin {
    private int id;
    private int maxCpuCapacity;
    private int maxMemCapacity;
    private int avaCpuCapacity;
    private int avaMemCapacity;
    private AllocMap allocMap = new AllocMap();
    private ConflictMap conflictMap = new ConflictMap();
    double measure;

    public Bin(int id, int maxCpuCapacity, int maxMemCapacity) {
        this.id = id;
        this.maxCpuCapacity = maxCpuCapacity;
        this.maxMemCapacity = maxMemCapacity;
        this.avaCpuCapacity = maxCpuCapacity;
        this.avaMemCapacity = maxMemCapacity;
    }

    public void addNewItem(Application application, int instanceId) {
        if (ifItemFit(application.getCpuOfinstance(), application.getMemOfinstance())) {
            if (allocMap.containsKey(application.getId())) {
                allocMap.get(application.getId()).add(instanceId);
            } else {
                List<Integer> list = Lists.newArrayList();
                list.add(instanceId);
                allocMap.put(application.getId(), list);
            }
            avaCpuCapacity -= application.getCpuOfinstance();
            avaMemCapacity -= application.getMemOfinstance();
        }
    }

    public boolean ifItemFit(int cpuSize, int memSize) {
        return cpuSize <= avaCpuCapacity && memSize <= avaMemCapacity;
    }

    public void addNewConflict(Application application) {
        if (allocMap.containsKey(application.getId())) {
            return;
        }
        for (Map.Entry<String, Integer> entry : application.getOutMap().entrySet()) {
            if (conflictMap.containsKey(entry.getKey())) {
                conflictMap.put(entry.getKey(), Math.min(entry.getValue(), conflictMap.get(entry.getKey())));
            } else {
                conflictMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean isAffinityCompliant(Application application) {
        if (conflictMap.containsKey(application.getId())) {
            if (conflictMap.get(application.getId()) < 1) {
                return false;
            }
            if (allocMap.containsKey(application.getId())) {
                if (conflictMap.get(application.getId()) < allocMap.get(application.getId()).size() + 1) {
                    return false;
                }
            }
        }
        for (Map.Entry<String, Integer> entry : application.getOutMap().entrySet()) {
            if (allocMap.containsKey(entry.getKey())) {
                if (allocMap.get(entry.getKey()).size() > entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void bubbleBinUp(List<Bin> binList, int fromIndex, int toIndex, Comparator<Bin> comparator) {
        if (fromIndex == toIndex) {
            return;
        }
        --toIndex;
        if (fromIndex == toIndex) {
            return;
        }
        int current = toIndex;
        int previous = toIndex - 1;
        while (fromIndex != previous) {
            if (comparator.compare(binList.get(current), binList.get(previous)) > 0) {
                Collections.swap(binList, current, previous);
            }
            --current;
            --previous;
        }
        if (comparator.compare(binList.get(current), binList.get(previous)) > 0) {
            Collections.swap(binList, current, previous);
        }
    }

    public static void bubbleBinDown(List<Bin> binList, int fromIndex, int toIndex, Comparator<Bin> comparator) {
        if (fromIndex == toIndex) {
            return;
        }
        int next = fromIndex;
        next++;
        if (next == toIndex) {
            return;
        }
        int current = fromIndex;
        while (next != toIndex) {
            if (comparator.compare(binList.get(next), binList.get(current)) > 0) {
                Collections.swap(binList, current, next);
            }
            ++current;
            ++next;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxCpuCapacity() {
        return maxCpuCapacity;
    }

    public void setMaxCpuCapacity(int maxCpuCapacity) {
        this.maxCpuCapacity = maxCpuCapacity;
    }

    public int getMaxMemCapacity() {
        return maxMemCapacity;
    }

    public void setMaxMemCapacity(int maxMemCapacity) {
        this.maxMemCapacity = maxMemCapacity;
    }

    public int getAvaCpuCapacity() {
        return avaCpuCapacity;
    }

    public void setAvaCpuCapacity(int avaCpuCapacity) {
        this.avaCpuCapacity = avaCpuCapacity;
    }

    public int getAvaMemCapacity() {
        return avaMemCapacity;
    }

    public void setAvaMemCapacity(int avaMemCapacity) {
        this.avaMemCapacity = avaMemCapacity;
    }

    public AllocMap getAllocMap() {
        return allocMap;
    }

    public void setAllocMap(AllocMap allocMap) {
        this.allocMap = allocMap;
    }

    public ConflictMap getConflictMap() {
        return conflictMap;
    }

    public void setConflictMap(ConflictMap conflictMap) {
        this.conflictMap = conflictMap;
    }

    public double getMeasure() {
        return measure;
    }

    public void setMeasure(double measure) {
        this.measure = measure;
    }
}
