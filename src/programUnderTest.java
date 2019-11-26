public class programUnderTest {

    public static double programUnderTest(int array[], int arraySize, int MIN, int MAX){
        int index, ti, tv, sum;
		double average;
		index = 0;
		ti = 0;
		tv = 0;
		sum = 0;
		while (ti < arraySize && array[index] != -999) {
			ti++;
			if (array[index] >= MIN && array[index] <= MAX) {
				tv++;
				sum += array[index];
			}
			index++;
		}
		if (tv > 0)
			average = (double) sum / tv;
		else
			average = (double) -999;
		return average;
    }
}
