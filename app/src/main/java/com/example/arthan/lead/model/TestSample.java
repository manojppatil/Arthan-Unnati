package com.example.arthan.lead.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class TestSample {

    public int solution(String s)
    {
//        String input="50052";
        String input=s;
        try
        {
       Integer.parseInt(s);
        }
        catch (NumberFormatException exc)
        {
            return -1;
        }
        catch (Exception e){
            return 0;
        }


        ArrayList<Integer> list=new ArrayList<>();
//        String possibleValue[]=new String[input.length()*input.length()];

        if(input.length()<=2)
        {
            return Integer.parseInt(s);
        }
            String init=input.charAt(0)+""+input.charAt(1);
            list.add(Integer.parseInt(init));
        for(int i=1;i<input.length();i++)
        {
            try {
                int nexLen=i+1;
                if(nexLen<input.length()) {
                    String value = input.charAt(i) + "" + input.charAt(i + 1);
                    list.add(Integer.parseInt(value));
                }

            }catch (StringIndexOutOfBoundsException indexOut)
            {

            }

        }
       Collections.sort(list);
        int max=list.get(list.size()-1);
        return max;
    }
    int count=0;
    public int solution2(String s)
    {
        boolean rerun=false;
//        s="BAAABAB";
        //s="BBBABAA";
        for(int i=0;i<s.length();i++) {

            if (i+1<s.length()) {
                if(s.charAt(i)>s.charAt(i+1))
                {
                    s=s.replaceFirst(s.charAt(i)+"","");
                    count++;
                    rerun=true;
                    break;
                }else
                {
rerun=false;
                }
               /* if (s.charAt(i) <= s.charAt(i + 1)) {

                } else {
                    s.replace(s.charAt(i + 1) + "", "");
                    count++;
                }*/
            }
//            Set<Integer> s=new HashSet<>();
        }
        if(rerun) {
            solution2(s);
        }
        System.out.println(s);
        return count;
    }

    public int solution3(int N)
    {
        //14=>1+4=>5
        // 14+5=>19=10

        int sum=getSumOfDigits(N);//N=14
        int d=sum+N;//=>14+5=>19

        if(getSumOfDigits(d)==2*sum)
        {
            return d;
        }
        while (true)
        {

//            sum=getSumOfDigits(d);


         if(getSumOfDigits(d)==2*sum)
         {
             break;
         }else {
             d=N+d;
         }
        }
        System.out.println("######-"+d);
       return d;
       // return 0;
    }

    private int getSumOfDigits(int n) {
        int sum=0;
        for (sum = 0; n > 0; sum += n % 10,
                n /= 10);
        return sum;
    }
}
