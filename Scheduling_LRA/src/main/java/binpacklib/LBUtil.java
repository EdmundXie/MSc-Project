package binpacklib;

import java.util.HashSet;
import java.util.Set;

public class LBUtil {
    private static int bpp2DLbalphaMem(Instance instance, int alpha) {
        if (alpha > (instance.getMemoryCapacityOfBin() / 2.0)) {
            return 0;
        }
        double thresholdOne = instance.getMemoryCapacityOfBin() - alpha;
        double thresholdTwo = instance.getMemoryCapacityOfBin() / 2.0;
        int n1 = 0;
        int n2 = 0;
        double j2 = 0;
        double j3 = 0;
        for (Application application : instance.getAppList()) {
            if (application.getMemOfinstance() > thresholdOne) {
                n1 += application.getinstanceNums();
            } else if (application.getMemOfinstance() > thresholdTwo) {
                n2 += application.getinstanceNums();
                j2 += application.getMemOfinstance() * application.getinstanceNums();
            } else if (application.getMemOfinstance() >= alpha) {
                j3 += application.getMemOfinstance() * application.getinstanceNums();
            }
        }
        int temp = (int) Math.ceil((j3 + j2 - (n2 * instance.getMemoryCapacityOfBin())) / instance.getMemoryCapacityOfBin());
        return n1 + n2 + Math.max(0, temp);
    }

    private static int bpp2DLbalphaCpu(Instance instance, int alpha) {
        if (alpha > (instance.getCpuCapacityOfBin() / 2.0)) {
            return 0;
        }
        double thresholdOne = instance.getCpuCapacityOfBin() - alpha;
        double thresholdTwo = instance.getCpuCapacityOfBin() / 2.0;
        int n1 = 0;
        int n2 = 0;
        double j2 = 0;
        double j3 = 0;
        for (Application application : instance.getAppList()) {
            if (application.getCpuOfinstance() > thresholdOne) {
                n1 += application.getinstanceNums();
            } else if (application.getCpuOfinstance() > thresholdTwo) {
                n2 += application.getinstanceNums();
                j2 += application.getCpuOfinstance() * application.getinstanceNums();
            } else if (application.getCpuOfinstance() >= alpha) {
                j3 += application.getCpuOfinstance() * application.getinstanceNums();
            }
        }
        int temp = (int) Math.ceil((j3 + j2 - (n2 * instance.getCpuCapacityOfBin())) / instance.getCpuCapacityOfBin());
        return n1 + n2 + Math.max(0, temp);
    }

    private static int bpp2DLbCpu(Instance instance) {
        int lbCpu = 0;
        Set<Integer> alphaSettedCpu = new HashSet<>();
        for (Application application : instance.getAppList()) {
            alphaSettedCpu.add(application.getCpuOfinstance());
        }
        for (int alpha : alphaSettedCpu) {
            int lbAlpha = bpp2DLbalphaCpu(instance, alpha);
            lbCpu = Math.max(lbAlpha, lbCpu);
        }
        return lbCpu;
    }

    private static int bpp2DLbMem(Instance instance) {
        int lbMem = 0;
        Set<Integer> alphaSettedMem = new HashSet<>();
        for (Application application : instance.getAppList()) {
            alphaSettedMem.add(application.getMemOfinstance());
        }
        for (int alpha : alphaSettedMem) {
            int lbAlpha = bpp2DLbalphaMem(instance, alpha);
            lbMem = Math.max(lbAlpha, lbMem);
        }
        return lbMem;
    }

    public static int bpp2DLb(Instance instance) {
        int lbCpu = bpp2DLbCpu(instance);
        int lbMem = bpp2DLbMem(instance);
        return Math.max(lbCpu, lbMem);
    }
}
