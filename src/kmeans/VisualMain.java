/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;


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

public class VisualMain
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {


        String center = "center";
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("str0");
        strings.add("str1");
        strings.add("str2");
        strings.add("str3");
        strings.add("str4");
        strings.add("str5");
        strings.add("str6");


        // Graph<V, E> where V is the type of the vertices
        // and E is the type of the edges
        Graph<String, String> g = new SparseMultigraph<String, String>();
        // Add some vertices. From above we defined these to be type String.
        g.addVertex(center);
        for (int i = 0; i < strings.size(); i++)
        {
            String str = strings.get(i);
            g.addVertex(str);
            g.addEdge("c-" + str, center, str);

        }




        // Add some edges. From above we defined these to be of type String
        // Note that the default is for undirected edges.
        //g.addEdge("Edge-A", v1, v2); // Note that Java 1.5 auto-boxes primitives
        //g.addEdge("Edge-B", v2, v3);
        // Let's see what we have. Note the nice output from the
        // SparseMultigraph<V,E> toString() method
        System.out.println("The graph g = " + g.toString());
        // Note that we can use the same nodes and edges in two different
        // graphs.
		/*Graph<String, String> g2 = new SparseMultigraph<String, String>();
        g2.addVertex((String) 1);
        g2.addVertex((String) 2);
        g2.addVertex((String) 3);
        g2.addEdge("Edge-A", 1, 3);
        g2.addEdge("Edge-B", 2, 3, EdgeType.DIRECTED);
        g2.addEdge("Edge-C", 3, 2, EdgeType.DIRECTED);
        g2.addEdge("Edge-P", 2, 3); // A parallel edge
        System.out.println("The graph g2 = " + g2.toString());
         */
        // SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in
        // here
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<String, String> layout = new FRLayout(g);
        layout.setSize(new Dimension(300, 300)); // sets the initial size of the
        // space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<String, String> vv = new BasicVisualizationServer<String, String>(
                layout);

        vv.setPreferredSize(new Dimension(350, 350)); // Sets the viewing area
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
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);



        JFrame frame = new JFrame("Simple Graph View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

    }
}
