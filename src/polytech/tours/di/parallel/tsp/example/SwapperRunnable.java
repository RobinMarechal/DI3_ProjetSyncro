package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Robin on 15/04/2017.
 */
public class SwapperRunnable implements Runnable
{
    private Solution solution;
    private boolean stop;
    private final long max_cpu;
    private final long startTime;
    private final long localStartTime;
    private final Instance instance;
    private long counter = 0;

    public SwapperRunnable (Instance instance, Solution solution, long startTime, long localStartTime, long max_cpu)
    {
        this.solution = solution;
        this.startTime = startTime;
        this.localStartTime = localStartTime;
        this.instance = instance;
        this.max_cpu = max_cpu;
        stop = false;
    }

    public Solution getSolution ()
    {
        return solution;
    }

    public boolean isDone ()
    {
        return stop;
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
    public void run ()
    {
        solution = runAlgorithm();
        stop = true;
    }

    public long getCounter()
    {
        return counter;
    }


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
