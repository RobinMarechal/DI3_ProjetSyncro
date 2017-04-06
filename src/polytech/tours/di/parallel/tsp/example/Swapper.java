package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Robin on 05/04/2017.
 */
public class Swapper implements Callable<Solution>{

    private Solution solution = null;
    private Solution bestSolution = null;
    private Instance instance = null;
    private long max_cpu = 0;
    private long nbIterations = 0;
    private long runs;
    private long timeLeft;

//    public Swapper(Solution solution, Instance instance, long max_cpu) {
//        this.solution = solution;
//        this.instance = instance;
//        this.max_cpu = max_cpu;
//    }

    public Swapper(long timeLeft)
    {
        this.timeLeft = timeLeft;
    }

    public Solution getSolution()
    {
        return this.solution;
    }

    public long getNbIterations()
    {
        return nbIterations;
    }

    public Solution getBestSolution()
    {
        return bestSolution;
    }

    public Solution localSearch()
    {
        int i, j, size;
        Solution solToSwap;
        Solution bestSol = solution.clone();
        double matrix[][] = instance.getDistanceMatrix();
        long startTime=System.currentTimeMillis();
        size = solution.size();
        TSPCostCalculator costCalc = new TSPCostCalculator();

        bestSol.setOF(costCalc.calcOF(matrix, bestSol));

        while((System.currentTimeMillis()-startTime)/1_000 <= timeLeft){
            i = ThreadLocalRandom.current().nextInt(size);
            j = ThreadLocalRandom.current().nextInt(size);

            if(i == j) continue;

            solToSwap = bestSol.clone();
            solToSwap.relocate(i, j);
            //set the objective function of the solution
            double OF = costCalc.calcOF(matrix, solToSwap);
            solToSwap.setOF(OF);
            if(OF < bestSol.getOF())
            {
                System.out.println(OF);
                bestSol = solToSwap;
            }
            nbIterations++;
        }

        return bestSol;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Solution call() throws Exception {
        return localSearch();
    }
}
