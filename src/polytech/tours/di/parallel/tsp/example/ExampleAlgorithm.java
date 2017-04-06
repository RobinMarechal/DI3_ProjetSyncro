package polytech.tours.di.parallel.tsp.example;

import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import polytech.tours.di.parallel.tsp.Algorithm;
import polytech.tours.di.parallel.tsp.Instance;
import polytech.tours.di.parallel.tsp.InstanceReader;
import polytech.tours.di.parallel.tsp.Solution;
import polytech.tours.di.parallel.tsp.TSPCostCalculator;

/**
 * Implements an example in which we read an instance from a file and print out some of the distances in the distance matrix.
 * Then we generate a random solution and computer its objective function. Finally, we print the solution to the output console.
 * @author Jorge E. Mendoza (dev@jorge-mendoza.com)
 * @version %I%, %G%
 *
 */
public class ExampleAlgorithm implements Algorithm {

	// Constants
	private final int NB_THREADS = 8;


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

		long startTime=System.currentTimeMillis();
		for(int i=0; i<instance.getN(); i++){
			s.add(i);
		}

//		best = localSearch(s, instance, max_cpu);
		Swapper swapper = new Swapper(s.clone(), instance, max_cpu);
		swapper.run();
		best = swapper.getBestSolution();

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

//	public Solution localSearch(Solution solution, Instance instance, long max_cpu)
//	{
//		int i, j, size;
//		Solution solToSwap;
//		Solution bestSol = solution.clone();
//		double matrix[][] = instance.getDistanceMatrix();
//		long startTime=System.currentTimeMillis();
//		size = solution.size();
//
//		bestSol.setOF(TSPCostCalculator.calcOF(matrix, bestSol));
//
//		while((System.currentTimeMillis()-startTime)/1_000<=max_cpu){
//			i = ThreadLocalRandom.current().nextInt(size);
//			j = ThreadLocalRandom.current().nextInt(size);
//
//			if(i == j) continue;
//
//			solToSwap = bestSol.clone();
//			solToSwap.relocate(i, j);
//			//set the objective function of the solution
//			double OF = TSPCostCalculator.calcOF(matrix, solToSwap);
//			solToSwap.setOF(OF);
//			if(OF < bestSol.getOF())
//			{
//				System.out.println(OF);
//				bestSol = solToSwap;
//			}
//			counter++;
//		}
//
//		return bestSol;
//	}

}
