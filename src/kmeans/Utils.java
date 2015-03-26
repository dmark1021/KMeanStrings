/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmeans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Utils
{

    public static double distance(String s, String t)
    {
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1

        n = s.length();
        m = t.length();
        if (n == 0)
        {
            return m;
        }
        if (m == 0)
        {
            return n;
        }
        d = new int[n + 1][m + 1];

        // Step 2

        for (i = 0; i <= n; i++)
        {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++)
        {
            s_i = s.charAt(i - 1);
            // Step 4
            for (j = 1; j <= m; j++)
            {
                t_j = t.charAt(j - 1);

                // Step 5
                if (s_i == t_j)
                {
                    cost = 0;
                } else
                {
                    cost = 1;
                }

                // Step 6
                d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
                        d[i - 1][j - 1] + cost);

            }
        }
        // Step 7

        //      double distance = longestSubstr(s,t)*0.3 + (Math.abs(s.length()-t.length()))*0.3 + d[n][m]*0.4;
        //double distance =  (Math.abs(s.length()-t.length()))*0.5 + d[n][m]*0.5;
        // double distance = d[n][m]*(Math.abs(s.length()-t.length()));
        double distance = (double) d[n][m] / Math.max(s.length(), t.length());
        // System.out.println(distance);
        return distance;


    }

    private static int Minimum(int a, int b, int c)
    {
        int mi;
        mi = a;
        if (b < mi)
        {
            mi = b;
        }
        if (c < mi)
        {
            mi = c;
        }
        return mi;
    }

    public static double variance(double[] population)
    {
        long n = 0;
        double mean = 0;
        double s = 0.0;

        for (double x : population)
        {
            n++;
            double delta = x - mean;
            mean += delta / n;
            s += delta * (x - mean);
        }
        // if you want to calculate std deviation
        // of a sample change this to (s/(n-1))
        return (s/n);
    }

    /**
     * @param population an array, the population
     * @return the standard deviation
     */
    public static double standard_deviation(double[] population)
    {
        return Math.sqrt(variance(population));
    }

    public static int longestSubstr(String str_, String toCompare_)
    {
        if (str_.isEmpty() || toCompare_.isEmpty())
        {
            return 0;
        }

        int[][] compareTable = new int[str_.length()][toCompare_.length()];
        int maxLen = 0;

        for (int m = 0; m < str_.length(); m++)
        {
            for (int n = 0; n < toCompare_.length(); n++)
            {
                compareTable[m][n] = (str_.charAt(m) != toCompare_.charAt(n)) ? 0
                        : (((m == 0) || (n == 0)) ? 1
                        : compareTable[m - 1][n - 1] + 1);
                maxLen = (compareTable[m][n] > maxLen) ? compareTable[m][n]
                        : maxLen;
            }
        }
        return str_.length() - maxLen;
    }



    public static ArrayList removeDuplicateWithOrder(ArrayList arlList)
    {
        ArrayList temp = new ArrayList();
        temp = (ArrayList) arlList.clone();
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = temp.iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            if (set.add(element))
            {
                newList.add(element);
            }
        }
        temp.clear();
        temp.addAll(newList);
        return temp;
    }

        static int pickupNearestAvg(double[] distances)
    {
        int index = 0;
        double sumDis = 0;
        double avgDis = 0;
        for (int i = 0; i < distances.length; i++)
        {
            sumDis = sumDis + distances[i];
        }
        avgDis = sumDis / distances.length;
        double temp = Math.abs(distances[0] - avgDis);

        for (int i = 1; i < distances.length; i++)
        {
            if (temp > (Math.abs(distances[i] - avgDis)))
            {
                temp = Math.abs(distances[i] - avgDis);
                index = i;
            }
        }
        return index;
    }
}
