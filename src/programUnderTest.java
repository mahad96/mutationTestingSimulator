public class programUnderTest {

    public static int programUnderTest(int key, int [] elemArray){
        int bottom = 0;
        int top = elemArray.length - 1;
        int mid;
        int result = -1;
        while (bottom <= top){
            mid = (top + bottom)/2;
            if (elemArray [mid] == key){
                result = mid;

            }
            else {
                if (elemArray[mid]<key){
                    bottom = mid+1;
                }
                else top = mid -1;
            }
            bottom++;
        }
        return result;
    }
}
