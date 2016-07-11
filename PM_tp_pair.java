import java.util.*;


public class PM_tp_pair 
{
	private static int[] pre_fn;  // prefix table
	private static String pattern;
	
	//Step1-Encoding text and pattern
	private static String patt_encode(int[] patt)
	{
		String temp = new String();
		for(int i=0;i<(patt.length)-1;i++)
		{
			if(patt[i]<patt[i+1])
				temp +=  1;
			else
				temp +=  0;
		}
		return temp;
	}
	private static String text_encode(int[] txt)
	{
		String temp = new String();
		for(int i=0;i<(txt.length)-1;i++)
		{
			if(txt[i]<txt[i+1])
				temp +=  1;
			else
				temp +=  0;
		}
		return temp;	
	}
	
	//Step2-Filtration.
	//Getting all the matches using KMP
    //computing the prefix table
	private static void computePrefx(String patt) 
	{
		pattern = patt;
		int m = pattern.length();
	    int[] pre_fn = new int[m];
		pre_fn[0] = 0;
		int k = 0; 
		for (int q = 1; q < m; q++) 
		{
			while (k > 0 && pattern.charAt(q) != pattern.charAt(k))
				k = pre_fn[k-1];
			if (pattern.charAt(q) == pattern.charAt(k))
				k++;
			assert 0 <= k && k < q;
			pre_fn[q] = k;
		}
	}
	
	//check if text has a match
	private static int[] search(String text)
	{
		int N = text.length();
		int M=  pattern.length();
		int i = 0;  // index for text[]
		int j  = 0;  // index for pat[]
		int[] result1 = new int[N];
		int[] result2;
		int k=0;  //index for result2[]	    
		while (i < N)
		{
			if (pattern.charAt(j) == text.charAt(i))
			{
				j++;
		        i++;
		    }
			
			if (j == M)
			{
				result1[k]=(i-j);
		        k++;
		        j = pre_fn[j-1];
			}
			
			// mismatch after j matches
		    else if (i < N && pattern.charAt(j) != text.charAt(i))
			{
				// Do not match lps[0..lps[j-1]] characters,
		        // they will match anyway
		        if (j != 0)
		         j = pre_fn[j-1];
		        else
		         i = i+1;
			}
		}
		
		if(k!=0)
		{
			//System.out.println("Possible match");
		    result2 = new int[k];
		    for(i=0;i<k;i++)
				result2[i]=result1[i];
		}
		else
		{
			//System.out.println("No match");
			result2 = new int[k];
			for(i=0;i<k;i++)
				result2[i]=0;
		}
		return result2;
	}
	
	//Result from KMP
	public static int[] MY_KMP_res(String text, String pat)
    {
		/** pre construct prefix array for a pattern **/
		pre_fn = new int[pat.length()];
        computePrefx(pat);	
        int[] res = search(text);
    	return res;
    }	
	
	//Verification
	//Step3-Preprocess the pattern
	//Part-a: Sort the pattern using radix sort
    //A utility function to get maximum value in arr[]
	//Used in radix sort
    static int getMax(int arr[], int n)
    {
        int mx = arr[0];
		for (int i = 1; i < n; i++)
			if (arr[i] > mx)
				mx = arr[i];
		return mx;
    }
    
    //A function to do counting sort of arr[] according to
    //the digit represented by exp.
	//Used in the inner loop of radix sort
    static void countSort(int arr[], int n, int exp)
    {
        int output[] = new int[n]; // output array
        int i;
        int count[] = new int[10];
        Arrays.fill(count,0);
        for (i = 0; i < n; i++)
            count[ (arr[i]/exp)%10 ]++;
        // Change count[i] so that count[i] now contains
        // actual position of this digit in output[]
        for (i = 1; i < 10; i++)
            count[i] += count[i - 1];
        // Build the output array
        for (i = n - 1; i >= 0; i--)
        {
            output[count[ (arr[i]/exp)%10 ] - 1] = arr[i];
            count[ (arr[i]/exp)%10 ]--;
        }
        // Copy the output array to arr[], so that arr[] now
        // contains sorted numbers according to curent digit
        for (i = 0; i < n; i++)
            arr[i] = output[i];
    }
       
	// Radix Sort
    // The main function to that sorts arr[] of size n using    
    static void radixsort(int arr[], int n)
    {
        // Find the maximum number to know number of digits
        int m = getMax(arr, n); 
        // Do counting sort for every digit. Note that instead
        // of passing digit number, exp is passed. exp is 10^i
        // where i is current digit number
        //for (int exp = 1; m/exp > 0; exp *= 10)
			int exp = 1;
		for (int i = 1; i <=4 && m/exp > 0 ; i++)
		{
            countSort(arr, n, exp);
			exp *= 10;
		}
    }
    
	//Part-b: Generate the auxiliary table
	private static int[] aux_tab(int[] orig, int[] srtd, int n) 
	{
		int[] aux = new int[n];
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
				if(srtd[i]==orig[j])
					aux[i]=j;
		}
		return aux;
	}
	
	//Pact-c: Generate the binary encoding
	private static int[] bin_encode(int[] aux, int[] pattern, int n) 
	{
		int[] bin=new int[n-1];
		for(int i=0;i<n-1;i++)
		{
			if (pattern[aux[i]] == pattern[aux[i+1]])
				bin[i]=1;
			else
				bin[i]=0;
		}
		return bin;
	}
	
	//Step4-(Last step) Validating the match
	private static void test(int[] txt, int[] aux, int[] bin_e, int[] res) 
	{
		int t1,t2,j,flag;
		int[] e=bin_e;
		int matches=res.length;
		for(int k=0;k<res.length;k++)
		{
			j=res[k];
			System.out.println("j:" + j);
			flag=1;
			for(int i=0;i<aux.length-1;i++)
			{
				t1=txt[(j)+(aux[i])];
				t2=txt[(j)+(aux[i+1])];
				System.out.println("t1:" + t1 + " t2:" +t2);
				if ((t1>t2) || ((t1==t2) && (e[i]==0)) || ((t1<t2) && (e[i]==1)))
				{   
					System.out.println("t1:" + t1 + " t2:" +t2);
					System.out.println("Pattern mismatch");
					flag=0;
					break;
				}
				else
				{
					System.out.println("Pattern matched");
					//flag=1;
					//break;
				}
			}
			if(flag==0)
			{
				matches--;
				res[k]=-1;
			}
		}
		System.out.println("Number of matches:"+matches);
		for(int l=0;l<res.length;l++)
		{
			if (res[l] != -1)
			{
				System.out.print(res[l]+"(");
				int p=0;
				int j1=res[l];
				System.out.print(txt[j1]);
				j1++;
				while(p<aux.length-1)
				{
					System.out.print(","+txt[j1]);
					j1++;
					p++;
				}
				System.out.print(")"+",");
			}
		}
	}
	
    // A utility function to print an array
    static void print(int arr[], int n)
    {
        for (int i=0; i<n; i++)
            System.out.println(arr[i]);
    }
    
	//Main function
	public static void main(String[] args)
	{
		//random text generation
		/*Random r = new Random();
		final int m = 1000;
		int[] text = new int[m];
		for (int i = 0; i < m; i++)
			text[i] = r.nextInt(Integer.MAX_VALUE);*/
		int[] text = {22,85,79,24,42,27,62,40,32,47,69,55,25,22,85,79,24,42,27,62,62,32,47,69,55,25};
		int m =text.length;
		//random pattern generation
		/*final int n = 10;
		int[] pattern = new int[n];
		for (int i = 0; i < n; i++)
			pattern[i] = r.nextInt(Integer.MAX_VALUE);*/
		int[] pattern = {10,22,15,30,20,18,27};
		int n =pattern.length;		
		int[] patt_sort = new int[n];
		for (int i=0; i<n; i++)
			 patt_sort[i]=pattern[i];
		int[] aux = new int[n];
		int[] bin_e=new int[n-1];
		radixsort(patt_sort,n);	
		aux=aux_tab(pattern,patt_sort,n);
		bin_e=bin_encode(aux,pattern,n);
		System.out.println("Binary vector:");
		/*for(int i=0;i<bin_e.length;i++)
			System.out.print(bin_e[i]+",");*/
		String patt_enc = patt_encode(pattern);
		System.out.println("\nPattern:");
		for(int i=0;i<n;i++)
			System.out.print(pattern[i]+",");
		System.out.println("\nPattern Encoding:" + patt_enc);	
		String txt_enc = text_encode(text);
		System.out.println("Text:");
		for(int i=0;i<m;i++)
			System.out.print(text[i]+",");
		System.out.println("\nText Encoding:" + txt_enc);			
		int[] kmp = MY_KMP_res(txt_enc, patt_enc);
		//long start = System.currentTimeMillis();
		if (kmp.length==0)
			System.out.println("0");
		else
			test(text,aux,bin_e,kmp);
		/*long end = System.currentTimeMillis();
		System.out.println("\nTime taken: "+(end-start));*/
	}	
}
