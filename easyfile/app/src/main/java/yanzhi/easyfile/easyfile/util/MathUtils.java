package yanzhi.easyfile.easyfile.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String hashKeyForDisk(String key) {
        String cacheKey;
        key.hashCode();
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }
}
