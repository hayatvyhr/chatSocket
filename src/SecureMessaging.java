import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class SecureMessaging {

    private static final String RSA = "RSA";
    private static final String AES = "AES";

    // Method to generate AES Key
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(256); // You can use 128 or 192 bits as well
        return keyGen.generateKey();
    }


    // Method to encrypt AES key with RSA
    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKeyBytes = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKeyBytes);
    }


    // Method to decrypt AES key with RSA
    public SecretKey decryptAESKeyWithRSA(String encryptedAESKey, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decodedKey = Base64.getDecoder().decode(encryptedAESKey);
            byte[] decryptedKeyBytes = cipher.doFinal(decodedKey);
            return new SecretKeySpec(decryptedKeyBytes, 0, decryptedKeyBytes.length, "AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Method to decrypt message with AES
    public static String encryptMessageWithAES(String message, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decryptMessageWithAES(String encryptedMessage, SecretKey aesKey) {
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Implement this method to get your private RSA key
    public PrivateKey getPrivateKey() {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to get the recipient's public key (stubbed)
    public PublicKey getRecipientPublicKey(String recipient) {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}