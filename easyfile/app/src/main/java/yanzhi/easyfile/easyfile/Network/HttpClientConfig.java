package yanzhi.easyfile.easyfile.Network;

/**
 * @desc Created by yanzhi on 2016-01-28.
 */
public class HttpClientConfig {
    /**
     * 发送的bufferlen
     */
    public static final Integer SEND_BUFF_LEN_INTEGER = 1024 * 32; // 32K
    /**
     * 接收的bufferlen
     */
    public static final Integer RECEIVE_BUFF_LEN_INTEGER = 1024 * 32;// 32K

    /**
     * 接受的文件最大的字符数
     */
    public static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 3; // 300MB

    public static final String CHAR_SET = "UTF-8";
}
