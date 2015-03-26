package kmeans;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Implementation of K-means clustering.
 */
public class BasicKMeans implements KMeans
{
    // Temporary clusters used during the clustering process.  Converted to
    // an array of the simpler class Cluster at the end of process.
    private ProtoCluster[] mProtoClusters;
    // Cache of Strings-to-center distances. Number of entries =
    // number of clusters X number of strings.
    private double[][] mDistanceCache;
    // Used in makeAssignments() to figure out how many moves are made
    // during each iteration -- the cluster assignment for string n is
    // found in mClusterAssignments[n] where the N strings are numbered
    // 0 ... (N-1)
    private int[] mClusterAssignments;
    // arraylist used to store the strings to be clustered.
    private ArrayList mStrings = new ArrayList();
    // The desired number of clusters and maximum number
    // of iterations.
    private int mK, mMaxIterations;
    // Seed for the random number generator used to select
    // strings for the initial cluster centers.
    private long mRandomSeed;
    // An array of Cluster objects: the output of k-means.
    private Cluster[] mClusters;

    
    /**
     * Constructor
     * 
     */
    public BasicKMeans()
    {
    }

    /**
     * Constructor
     * 
     * @param strings arraylist containing the strings to be clustered.
     * @param k  the number of desired clusters.
     * @param maxIterations the maximum number of clustering iterations.
     * @param randomSeed seed used with the random number generator.
     */
    public BasicKMeans(ArrayList strings, int k, int maxIterations, long randomSeed)
    {
        mStrings = strings;
        // Can't have more clusters than the number of strings.
        mK = Math.min(k, mStrings.size());
        mMaxIterations = maxIterations;
        mRandomSeed = randomSeed;

    }

    /**
     * Get the clusters computed by the algorithm.  This method should
     * not be called until clustering has completed successfully.
     * 
     * @return an array of Cluster objects.
     */
    public Cluster[] getClusters()
    {
        return mClusters;
    }

    /**
     * Run the clustering algorithm.
     */
    public void run()
    {
        try
        {
            // Note the start time.
            long startTime = System.currentTimeMillis();
            // Randomly initialize the cluster centers creating the
            // array mProtoClusters.
            initCenters();
            // Perform the initial computation of distances.
            computeDistances();
            // Make the initial cluster assignments.
            makeAssignments();
            // Number of moves in the iteration and the iteration counter.
            int moves = 0;
            int it = 0;
            // Main Loop:
            //
            // Two stopping criteria:
            // - no moves in makeAssignments 
            //   (moves == 0)
            // OR
            // - the maximum number of iterations has been reached
            //   (it == mMaxIterations)
            //
            do
            {
                // Compute the centers of the clusters that need updating.
                computeCenters();
                // Compute the stored distances between the updated clusters and the
                // coordinates.
                computeDistances();
                // Make this iteration's assignments.
                moves = makeAssignments();
                it++;
            } while (moves > 0 && it < mMaxIterations);
            // Transform the array of ProtoClusters to an array
            // of the simpler class Cluster.
            mClusters = generateFinalClusters();
            long executionTime = System.currentTimeMillis() - startTime;
        } catch (InsufficientMemoryException ex)
        {
            Logger.getLogger(BasicKMeans.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Randomly select strings to be the initial cluster centers.
     */
    private void initCenters()
    {
        Random random = new Random(mRandomSeed);
        // total number of strings
        int stringCount = mStrings.size();
        // The array mClusterAssignments is used only to keep track of the cluster 
        // membership for each string.  The method makeAssignments() uses it
        // to keep track of the number of moves.
        if (mClusterAssignments == null)
        {
            mClusterAssignments = new int[stringCount];
            // Initialize to -1 to indicate that they haven't been assigned yet.
            Arrays.fill(mClusterAssignments, -1);
        }

        // Place the string indices into an array and shuffle it.
        int[] indices = new int[stringCount];
        for (int i = 0; i < stringCount; i++)
        {
            indices[i] = i;
        }
        for (int i = 0, m = stringCount; m > 0; i++, m--)
        {
            int j = i + random.nextInt(m);
            if (i != j)
            {
                // Swap the indices.
                indices[i] ^= indices[j];
                indices[j] ^= indices[i];
                indices[i] ^= indices[j];
            }
        }
        // build temporary clusters
        mProtoClusters = new ProtoCluster[mK];
        // assign centers
        for (int i = 0; i < mK; i++)
        {
            int coordIndex = indices[i];
            mProtoClusters[i] = new ProtoCluster((String) mStrings.get(coordIndex), coordIndex);
            mClusterAssignments[indices[i]] = i;
        }
        // print the initial centers
        System.out.println("Initial step, centers are:::::::::::::::::::::::::::::");
        for (int i = 0; i < mK; i++)
        {
            System.out.print("center" + i + ": " + mProtoClusters[i].getCenter() + "|| ");
        }
        System.out.println("\n::::::::::::::::::::::::::::::::::::::::::::::::::");

    }

    /**
     * Recompute the centers of the protoclusters with 
     * update flags set to true.
     */
    private void computeCenters()
    {
        int numClusters = mProtoClusters.length;

        // Sets the update flags of the protoclusters that haven't been deleted and
        // whose memberships have changed in the iteration just completed.
        //
        for (int c = 0; c < numClusters; c++)
        {
            ProtoCluster cluster = mProtoClusters[c];
            if (cluster.getConsiderForAssignment())
            {
                if (!cluster.isEmpty())
                {
                    // This sets the protocluster's update flag to
                    // true only if its membership changed in last call
                    // to makeAssignments().  
                    cluster.setUpdateFlag();
                    // If the update flag was set, update the center.
                    if (cluster.needsUpdate())
                    {
                        cluster.updateCenter(mStrings);
                    }
                } else
                {
                    // When a cluster loses all of its members, it
                    // falls out of contention.  So it is possible for
                    // k-means to return fewer than k clusters.
                      cluster.setConsiderForAssignment(false);
                }
            }
        }

        for (int i = 0; i < mK; i++)
        {
            System.out.print("center" + i + ": " + mProtoClusters[i].getCenter() + " ");
        }
        System.out.println();
    }

    /** 
     * Compute distances between strings and cluster centers,
     * storing them in the distance cache.  Only distances that
     * need to be computed are computed.  This is determined by
     * distance update flags in the protocluster objects.
     */
    private void computeDistances() throws InsufficientMemoryException
    {

        int numStrings = mStrings.size();
        int numClusters = mProtoClusters.length;

        if (mDistanceCache == null)
        {
            // Explicit garbage collection to reduce likelihood of insufficient
            // memory.
            System.gc();
            // Ensure there is enough memory available for the distances.
            // Throw an exception if not.
            long memRequired = 8L * numStrings * numClusters;
            if (Runtime.getRuntime().freeMemory() < memRequired)
            {
                throw new InsufficientMemoryException();
            }
            // Instantiate an array to hold the distances between coordinates
            // and cluster centers
            mDistanceCache = new double[numStrings][numClusters];
        }

        for (int string = 0; string < numStrings; string++)
        {
            // Update the distances between the string and all
            // clusters currently in contention with update flags set.
            for (int clust = 0; clust < numClusters; clust++)
            {
                ProtoCluster cluster = mProtoClusters[clust];
                if (cluster.getConsiderForAssignment() && cluster.needsUpdate())
                {
                        mDistanceCache[string][clust] =
                                Utils.distance((String) mStrings.get(string), cluster.getCenter());
                }
            }
        }
    }

    /** 
     * Assign each string to the nearest cluster.  Called once
     * per iteration.  Returns the number of strings that have
     * changed their cluster membership.
     */
    private int makeAssignments()
    {
        int moves = 0;
        int stringCount = mStrings.size();
        // Checkpoint the clusters, so we'll be able to tell
        // which ones have changed after all the assignments have been
        // made.
        int numClusters = mProtoClusters.length;
        for (int c = 0; c < numClusters; c++)
        {
            if (mProtoClusters[c].getConsiderForAssignment())
            {
                mProtoClusters[c].checkPoint();
            }
        }

        // Now do the assignments.
        for (int i = 0; i < stringCount; i++)
        {
            int c = nearestCluster(i);
            mProtoClusters[c].add(i);
            if (mClusterAssignments[i] != c)
            {
                mClusterAssignments[i] = c;
                moves++;
            }
        }
        return moves;
    }

    /**
     * Find the nearest cluster to the string identified by
     * the specified index.
     */
    private int nearestCluster(int ndx)
    {
        int nearest = -1;
        double min = Integer.MAX_VALUE;
        int numClusters = mProtoClusters.length;
        for (int c = 0; c < numClusters; c++)
        {
            if (mProtoClusters[c].getConsiderForAssignment())
            {
                double d = mDistanceCache[ndx][c];
                if (d < min)
                {
                    min = d;
                    nearest = c;
                }
            }
        }
        return nearest;
    }

    /**
     * Generate an array of Cluster objects from mProtoClusters.
     * 
     * @return array of Cluster object references.
     */
    public Cluster[] generateFinalClusters()
    {
        int numClusters = mProtoClusters.length;

        // Convert the proto-clusters to the final Clusters.
        //
        // - accumulate in a list.
        List<Cluster> clusterList = new ArrayList<Cluster>(numClusters);
        for (int c = 0; c < numClusters; c++)
        {
            ProtoCluster pcluster = mProtoClusters[c];
            if (!pcluster.isEmpty())
            {
                Cluster cluster = new Cluster(pcluster.getMembership(), pcluster.getCenter());
                clusterList.add(cluster);
            }
        }

        // - convert list to an array.
        Cluster[] clusters = new Cluster[clusterList.size()];
        clusterList.toArray(clusters);

        return clusters;
    }

    /**
     * Clean up items used by the clustering algorithm that are no longer needed.
     */
    private void cleanup()
    {
        mProtoClusters = null;
        mDistanceCache = null;
        mClusterAssignments = null;
    }

    /**
     * Cluster class used temporarily during clustering.  Upon completion,
     * the array of ProtoClusters is transformed into an array of
     * Clusters.
     */
    public static class ProtoCluster 
    {

        // The previous iteration's cluster membership and
        // the current iteration's membership.  Compared to see if the
        // cluster has changed during the last iteration.
        private int[] mPreviousMembership;
        private int[] mCurrentMembership;
        private int mCurrentSize;
        // The cluster center.
        private String mStringCenter;
        // Born true, so the first call to updateDistances() will set all the
        // distances.
        private boolean mUpdateFlag = true;
        // Whether or not this cluster takes part in the operations.
        private boolean mConsiderForAssignment = true;
        private ArrayList<String> centers;


        public void setmStringCenter(String mStringCenter)
        {
            this.mStringCenter = mStringCenter;
            if (!isNewCenterInCenterList(mStringCenter))
            {
                centers.add(mStringCenter);
            }
        }

        public boolean isNewCenterInCenterList(String newCenter)
        {
            return centers.contains(newCenter);
        }

        /**
         * Constructor
         * 
         * @param center  the initial cluster center.
         * @param stringIndex  the initial member.
         */

        ProtoCluster(String center, int stringIndex)
        {
            mStringCenter = center;
            // No previous membership.
            mPreviousMembership = new int[0];
            // Provide space for 10 members to be added initially.
            mCurrentMembership = new int[10];
            mCurrentSize = 0;
            add(stringIndex);
            centers=new ArrayList<String>();
        }

        /**
         * Get the members of this protocluster.
         * 
         * @return an array of string indices.
         */
        int[] getMembership()
        {
            trimCurrentMembership();
            return mCurrentMembership;
        }

        /**
         * Get the protocluster's center.
         * 
         * @return center
         */
        String getCenter()
        {
            return mStringCenter;
        }

        /**
         * Reduces the length of the array of current members to
         * the number of members.
         */
        void trimCurrentMembership()
        {
            if (mCurrentMembership.length > mCurrentSize)
            {
                int[] temp = new int[mCurrentSize];
                System.arraycopy(mCurrentMembership, 0, temp, 0, mCurrentSize);
                mCurrentMembership = temp;
            }
        }

        /**
         * Add a string to the protocluster.
         * 
         * @param ndx index of the coordinate to be added.
         */
        void add(int ndx)
        {
            // Ensure there's space to add the new member.
            if (mCurrentSize == mCurrentMembership.length)
            {
                // If not, double the size of mCurrentMembership.
                int newCapacity = Math.max(10, 2 * mCurrentMembership.length);
                int[] temp = new int[newCapacity];
                System.arraycopy(mCurrentMembership, 0, temp, 0, mCurrentSize);
                mCurrentMembership = temp;
            }
            // Add the index.
            mCurrentMembership[mCurrentSize++] = ndx;
        }

        /**
         * Does the protocluster contain any members?
         * 
         * @return true if the cluster is empty.
         */
        boolean isEmpty()
        {
            return mCurrentSize == 0;
        }

        /**
         * Compares the previous and the current membership.
         * Sets the update flag to true if the membership
         * changed in the previous call to makeAssignments().
         */
        void setUpdateFlag()
        {
            // Trim the current membership array length down to the
            // number of members.
            trimCurrentMembership();
            mUpdateFlag = false;
            if (mPreviousMembership.length == mCurrentSize)
            {
                for (int i = 0; i < mCurrentSize; i++)
                {
                    if (mPreviousMembership[i] != mCurrentMembership[i])
                    {
                        mUpdateFlag = true;
                        break;
                    }
                }
            } else
            { // Number of members has changed.
                mUpdateFlag = true;
            }
        }

        /**
         * Clears the current membership after copying it to the
         * previous membership.
         */
        void checkPoint()
        {
            mPreviousMembership = mCurrentMembership;
            mCurrentMembership = new int[10];
            mCurrentSize = 0;
        }

        /**
         * Is this protocluster currently in contention?
         * 
         * @return true if this cluster is still in the running.
         */
        boolean getConsiderForAssignment()
        {
            return mConsiderForAssignment;
        }

        /**
         * Set the flag to indicate that this protocluster is
         * in or out of contention.
         * 
         * @param b
         */
        void setConsiderForAssignment(boolean b)
        {
            mConsiderForAssignment = b;
        }

        /**
         * Get the value of the update flag.  This value is
         * used to determine whether to update the cluster center and
         * whether to recompute distances to the cluster.
         * 
         * @return the value of the update flag.
         */
        boolean needsUpdate()
        {
            return mUpdateFlag;
        }

        /**
         * Update the cluster center. The new center has the nearest number
         * to the average distances of all strings to the center in current cluster.
         * 
         * @param strings the arraylist of strings.
         */
        void updateCenter(ArrayList strings)
        {
            //reset the center
            //mStringCenter = "";
            //array used to store the strings
            String[] tempCurrentStrings = new String[mCurrentSize];
            //array used to store the distances
            double[] tempDistances = new double[mCurrentSize];

            if (mCurrentSize > 0)
            {
                for (int i = 0; i < mCurrentSize; i++)
                {
                    tempCurrentStrings[i] = (String) strings.get(mCurrentMembership[i]);
                    tempDistances[i] = Utils.distance(this.getCenter(), tempCurrentStrings[i]);
                }
                //find out and update the center
                int index = Utils.pickupNearestAvg(tempDistances);

                if(!isNewCenterInCenterList((String)strings.get(mCurrentMembership[index])))
                {
                    this.setmStringCenter((String)strings.get(mCurrentMembership[index]));
                }else
                {
                    return;
                }
                
            }
        }
    }

}
