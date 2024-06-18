//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.util.Base64;
//
//public class SecureMessagingApp {
//
//    public static void main(String[] args) throws Exception {
//        // Simulate sending multiple messages
//        String[] messages = {
//                "Hello, Bob! This is a secure message.",
//                "This is another message with a different key.",
//                "Every message will use a new set of keys."
//        };
//
//        for (String originalMessage : messages) {
//            // Generate RSA key pair for each message
//            KeyPair keyPair = generateRSAKeyPair();
//            PublicKey publicKey = keyPair.getPublic();
//            PrivateKey privateKey = keyPair.getPrivate();
//
//            // Generate AES key for each message
//            SecretKey aesKey = generateAESKey();
//            byte[] aesKeyBytes = aesKey.getEncoded();
//
//            // Print keys
//            System.out.println("\n--- New Message ---");
//            System.out.println("Original Message: " + originalMessage);
//            System.out.println("Public Key (Base64): " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
//            System.out.println("Private Key (Base64): " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
//            System.out.println("AES Key (Base64): " + Base64.getEncoder().encodeToString(aesKeyBytes));
//
//            // Encrypt the message with AES
//            byte[] encryptedMessage = encryptWithAES(originalMessage, aesKey);
//
//            // Print encrypted message
//            System.out.println("Encrypted Message (Base64): " + Base64.getEncoder().encodeToString(encryptedMessage));
//
//            // Encrypt AES key with RSA public key
//            byte[] encryptedAESKey = encryptWithRSA(aesKeyBytes, publicKey);
//
//            // Print encrypted AES key
//            System.out.println("Encrypted AES Key (Base64): " + Base64.getEncoder().encodeToString(encryptedAESKey));
//
//            // Simulate message sending and receiving
//            // In practice, this would involve sending both encryptedMessage and encryptedAESKey to the recipient
//
//            // Decrypt AES key with RSA private key
//            byte[] decryptedAESKey = decryptWithRSA(encryptedAESKey, privateKey);
//
//            // Decrypt the message with AES
//            String decryptedMessage = decryptWithAES(encryptedMessage, decryptedAESKey);
//
//            // Print decrypted message
//            System.out.println("Decrypted Message: " + decryptedMessage);
//        }
//    }
//
//    private static KeyPair generateRSAKeyPair() throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048); // You can adjust the key size as needed
//        return keyPairGenerator.generateKeyPair();
//    }
//
//    private static SecretKey generateAESKey() throws Exception {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(256); // You can adjust the key size as needed
//        return keyGenerator.generateKey();
//    }
//
//    private static byte[] encryptWithAES(String message, SecretKey aesKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
//        return cipher.doFinal(message.getBytes());
//    }
//
//    private static String decryptWithAES(byte[] encryptedMessage, byte[] aesKey) throws Exception {
//        SecretKey secretKey = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessage);
//        return new String(decryptedMessageBytes);
//    }
//
//    private static byte[] encryptWithRSA(byte[] data, PublicKey publicKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        return cipher.doFinal(data);
//    }
//
//    private static byte[] decryptWithRSA(byte[] encryptedData, PrivateKey privateKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        return cipher.doFinal(encryptedData);
//    }
//}

//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.util.Base64;
//
//public class SecureMessagingUtil {
//
//    public static KeyPair generateRSAKeyPair() throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048); // You can adjust the key size as needed
//        return keyPairGenerator.generateKeyPair();
//    }
//
//    public static SecretKey generateAESKey() throws Exception {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(256); // You can adjust the key size as needed
//        return keyGenerator.generateKey();
//    }
//
//    public static byte[] encryptWithAES(String message, SecretKey aesKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
//        return cipher.doFinal(message.getBytes());
//    }
//
//    public static String decryptWithAES(byte[] encryptedMessage, byte[] aesKey) throws Exception {
//        SecretKey secretKey = new SecretKeySpec(aesKey, 0, aesKey.length, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessage);
//        return new String(decryptedMessageBytes);
//    }
//
//    public static byte[] encryptWithRSA(byte[] data, PublicKey publicKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        return cipher.doFinal(data);
//    }
//
//    public static byte[] decryptWithRSA(byte[] encryptedData, PrivateKey privateKey) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA");
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        return cipher.doFinal(encryptedData);
//    }
//
//    public static void printKeysAndMessages(String originalMessage, PublicKey publicKey, PrivateKey privateKey,
//                                            SecretKey aesKey, byte[] encryptedMessage, byte[] encryptedAESKey,
//                                            String decryptedMessage) {
//        System.out.println("\n--- New Message ---");
//        System.out.println("Original Message: " + originalMessage);
//        System.out.println("Public Key (Base64): " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
//        System.out.println("Private Key (Base64): " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
//        System.out.println("AES Key (Base64): " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
//        System.out.println("Encrypted Message (Base64): " + Base64.getEncoder().encodeToString(encryptedMessage));
//        System.out.println("Encrypted AES Key (Base64): " + Base64.getEncoder().encodeToString(encryptedAESKey));
//        System.out.println("Decrypted Message: " + decryptedMessage);
//    }
//}


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecureMessagingUtil {

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static byte[] encryptWithAES(String message, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(message.getBytes());
    }

    public static String decryptWithAES(byte[] encryptedMessage, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return new String(cipher.doFinal(encryptedMessage));
    }

    public static byte[] encryptWithRSA(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptWithRSA(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    public static String encodeKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey decodePublicKey(String encodedKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    public static String encodeBytes(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeBytes(String data) {
        return Base64.getDecoder().decode(data);
    }
}
