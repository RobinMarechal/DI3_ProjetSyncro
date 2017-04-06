package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Robin on 06/04/2017.
 */
public class TSPCalculator {

    private Solution solution;
    private long max_cpu;
    private Instance instance;

    public TSPCalculator(Solution solution, Instance instance, long max_cpu) {
        this.solution = solution;
        this.max_cpu = max_cpu;
        this.instance = instance;
    }

    public Solution computeTSP(int nbThreads, long nbTasks)
    {
        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);

        List<Future<Solution>> results = new ArrayList<Future<Solution>>();
        List<Callable<Solution>> tasks = new ArrayList<Callable<Solution>>();

        long startTime=System.currentTimeMillis();

        long currentTime;

        while((System.currentTimeMillis()-startTime)/1_000<=max_cpu)
        {
            long timeLeft = max_cpu - (System.currentTimeMillis() - startTime)/1_000;
            results.add(executor.submit(new Swapper(timeLeft)));
        }

        Solution best = solution;

        // get best solution
        try {
            long counter=0l;
            for(Future<Solution> t : results){
                if(t.get().getOF() < best.getOF())
                {
                    best = t.get();
                }
            }
        }
        catch (InterruptedException | ExecutionException e) {
            return null;
        }

        return best;
    }


}
