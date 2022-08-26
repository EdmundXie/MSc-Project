package algos;

import binpacklib.Bin;
import binpacklib.Instance;

import java.util.List;

public class Algo2DRefineWFDAvg extends Algo2DSpreadWFDAvgExpo {
    private double ratioRefinement;

    public double getRatioRefinement() {
        return ratioRefinement;
    }

    public void setRatioRefinement(double ratioRefinement) {
        this.ratioRefinement = ratioRefinement;
    }

    public Algo2DRefineWFDAvg(Instance instance, double ratioRefinement) {
        super(instance);
        this.ratioRefinement = ratioRefinement;
    }

    @Override
    public int solveInstanceSpread(int lbBins, int ubBins) {
        int refineStep = (int) Math.ceil(lbBins * ratioRefinement);
        int bestSolution = -1;
        if (refineStep < 1) {
            refineStep = 1;
        }
        if (!trySolve(ubBins)) {
            return ubBins;
        } else {
            List<Bin> best2DList = getCopyOfBinList();
            bestSolution = ubBins;
            int targetBins;
            while (lbBins < bestSolution) {
                targetBins = bestSolution - refineStep;
                if (trySolve(targetBins)) {
                    bestSolution = targetBins;
                    best2DList.clear();
                    best2DList = getCopyOfBinList();
                } else {
                    break;
                }
            }
            setSolution(best2DList);
        }
        return bestSolution;
    }
}
