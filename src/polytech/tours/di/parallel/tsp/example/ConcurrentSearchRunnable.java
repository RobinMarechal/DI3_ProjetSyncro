package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Robin on 05/04/2017.
 */
public class ConcurrentSearchRunnable implements Algorithm
{

    // Constants
    private final int NB_THREADS = 64;

    private long counter = 0;
    private long startTime;
    private Random rnd;

    @Override
    public Solution run (Properties config)
    {
        //read instance
        InstanceReader ir = new InstanceReader();
        ir.buildInstance(config.getProperty("instance"));

        //get the instance
        Instance instance = ir.getInstance();

        //print some distances
//        System.out.println("d(1,2)=" + instance.getDistance(1, 2));
//        System.out.println("d(10,19)=" + instance.getDistance(10, 19));

        //read maximum CPU time
        long max_cpu = Long.valueOf(config.getProperty("maxcpu"));

        //build a random solution
        rnd = new Random(Long.valueOf(config.getProperty("seed")));
        Solution s = new Solution();

        startTime = System.currentTimeMillis();
        for (int i = 0; i < instance.getN(); i++)
        {
            s.add(i);
        }

        TSPCostCalculator tsp = new TSPCostCalculator(instance);
        s.setOF(tsp.calcOF(s));
        Solution best = execute(s, instance, max_cpu);

        return best;
    }


    private Solution execute (Solution solution, Instance instance, long max_cpu)
    {
        Solution bestSolution = solution.clone();

        CopyOnWriteArrayList<SwapperRunnable> swappers = new CopyOnWriteArrayList<>();
        ArrayList<Solution> solutions = new ArrayList<>();

        for (int i = 0; i < NB_THREADS; i++)
        {
            Collections.shuffle(bestSolution, rnd);
            SwapperRunnable swapper = new SwapperRunnable(instance, bestSolution, startTime, System.currentTimeMillis() - startTime + 1_000, max_cpu);
            (new Thread(swapper)).start();
            swappers.add(swapper);
        }

        for (SwapperRunnable swapper : swappers)
        {
            while (!swapper.isDone())
            {
                // We pause this thread to allow the others to run
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    return bestSolution;
                }
            }

            // We add this swapper's solution to the list
            solutions.add(swapper.getSolution());
            counter += swapper.getCounter();
        }

        if (bestSolution == null && !solutions.isEmpty())
        {
            bestSolution = solutions.get(0);
        }

        // We look for the best 'best solution'
        for (Solution sol : solutions)
        {
            if (sol.getOF() < bestSolution.getOF())
            {
                bestSolution = sol;
            }
        }

        System.out.println("Computations : " + counter);
        System.out.println("Time : " + (System.currentTimeMillis() - startTime) + "ms");

        return bestSolution;
    }

}

