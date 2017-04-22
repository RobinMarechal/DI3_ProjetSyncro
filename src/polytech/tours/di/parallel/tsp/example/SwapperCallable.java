package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Robin on 15/04/2017.
 */
public class SwapperCallable implements Callable<Solution>
{
    private Solution solution;
    private final long max_cpu;
    private final long startTime;
    private final long localStartTime;
    private final Instance instance;
    private long counter = 0;

    public SwapperCallable (Instance instance, Solution solution, long startTime, long localStartTime, long max_cpu)
    {
        this.solution = solution;
        this.startTime = startTime;
        this.localStartTime = localStartTime;
        this.instance = instance;
        this.max_cpu = max_cpu;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public Solution call ()
    {
        return runAlgorithm();
    }

    public long getCounter()
    {
        return counter;
    }

//    private Solution runAlgorithm ()
//    {
//        int i, j, size;
//        TSPCostCalculator costCalculator = new TSPCostCalculator(instance);
//        Solution bestSolution = solution.clone();
//        Collections.shuffle(bestSolution, ThreadLocalRandom.current());
//        bestSolution.setOF(costCalculator.calcOF(bestSolution));
//
//        size = bestSolution.size();
//
//        while (( System.currentTimeMillis() - startTime + localStartTime) / 1_000 <= max_cpu)
//        {
//            i = ThreadLocalRandom.current().nextInt(size);
//            j = ThreadLocalRandom.current().nextInt(size);
//
//            double OF;
//            Solution test = bestSolution.clone();
//
//            test.relocate(i, j);
//            OF = costCalculator.calcOF(test);
//            test.setOF(OF);
//            counter++;
//
//            if (OF < bestSolution.getOF())
//            {
//                bestSolution = test;
//            }
//        }
//
//        return bestSolution;
//    }

    private Solution runAlgorithm ()
    {
        TSPCostCalculator costCalculator = new TSPCostCalculator(instance);
        Solution bestSolution = solution.clone();
        Collections.shuffle(bestSolution, ThreadLocalRandom.current());
        bestSolution.setOF(costCalculator.calcOF(bestSolution));

        int size = bestSolution.size();
        boolean stop = false;

//        while (( System.currentTimeMillis() - startTime + localStartTime) / 1_000 <= max_cpu)
        while(!stop)
        {
            for(int i = 0; i < size && !stop; i++)
            {
                for(int j = 0; j < size && !stop; j++)
                {
                    double OF;
                    Solution test = bestSolution.clone();

                    test.relocate(i, j);
                    OF = costCalculator.calcOF(test);
                    test.setOF(OF);
                    counter++;

                    if (OF < bestSolution.getOF())
                    {
                        bestSolution = test;
                    }

                    if(( System.currentTimeMillis() - startTime + localStartTime) / 1_000 >= max_cpu)
                        stop = true;
                }
            }

        }

        return bestSolution;
    }


}
