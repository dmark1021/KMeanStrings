/*
 * The main API
 */
package kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class HierarchicalKMeans
{
    private Clusters clusters;
    private double STAND_DEVIATION_LIMIT = 0.2;
    private ArrayList<String> mStrings;
    private int initialNumOfClusters = 3;

    public void setInitialNumOfClusters(int initialNumOfClusters)
    {
        this.initialNumOfClusters = initialNumOfClusters;
    }

    public void setSTAND_DEVIATION_LIMIT(double STAND_DEVIATION_LIMIT)
    {
        this.STAND_DEVIATION_LIMIT = STAND_DEVIATION_LIMIT;
    }

    public HierarchicalKMeans()
    {
    }

    public HierarchicalKMeans(ArrayList<String> mStrings)
    {
        this.mStrings = mStrings;
        this.run();
    }

    
    public HierarchicalKMeans(ArrayList<String> mStrings, double stdDiviationLimit)
    {
        this.mStrings = mStrings;
        this.STAND_DEVIATION_LIMIT = stdDiviationLimit;
        this.run();
    }

    private void run()
    {
        Random random = new Random();
        long randomSeed = random.nextLong();
        int maxIterations = 200;
        

        BasicKMeans basicKMeans = new BasicKMeans(mStrings, initialNumOfClusters, maxIterations, randomSeed);
        basicKMeans.run();
        // Add the results of clusters into an ArrayList
        Cluster[] mClustersArray = basicKMeans.getClusters();
        ArrayList<Cluster> mClusters = new ArrayList<Cluster>();
        mClusters.addAll(Arrays.asList(mClustersArray));

        clusters = new Clusters(mStrings, mClusters);

                // set the iteration of process
        int iteration = 1;
        //while(true&&iteration<=3)
        while (true)
        {
            System.out.println("iteration" + iteration + ":=========================================================================");

            int index = clusters.getClusterIndexWithMaxStdDiv();
            //System.out.println(index);
            System.out.println("Max_Std_Div_Cluster: " + index);

            //Select the cluster with Max standard deviation and split it
            Cluster cluster = clusters.getCluster(index);
            //cluster.printString(mStrings);
            cluster.caucalteDistanceToCenter(mStrings, cluster.getCenter());
            cluster.caculateStdDiviation();
            double stdDiviation = cluster.getStdDiviation();
            //System.out.println(stdDiviation);

            if (stdDiviation <= STAND_DEVIATION_LIMIT)
            {
                break;    // If the standard deviation of cluster smaller than 0.1, stop the split
            } else
            {
                // split the cluster and remove it from ArrayList
                // add new clusters into ArrayList
                ArrayList<Cluster> clustersArray = clusters.splitACluster(index);
                clusters.removeClusterBy(index);
                clusters.addToClusters(clustersArray);
            }
            iteration++;
            //       clusters.printString();
        }

   //     clusters.printString();
    //    System.out.println("Merge clusters:::::::::::::::::::::::::::::");
        clusters.checkAndMerge();
    }

    public Clusters getClusters()
    {
        return clusters;
    }




}
