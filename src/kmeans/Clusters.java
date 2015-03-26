package kmeans;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * Class to represent a set of clusters.
 */
public class Clusters
{

    // The arrayList of all clusters.
    private ArrayList<Cluster> mClusters;
    // The arrayList of strings need to be clustered.
    private ArrayList<String> mStrings;

    /**
     * Constructor.
     *
     * @param mStrings   arrayList of all clusters.
     * @param mClusters   arrayList of strings need to be clustered.
     */
    Clusters(ArrayList mStrings, ArrayList<Cluster> mClusters)
    {
        this.mStrings = mStrings;
        this.mClusters = mClusters;
    }

    /**
     * Calculate the cluster which has the maximum standard deviation in the clusters.
     *
     * @return results   the index of cluster of maximum standard deviation.
     *
     */
    public int getClusterIndexWithMaxStdDiv()
    {
        int results = 0;
        // Calculate the first cluster
        mClusters.get(results).caucalteDistanceToCenter(mStrings, mClusters.get(results).getCenter());
        mClusters.get(results).caculateStdDiviation();
        // Compare and generate the maximum
        for (int i = 1; i < mClusters.size(); i++)
        {
            mClusters.get(i).caucalteDistanceToCenter(mStrings, mClusters.get(i).getCenter());
            mClusters.get(i).caculateStdDiviation();

            if (mClusters.get(i).getStdDiviation() > mClusters.get(results).getStdDiviation())
            {
                results = i;
            }
        }
        return results;
    }

    /**
     * Get a cluster by its index.
     *
     * @param index    the index of cluster.
     *
     */
    public Cluster getCluster(int index)
    {
        return mClusters.get(index);
    }

    /**
     * Remove a cluster by its index.
     *
     * @param clusterIndex    the index of cluster.
     *
     */
    public void removeClusterBy(int clusterIndex)
    {
        mClusters.remove(clusterIndex);
    }

    /**
     * Add a cluster to current arraylist of clusters.
     *
     * @param cluster    the cluster needs to be added.
     *
     */
    public void addToClusters(Cluster cluster)
    {
        mClusters.add(cluster);
    }

    /**
     * Add an arrayList of clusters to current arrayList of clusters.
     *
     * @param clusters    the arrayList of new clusters.
     *
     */
    public void addToClusters(ArrayList<Cluster> clusters)
    {
        mClusters.addAll(clusters);
    }

    /**
     * Use BasicKMeans to split a cluster into two sub clusters.
     *
     * @param clusterIndex    the index of cluster needs to be splited.
     * @return resultList    the ArrayList of clusters splited
     *
     */
    public ArrayList<Cluster> splitACluster(int clusterIndex)
    {
        // ArrayList to store the result of split
        ArrayList<Cluster> resultList = new ArrayList();
        // Get parent cluster needs to be splited
        Cluster parentClusterForSplit = mClusters.get(clusterIndex);
        // Use BasicKMeans to split
        Random random = new Random();
        long randomSeed = random.nextLong();
        int maxIterations = 200;
        BasicKMeans basicKMeans = new BasicKMeans(getClusterStringsByClusterIndex(clusterIndex), 2, maxIterations, randomSeed);
        basicKMeans.run();
        Cluster[] splitedClusters = basicKMeans.getClusters();
        // Convert the indexes of clusters generated
        for (int i = 0; i < splitedClusters.length; i++)
        {
            Cluster cluster = splitedClusters[i];
            if (cluster.hasMember())
            {
                cluster.setSplited(true);
                cluster.setmMemberIndex(parentClusterForSplit.getMemberIndexes());
            }
            resultList.add(cluster);
        }
        return resultList;
    }

    public void mergeTwoClusters(int clusterIndexA, int clusterIndexB)
    {


        Cluster clusterAForMerge = mClusters.get(clusterIndexA);
        Cluster clusterBForMerge = mClusters.get(clusterIndexB);

        int[] newMemberIndexes = new int[clusterAForMerge.getMemberIndexes().length + clusterBForMerge.getMemberIndexes().length];
        System.arraycopy(clusterAForMerge.getMemberIndexes(), 0, newMemberIndexes, 0, clusterAForMerge.getMemberIndexes().length);
        System.arraycopy(clusterBForMerge.getMemberIndexes(), 0, newMemberIndexes, clusterAForMerge.getMemberIndexes().length, clusterBForMerge.getMemberIndexes().length);
        Cluster result = new Cluster(newMemberIndexes, clusterAForMerge.getCenter());

        mClusters.remove(clusterIndexA);
        mClusters.remove(clusterIndexB - 1);
        result.updateClusterCenter(mStrings);
        result.setMerged(true);
        mClusters.add(result);

    }

    public boolean canBeMerged(int clusterIndexA, int clusterIndexB)
    {
        Cluster clusterAForMerge = mClusters.get(clusterIndexA);
        Cluster clusterBForMerge = mClusters.get(clusterIndexB);
        String centerOfClusterA = clusterAForMerge.getCenter();
        String centerOfClusterB = clusterBForMerge.getCenter();
        Double distanceOfTwoCenters = Utils.distance(centerOfClusterA, centerOfClusterB);

        return distanceOfTwoCenters < 0.1;


    }

    public void checkAndMerge()
    {
        for (int clusterIndexA = 0; clusterIndexA < mClusters.size(); clusterIndexA++)
        {
            for (int clusterIndexB = clusterIndexA + 1; clusterIndexB < mClusters.size(); clusterIndexB++)
            {
                if (canBeMerged(clusterIndexA, clusterIndexB))
                {
                    mergeTwoClusters(clusterIndexA, clusterIndexB);

                }
            }
            
        }
    }

    /**
     * Get the strings of one cluster
     *
     * @param clusterIndex    the index of a cluster
     * @return resultStrings   the ArrayList stores the strings of the cluster
     *
     */
    private ArrayList<String> getClusterStringsByClusterIndex(int clusterIndex)
    {
        Cluster cluster = mClusters.get(clusterIndex);
        int[] mMemberIndexes = cluster.getMemberIndexes();
        ArrayList resultStrings = new ArrayList<String>();
        for (int i = 0; i < mMemberIndexes.length; i++)
        {
            resultStrings.add(mStrings.get(mMemberIndexes[i]));
        }
        return resultStrings;
    }

    /**
     * Output the strings of all clusters
     *
     *
     */
    public void printString()
    {
        Graph<String, String> g = new SparseMultigraph<String, String>();
        for (int i = 0; i < mClusters.size(); i++)
        {
            System.out.println("Cluster" + i + ": ");
            mClusters.get(i).printString(mStrings);
            mClusters.get(i).caucalteDistanceToCenter(mStrings, mClusters.get(i).getCenter());
            mClusters.get(i).caculateStdDiviation();
            if (mClusters.get(i).hasBeenMerged())
            {
                System.out.println("Merged cluster!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                
            }
            System.out.println("Center:::: " + mClusters.get(i).getCenter());
            System.out.println("Stand_diviation: " + mClusters.get(i).getStdDiviation());
            System.out.println("-------------------------------------");


            String center = mClusters.get(i).getCenter()+"CENTER";
            g.addVertex(center);
            ArrayList<String> strings = mClusters.get(i).getStrings(mStrings);
            for (int j = 0; j < strings.size(); j++)
            {
                String str=strings.get(j);
                g.addVertex(str);
                g.addEdge(str, center, str);                
            }
            
        }

       // Layout<String, String> layout = new FRLayout(g);
        Layout<String, String> layout = new SpringLayout(g);

        layout.setSize(new Dimension(600, 600)); // sets the initial size of the
        // space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<String, String> vv = new BasicVisualizationServer<String, String>(
                layout);

        vv.setPreferredSize(new Dimension(1000, 800)); // Sets the viewing area

        // size
        // Set up a new stroke Transformer for the edges
        float dash[] =
        {
            10.0f
        };
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Transformer<String, Stroke> edgeStrokeTransformer = new Transformer<String, Stroke>()
        {

            public Stroke transform(String s)
            {
                return edgeStroke;
            }
        };

        // Setup up a new vertex to paint transformer...
        Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>()
        {

            public Paint transform(String i)
            {
                return Color.GREEN;
            }
        };
        // Set up a new stroke Transformer for the edges

        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        //vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);



        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }

}
