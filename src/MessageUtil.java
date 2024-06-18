import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class MessageUtil {

    public static String encryptMessage(SecretKey aesKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        byte[] combined = new byte[iv.length + encryptedMessage.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedMessage, 0, combined, iv.length, encryptedMessage.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptMessage(SecretKey aesKey, String encryptedMessage) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedMessage);
        byte[] iv = new byte[16];
        byte[] messageBytes = new byte[combined.length - iv.length];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, messageBytes, 0, messageBytes.length);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
        byte[] decryptedMessage = cipher.doFinal(messageBytes);
        return new String(decryptedMessage);
    }
}
