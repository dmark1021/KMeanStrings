package kmeans;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;




import java.util.ArrayList;




/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class KmeansTest {

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        // Read the strings from file
        ArrayList mStrings = new ArrayList();
        File file = new File("d:\\testt.txt");
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        int line = 1;
        while ((tempString = reader.readLine()) != null)
        {
            mStrings.add(tempString);
            line++;
        }


        HierarchicalKMeans hierarchicalKMeans = new HierarchicalKMeans(mStrings);
        Clusters clusters = hierarchicalKMeans.getClusters();
        clusters.printString();




    }

}
