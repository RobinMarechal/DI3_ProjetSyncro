package polytech.tours.di.parallel.tsp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Launches the optimization algorithm
 *
 * @author Jorge E. Mendoza (dev@jorge-mendoza.com)
 * @version %I%, %G%
 */
public class Launcher
{

    /**
     * @param args[0] the file (path included) with he configuration settings
     */
    public static void main (String[] args)
    {
        //read properties
        Properties config = new Properties();
        try
        {
            config.loadFromXML(new FileInputStream(args[0]));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //dynamically load the algorithm class
        Algorithm algorithm = null;
        Algorithm algorithm2 = null;
        try
        {
            Class<?> c = Class.forName(config.getProperty("algorithm"));
            algorithm = (Algorithm) c.newInstance();
            Class<?> c2 = Class.forName(config.getProperty("algorithm2"));
            algorithm2 = (Algorithm) c2.newInstance();
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("-------------------------------------------------------");
        System.out.println("Class: " + algorithm.getClass().getSimpleName());
        System.out.println(config.getProperty(algorithm.getClass().getSimpleName() + "_description"));
        System.out.println("-------------------------------------------------------");

        //run algorithm
        Solution s = algorithm.run(config);

        //report solution
        System.out.println(s);

        System.out.println();
        System.out.println("-------------------------------------------------------");
        System.out.println("Class: " + algorithm2.getClass().getSimpleName());
        System.out.println(config.getProperty(algorithm2.getClass().getSimpleName() + "_description"));
        System.out.println("-------------------------------------------------------");

        //run algorithm2
        Solution s2 = algorithm2.run(config);

        //report solution
        System.out.println(s2);
    }
}
