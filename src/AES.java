import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int T_LEN = 128;
    private Cipher encryptionCipher;

    // Initialize the fixed key here
    private static final byte[] FIXED_KEY = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);

    public void init() throws GeneralSecurityException {
        key = new SecretKeySpec(FIXED_KEY, "AES");
    }

    public String encrypt(String message) throws GeneralSecurityException {
        byte[] messageInBytes = message.getBytes();
        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(T_LEN, iv);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        byte[] output = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, output, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, output, iv.length, encryptedBytes.length);
        return encode(output);
    }

    public String decrypt(String encryptedMessage) throws GeneralSecurityException {
        byte[] encryptedDataWithIv = decode(encryptedMessage);
        byte[] iv = new byte[12];
        System.arraycopy(encryptedDataWithIv, 0, iv, 0, iv.length);
        byte[] cipherText = new byte[encryptedDataWithIv.length - iv.length];
        System.arraycopy(encryptedDataWithIv, iv.length, cipherText, 0, cipherText.length);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, iv);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(cipherText);
        return new String(decryptedBytes);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

}
