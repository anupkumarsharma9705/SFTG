import java.util.*;
class SecondLargest{
	public static void main(String[] args){
		// --- Measure start time and memory ---
        	long startTime = System.nanoTime();
        	long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        	
		Scanner sc = new Scanner(System.in);
		int [] arr = {0,15,9,3,-3,4};
		int n;
		do{
			System.out.println("Enter your choice : \n1.normal method (LInear Scan) \n2.pairwise Comparision");
			n=sc.nextInt();
			switch (n){
			case 1 -> {System.out.println(SortAndPickSecond(arr)); System.out.print("Sort and Pick Method. So the Second Largest Number is "); break; }
			case 2 -> {System.out.println(SinglePass(arr)); System.out.print("Single Pass method. So the Second Largest Number is "); break; }
			default -> { System.out.println("Entered wrong Input"); break; }
			}
		}while(n>=3 || n==0);
		// --- Measure end time and memory ---
		long endTime = System.nanoTime();
		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

		long timeTaken = endTime - startTime;
		long memoryUsed = endMemory - startMemory;

		// Print results
		System.out.println("Time Taken: " + timeTaken + " ns");
		System.out.println("Memory Used: " + memoryUsed + " bytes");
	}
	public static Integer SortAndPickSecond(int [] arr){
		Arrays.sort(arr);
		int n=arr.length;
		int largest = arr[n-1];
		for(int i=n-2;i>=0;i--){
			if(arr[i]<largest) return arr[i];
		}
		return null;
	}
	public static Integer SinglePass(int [] arr){
		int largest= arr[0],seclar =Integer.MIN_VALUE;
		for(int i=1;i<arr.length;i++){
			if(arr[i]>largest){
				seclar = largest;
				largest =arr[i];
			}else if(arr[i]>seclar && arr[i]!=largest) seclar = arr[i];
		}
		return seclar;
	}
}
