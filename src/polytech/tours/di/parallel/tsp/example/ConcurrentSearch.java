package polytech.tours.di.parallel.tsp.example;

import polytech.tours.di.parallel.tsp.*;

import java.util.Properties;
import java.util.Random;

/**
 * Created by Robin on 05/04/2017.
 */
public class ConcurrentSearch implements Algorithm {

    // Constants
    private final int NB_THREADS = 8;
    private final int NB_TASKS = 100;

    private long counter = 0;

    @Override
    public Solution run(Properties config) {

        //read instance
        InstanceReader ir=new InstanceReader();
        ir.buildInstance(config.getProperty("instance"));
        //get the instance
        Instance instance=ir.getInstance();
        //print some distances
        System.out.println("d(1,2)="+instance.getDistance(1, 2));
        System.out.println("d(10,19)="+instance.getDistance(10, 19));
        //read maximum CPU time
        long max_cpu=Long.valueOf(config.getProperty("maxcpu"));
        //build a random solution
        Random rnd=new Random(Long.valueOf(config.getProperty("seed")));
        Solution s=new Solution();
        Solution best=null;

        // List of threads.....

//        long startTime=System.currentTimeMillis();
//        for(int i=0; i<instance.getN(); i++){
//            s.add(i);
//        }

        TSPCalculator calculator = new TSPCalculator(s.clone(), instance, max_cpu);
        best = calculator.computeTSP(NB_THREADS, NB_TASKS);

//		best = localSearch(s, instance, max_cpu);
//        Swapper swapper = new Swapper(s.clone(), instance, max_cpu);
//        swapper.call();
//        best = swapper.getBestSolution();

        System.out.println("BEST SOL ("+counter+" calculations) : ");
        return best; //execution(s, instance, max_cpu);
    }

    private Solution execution(Solution s, Instance instance, long max_cpu)
    {
//		CopyOnWriteArrayList<Swapper> swappers = new CopyOnWriteArrayList<>();
//		CopyOnWriteArrayList<Solution> solutions = new CopyOnWriteArrayList<>();
//
//		long startTime=System.currentTimeMillis();
//		for(int i=0; i<instance.getN(); i++){
//			s.add(i);
//		}
//
//		while((System.currentTimeMillis()-startTime)/1_000<=max_cpu)
//		{
//
//		}


        return s;
    }

}

