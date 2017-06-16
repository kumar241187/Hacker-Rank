import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

     static int twinArrays(int[] ar1, int[] ar2){

    int first_minimum_ar1 = Integer.MAX_VALUE;
    int second_minimum_ar1 = Integer.MAX_VALUE;
    int index_ar1=-1;

    int first_minimum_ar2 = Integer.MAX_VALUE;
    int second_minimum_ar2 = Integer.MAX_VALUE;
    int index_ar2=-1;


    for(int i=0;i<ar1.length;i++)
    {
        int element = ar1[i];
        if(first_minimum_ar1>=element)
        {
            second_minimum_ar1=first_minimum_ar1;
            first_minimum_ar1=element;
            index_ar1=i;
        }
    }

    for(int i=0;i<ar2.length;i++)
    {
        int element = ar2[i];
        if(first_minimum_ar2>=element)
        {
            second_minimum_ar2=first_minimum_ar2;
            first_minimum_ar2=element;
            index_ar2=i;
        }
    }

    if(index_ar2!=index_ar1)
        return first_minimum_ar1+first_minimum_ar2;


    return Math.min(first_minimum_ar1+second_minimum_ar2,first_minimum_ar2+second_minimum_ar1);

}

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int[] ar1 = new int[n];
        for(int ar1_i = 0; ar1_i < n; ar1_i++){
            ar1[ar1_i] = in.nextInt();
        }
        int[] ar2 = new int[n];
        for(int ar2_i = 0; ar2_i < n; ar2_i++){
            ar2[ar2_i] = in.nextInt();
        }
        int result = twinArrays(ar1, ar2);
        System.out.println(result);
    }
}
