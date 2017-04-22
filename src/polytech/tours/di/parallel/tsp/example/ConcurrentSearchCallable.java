package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Robin on 05/04/2017.
 */
public class ConcurrentSearchCallable implements Algorithm
{

    private int nbThreads = 4;
    private long counter;
    private long startTime;

    /**
     *
     * @param config the algorithm's configuration
     * @return the best found solution
     */
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

        Solution s = new Solution();

        for (int i = 0; i < instance.getN(); i++)
        {
            s.add(i);
        }

        TSPCostCalculator tsp = new TSPCostCalculator(instance);
        s.setOF(tsp.calcOF(s));
        counter = 0;

        Solution best = execute(s, instance, max_cpu);

        return best;
    }

    /**
     * Sets the number of threads that will be created
     * @param nbThreads the number of threads that will be created
     */
    public void setNbThreads (int nbThreads)
    {
        this.nbThreads = nbThreads;
    }

    /**
     * Returns the total number of the parallel algorithm's computations
     *
     * @return the number of computations
     */
    public long getCounter ()
    {
        return counter;
    }

    /**
     * Runs a parallel algorithm to find the best solution of the TSP
     *
     * @param solution one random solution
     * @param instance the instance containing distances matrix
     * @param max_cpu  the maximal runtime
     * @return the best found solution
     */
    private Solution execute (Solution solution, Instance instance, long max_cpu)
    {
        Solution bestSolution = solution.clone();

        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
        List<Callable<Solution>> tasks = new ArrayList<>();
        List<Future<Solution>> solutions;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < nbThreads; i++)
        {
            Callable<Solution> swapper = new SwapperCallable(instance, solution, startTime, System.currentTimeMillis() - startTime, max_cpu);
            tasks.add(swapper);
        }

        try
        {
            solutions = executor.invokeAll(tasks);
            executor.shutdown();

            for (Callable<Solution> t : tasks)
            {
                counter += ((SwapperCallable) t).getCounter();
            }
        }
        catch (InterruptedException e)
        {
            return bestSolution;
        }

        try
        {
            for (Future<Solution> s : solutions)
            {
                Solution tmp = s.get();

                if (bestSolution.getOF() > tmp.getOF())
                {
                    bestSolution = tmp;
                }
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            return bestSolution;
        }

        System.out.println("Threads: " + nbThreads);
        System.out.println("Computations: " + counter);
        System.out.println("Time: " + (System.currentTimeMillis() - startTime) + "ms");


        return bestSolution;
    }

}

