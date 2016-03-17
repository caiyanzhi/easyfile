package yanzhi.easyfile.easyfile.util;

/**
 * @desc Created by yanzhi on 2016-03-18.
 */
public class HelperUtil {
    public static void validateNull(Object object) {
        if(object == null) {
            throw new IllegalArgumentException("object is null");
        }
    }
}
