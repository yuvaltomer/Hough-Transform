import java.util.Random;

import javax.annotation.processing.SupportedSourceVersion;

import Plotter.Plotter;


public class Sorting{

	final static int SELECT_VS_QUICK_LENGTH = 12;
	final static int MERGE_VS_QUICK_LENGTH = 15;
	final static int COUNTING_VS_QUICK_LENGTH = 16;
	final static int BUBBLE_VS_MERGE_LENGTH = 12;
	final static int MERGE_VS_QUICK_SORTED_LENGTH =11;
	final static double T = 600.0;
	
	/**
	 * Sorts a given array using the quick sort algorithm.
	 * At each stage the pivot is chosen to be the rightmost element of the subarray.
	 * 
	 * Should run in average complexity of O(nlog(n)), and worst case complexity of O(n^2)
	 * 
	 * @param arr - the array to be sorted
	 */
	public static void quickSort(double[] arr){
		quickSort(arr, 0, arr.length-1);
	}

	public static void quickSort(double[] arr, int first, int last) {
		if(first < last - 1) {
			int pivot = partition(arr, first, last);
			if(pivot == first) {
				quickSort(arr, pivot + 1, last);
			}
			else if(pivot == last) {
				quickSort(arr, first, pivot - 1);
			}
			else {
				quickSort(arr, first, pivot - 1);
				quickSort(arr, pivot + 1, last);
			}
		}
		else { // check!!!!
			if(arr[last] < arr[first]) {
				double temp = arr[last];
				arr[last] = arr[first];
				arr[first] = temp;
			}
		}
	}

	public static int partition(double[] arr, int first, int last) {
		double pivot = arr[last];
		int j = last;
		int i = first - 1;
		while(true) {
			do {
				j--;
			}
			while(j >= first && (arr[j] >= pivot));
			do {
				i++;
			}
			while(i <= last && (arr[i] < pivot));
			if(i < j) {
				double temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			}
			else {
				double temp = arr[j+1];
				arr[j+1] = arr[last];
				arr[last] = temp;
				return j + 1;
			}
		}
	}



	/**
	 * Given an array arr and an index i returns the the i'th order statstics in arr. 
	 * In other words, it returns the element with rank i in the array arr.
	 * 
	 * At each stage the pivot is chosen to be the rightmost element of the subarray.
	 * 
	 * Should run in average complexity of O(n), and worst case complexity of O(n^2)
	 * 
	 **/
	public static double QuickSelect(double[] arr, int i){
		if(i < 1 || i > arr.length)    throw new IllegalArgumentException("Index out of array bounds");
		return QuickSelect(arr, 0, arr.length - 1, i);
	}

	public static double QuickSelect(double[] arr, int first, int last, int i){
		if(first == last)	return arr[first];
		int q = partition(arr, first, last);
		int pivot = q - first + 1;
		if(i == pivot)	return arr[q];
		if(i < pivot)	return QuickSelect(arr, first, q - 1, i);
		return QuickSelect(arr, q + 1, last, i - pivot);
	}




	/**
	 * Sorts a given array using the merge sort algorithm.
	 * 
	 * Should run in complexity O(nlog(n)) in the worst case.
	 * 
	 * @param arr - the array to be sorted
	 */
	public static void mergeSort(double[] arr){
			mergeSort(arr, 0, arr.length-1);
	}

	public static void mergeSort(double[] arr, int first, int last) {
		if(first < last) {
			int mid = (first + last) / 2;
			mergeSort(arr, first, mid);
			mergeSort(arr, mid + 1, last);
			merge(arr, first, mid, last);
		}
	}

	public static void merge(double[] arr, int first, int mid, int last) {
		int l1 = mid - first + 1;
		int l2 = last - mid;
		double[] left = new double[l1];
		double[] right = new double[l2];
		int j = 0;
		for(int i = first; i < l1 + first; i++) {
			left[j++] = arr[i];
		}
		j = 0;
		for (int i = l1 + first; i <= last; i++) {
			right[j++] = arr[i];
		}
		int i = 0;
		j = 0;
		for(int k = first; k <= last; k++) {
			if(i >= l1) {
				arr[k] = right[j];
				j++;
			}
			else if(j >= l2) {
				arr[k] = left[i];
				i++;
			}
			else if(left[i] <= right[j]) {
				arr[k] = left[i];
				i++;
			}
			else {
				arr[k] = right[j];
				j++;
			}
		}
	}


	/**
	 * Sorts a given array using bubble sort.
	 * 
	 * The algorithm should run in complexity O(n^2).
	 * 
	 * @param arr - the array to be sorted
	 */
	public static void bubbleSort(double[] arr){
			for(int i = 0; i < arr.length - 1; i++) {
				for(int j = 1; j < arr.length - i; j++) {
					if(arr[j-1] > arr[j]) {
						double temp = arr[j-1];
						arr[j-1] = arr[j];
						arr[j] = temp;
					}
				}
			}
	}


	/**
	 * Sorts a given array, using the counting sort algorithm.
	 * You may assume that all elements in the array are between 0 and k (not including k).
	 * 
	 * Should run in complexity O(n + k) in the worst case.
	 * 
	 * @param arr - an array with possitive integers
	 * @param k - an upper bound for the values of all elements in the array.
	 */
	public static void countingSort(int[] arr, int k){
		int[] count = new int[k];
		int[] output = new int[arr.length];
		for(int i = 0; i < arr.length; i++) {
			count[arr[i]]++;
		}
		for(int i = 1; i < count.length; i++) {
			count[i] += count[i-1];
		}
		for(int i = arr.length - 1; i >= 0; i--) {
			output[count[arr[i]]-1] = arr[i];
			count[arr[i]]--;
		}
		for(int i = 0; i < arr.length; i++) {
			arr[i] = output[i];
		}
	}

	public static void main(String[] args) {

		countingVsQuick();
		mergeVsQuick();
		mergeVsQuickOnSortedArray();
		mergeVsBubble();
		QuickSelectVsQuickSort();
		
	}
	


	private static void countingVsQuick() {
		double[] quickTimes = new double[COUNTING_VS_QUICK_LENGTH];
		double[] countingTimes = new double[COUNTING_VS_QUICK_LENGTH];
		long startTime, endTime;
		Random r = new Random();
		for (int i = 0; i < COUNTING_VS_QUICK_LENGTH; i++) {
			long sumQuick = 0;
			long sumCounting = 0;
			for(int k = 0; k < T; k++){
				int size = (int)Math.pow(2, i);
				double[] a = new double[size];
				int[] b = new int[size];
				for (int j = 0; j < a.length; j++) {
					b[j] = r.nextInt(size);
					a[j] = b[j];
				}
				startTime = System.currentTimeMillis();
				quickSort(a);
				endTime = System.currentTimeMillis();
				sumQuick += endTime - startTime;
				startTime = System.currentTimeMillis();
				countingSort(b, size);
				endTime = System.currentTimeMillis();
				sumCounting += endTime - startTime;
			}
			quickTimes[i] = sumQuick/T;
			countingTimes[i] = sumCounting/T;
		}
		Plotter.plot("Counting sort on arrays with elements < n", countingTimes, "Quick sort on arrays with elements < n", quickTimes);
		
	}


	
	/**
	 * Compares the merge sort algorithm against quick sort on random arrays
	 */
	public static void mergeVsQuick(){
		double[] quickTimes = new double[MERGE_VS_QUICK_LENGTH];
		double[] mergeTimes = new double[MERGE_VS_QUICK_LENGTH];
		long startTime, endTime;
		Random r = new Random();
		for (int i = 0; i < MERGE_VS_QUICK_LENGTH; i++) {
			long sumQuick = 0;
			long sumMerge = 0;
			for (int k = 0; k < T; k++) {
				int size = (int)Math.pow(2, i);
				double[] a = new double[size];
				double[] b = new double[size];
				for (int j = 0; j < a.length; j++) {
					a[j] = r.nextGaussian() * 5000;
					b[j] = a[j];
				}
				startTime = System.currentTimeMillis();
				quickSort(a);
				endTime = System.currentTimeMillis();
				sumQuick += endTime - startTime;
				startTime = System.currentTimeMillis();
				mergeSort(b);
				endTime = System.currentTimeMillis();
				sumMerge += endTime - startTime;
			}
			quickTimes[i] = sumQuick/T;
			mergeTimes[i] = sumMerge/T;
		}
		Plotter.plot("quick sort on random array", quickTimes, "merge sort on random array", mergeTimes);
	}
	
	/**
	 * Compares the merge sort algorithm against quick sort on pre-sorted arrays
	 */
	public static void mergeVsQuickOnSortedArray(){
		double[] quickTimes = new double[MERGE_VS_QUICK_SORTED_LENGTH];
		double[] mergeTimes = new double[MERGE_VS_QUICK_SORTED_LENGTH];
		long startTime, endTime;
		for (int i = 0; i < MERGE_VS_QUICK_SORTED_LENGTH; i++) {
			long sumQuick = 0;
			long sumMerge = 0;
			for (int k = 0; k < T; k++) {
				int size = (int)Math.pow(2, i);
				double[] a = new double[size];
				double[] b = new double[size];
				for (int j = 0; j < a.length; j++) {
					a[j] = j;
					b[j] = j;
				}
				startTime = System.currentTimeMillis();
				quickSort(a);
				endTime = System.currentTimeMillis();
				sumQuick += endTime - startTime;
				startTime = System.currentTimeMillis();
				mergeSort(b);
				endTime = System.currentTimeMillis();
				sumMerge  += endTime - startTime;
			}
			quickTimes[i] = sumQuick/T;
			mergeTimes[i] = sumMerge/T;
		}
		Plotter.plot("quick sort on sorted array", quickTimes, "merge sort on sorted array", mergeTimes);
	}
	/**
	 * Compares merge sort and bubble sort on random arrays
	 */
	public static void mergeVsBubble(){
		double[] mergeTimes = new double[BUBBLE_VS_MERGE_LENGTH];
		double[] bubbleTimes = new double[BUBBLE_VS_MERGE_LENGTH];
		long startTime, endTime;
		Random r = new Random();
		for (int i = 0; i < BUBBLE_VS_MERGE_LENGTH; i++) {
			long sumMerge = 0;
			long sumBubble = 0;
			for(int k = 0; k < T; k++){
				int size = (int)Math.pow(2, i);
				double[] a = new double[size];
				double[] b = new double[size];
				for (int j = 0; j < a.length; j++) {
					a[j] = r.nextGaussian() * 5000;
					b[j] = a[j];
				}
				startTime = System.currentTimeMillis();
				mergeSort(a);
				endTime = System.currentTimeMillis();
				sumMerge += endTime - startTime;
				startTime = System.currentTimeMillis();
				bubbleSort(a);
				endTime = System.currentTimeMillis();
				sumBubble += endTime - startTime;
			}
			mergeTimes[i] = sumMerge/T;
			bubbleTimes[i] = sumBubble/T;
		}
		Plotter.plot("merge sort on random array", mergeTimes, "bubble sort on random array", bubbleTimes);
	}
	
	



	/**
	 * Compares the quick select algorithm with a random rank, and the quick sort algorithm.
	 */
	public static void QuickSelectVsQuickSort(){
		double[] QsortTimes = new double[SELECT_VS_QUICK_LENGTH];
		double[] QselectTimes = new double[SELECT_VS_QUICK_LENGTH];
		Random r = new Random();
		long startTime, endTime;
		for (int i = 0; i < SELECT_VS_QUICK_LENGTH; i++) {
			long sumQsort = 0;
			long sumQselect = 0;
			for (int k = 0; k < T; k++) {
				int size = (int)Math.pow(2, i);
				double[] a = new double[size];
				double[] b = new double[size];
				for (int j = 0; j < a.length; j++) {
					a[j] = r.nextGaussian() * 5000;
					b[j] = a[j];
				}
				startTime = System.currentTimeMillis();
				quickSort(a);
				endTime = System.currentTimeMillis();
				sumQsort += endTime - startTime;
				startTime = System.currentTimeMillis();
				QuickSelect(b, r.nextInt(size)+1);
				endTime = System.currentTimeMillis();
				sumQselect += endTime - startTime;
			}
			QsortTimes[i] = sumQsort/T;
			QselectTimes[i] = sumQselect/T;
		}
		Plotter.plot("quick sort with an arbitrary pivot", QsortTimes, "quick select with an arbitrary pivot, and a random rank", QselectTimes);
	}
	

}
