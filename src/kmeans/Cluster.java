package kmeans;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



/**
 * Class to represent a cluster of strings.
 */
public class Cluster
{

    // Indexes of the member strings.
    private int[] mMemberIndexes;
    // The cluster center.

    private ArrayList<String> centers;

    private String mStringCenter;
    // standard deviation of cluster
    private double stdDiviation;
    // array of distances
    private double[] distanceToCenter;
    // set the cluster
    private boolean splited = false;
    private boolean merged = false;


    public boolean hasBeenMerged()
    {
        return merged;
    }

    public void setMerged(boolean merged)
    {
        this.merged = merged;
    }


    public void setmStringCenter(String mStringCenter)
    {
        this.mStringCenter = mStringCenter;
        if(!isNewCenterInCenterList(mStringCenter)){
            centers.add(mStringCenter);
        }
    }

    public boolean isNewCenterInCenterList(String newCenter ){
        return centers.contains(newCenter);
    }

    /**
     * Constructor.
     * 
     * @param memberIndexes indexes of the member strings.
     * @param center the cluster center.
     */
    public Cluster()
    {

    }
    public Cluster(int[] memberIndexes, String stringCenter)
    {
        mMemberIndexes = memberIndexes;
        mStringCenter = stringCenter;
        centers=new ArrayList<String>();
    }

    /**
     * Convert local indexes into parent indexes.
     *
     * @param parent indexes of the cluster splited.
     */
    public void setmMemberIndex(int[] parentIndexes)
    {
        if (splited)
        {
            for (int i = 0; i < mMemberIndexes.length; i++)
            {
                mMemberIndexes[i] = parentIndexes[mMemberIndexes[i]];
            }
        }
    }

    /**
     * set the cluster is splited.
     * 
     */
    public void setSplited(boolean splited)
    {
        this.splited = splited;
    }

    /**
     * Get the member indexes.
     * 
     * @return an array containing the indexes of the member strings.
     */
    public int[] getMemberIndexes()
    {
        return mMemberIndexes;
    }

    /**
     * Get the cluster center.
     * 
     * @return a reference to the cluster center array.
     */
    public String getCenter()
    {
        return mStringCenter;
    }

    /**
     * Calculate the standard deviation.
     *
     */
    public void caculateStdDiviation()
    {
        stdDiviation = Utils.standard_deviation(distanceToCenter);
    }

    /**
     * Get the standard deviation.
     *
     * @return standard deviation.
     */
    public double getStdDiviation()
    {
        return stdDiviation;
    }

    /**
     * Calculate the distances to the center
     *
     * @param mStrings the original strings that need to be clustered.
     * @param centerStr the center string of current cluster.
     */
    public void caucalteDistanceToCenter(ArrayList mStrings, String centerStr)
    {
        distanceToCenter = new double[mMemberIndexes.length];
        mStringCenter = centerStr;

        for (int j = 0; j < mMemberIndexes.length; j++)
        {
            distanceToCenter[j] = Utils.distance((String) mStrings.get(mMemberIndexes[j]), mStringCenter);
        }
    }

    /**
     * Get the array of distances.
     *
     * @return array of distances.
     */
    public double[] getDistanceTocenter()
    {
        return distanceToCenter;
    }

    /**
     * Check the cluster members.
     *
     * @return true if cluster has members.
     */
    public boolean hasMember()
    {
        return mMemberIndexes.length > 0;
    }

    public void updateClusterCenter(ArrayList<String> mStrings)
    {

        int mCurrentSize = this.getMemberIndexes().length;
        //reset the center
        //mClusters.get(index).setmStringCenter("");
        //array used to store the strings
        String[] tempCurrentStrings = new String[mCurrentSize];
        //array used to store the distances
        double[] tempDistances = new double[mCurrentSize];

        if (mCurrentSize > 0)
        {
            for (int i = 0; i < mCurrentSize; i++)
            {
                tempCurrentStrings[i] = (String) mStrings.get(this.getMemberIndexes()[i]);
                tempDistances[i] = Utils.distance(this.getCenter(), tempCurrentStrings[i]);
            }
            //find out and update the center
            int centerIndex = Utils.pickupNearestAvg(tempDistances);

            this.setmStringCenter((String) mStrings.get(this.getMemberIndexes()[centerIndex]));
        }
    }
        
    /**
     * Output the string of cluster.
     *
     */
    public void printString(ArrayList mStrings)
    {
        for (int j = 0; j < getMemberIndexes().length; j++)
        {
            System.out.println((String) mStrings.get(getMemberIndexes()[j]));
        }
    }

    public ArrayList<String> getStrings(ArrayList mStrings)
    {
        ArrayList<String> result = new ArrayList<String>();
        for (int j = 0; j < getMemberIndexes().length; j++)
        {
            result.add((String) mStrings.get(getMemberIndexes()[j]));
        }

        return result;

    }

    public void outputToFile(ArrayList mStrings) throws IOException
    {
        FileWriter fw = new FileWriter("d:/output.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        String myreadline = new String();

        for (int j = 0; j < getMemberIndexes().length; j++)
        {
            myreadline = (String) mStrings.get(getMemberIndexes()[j]);

        }
        bw.write(myreadline);
        bw.newLine();

        bw.flush();
        bw.close();
        fw.close();
    }



}
