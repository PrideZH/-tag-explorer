package cn.pridezh.tagexplore.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author PrideZH
 */
public class XORUtils {

    private static final int KEY_LEN = 512;

    /**
     * 对key进行填充 填充1至512位
     * @param key
     * @return
     */
    private static byte[] fillForKey(String key) {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = new byte[KEY_LEN];
        Arrays.fill(keyBytes, (byte) 1);
        System.arraycopy(bytes, 0, keyBytes, 0, bytes.length);
        return keyBytes;
    }

    public static byte[] execute(byte[] content, String key) {
        return execute(content, key, true);
    }

    public static byte[] execute(byte[] content, String key, boolean slice) {
        if (slice && content.length % KEY_LEN != 0) {
            throw new RuntimeException("数组长度必须是" + KEY_LEN + "的整数倍");
        }

        byte[] bytes = fillForKey(key);
        byte[] result = new byte[content.length];
        for (int i = 0; i < content.length; i++) {
            result[i] = (byte) (content[i] ^ bytes[i % KEY_LEN]);
        }
        return result;
    }

}
