package yanzhi.easyfile.easyfile.util;

/**
 * @desc Created by yanzhi on 2016-02-22.
 */
public class MathUtils {
    /**
     * 获取value的数字hash值
     * @param value
     * @return
     */
    public static String hashValue(String value) {
        return String.valueOf(getPositiveHashInt(value));
    }

    /**
     * 非负32位整数的映射
     * @param value
     * @return
     */
    public static int getPositiveHashInt(String value){
        return  hashInt(value) & 0x7FFFFFFF;
    }

    /**
     * 32位整数的映射
     * @param value
     * @return
     */
    public static int hashInt(String value) {
        int seed = 131; // 31 131 1313 13131 131313 etc..  BKDRHash
        int hash=0;
        for (int i = 0; i< value.length(); i++) {
            hash = (hash * seed) + value.charAt(i);
        }
        return hash;
    }
}
