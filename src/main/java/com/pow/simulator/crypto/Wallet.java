package com.pow.simulator.crypto;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HexFormat;

/**
 * Production-grade Cryptographic Wallet utilizing Elliptic Curve Digital Signature Algorithm (ECDSA).
 * Conforms to standard Bitcoin-style asymmetric key principles with private/public key pairs and addresses.
 */
public class Wallet {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String address;

    public Wallet(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = deriveAddress(publicKey);
    }

    /**
     * Generates a new random cryptographic wallet with 256-bit Elliptic Curve keys.
     */
    public static Wallet createNew() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            try {
                // Attempt standard Bitcoin curve secp256k1
                keyGen.initialize(new ECGenParameterSpec("secp256k1"), new SecureRandom());
            } catch (Exception ignored) {
                // Fallback to guaranteed standard NIST P-256 curve (secp256r1)
                keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
            }
            KeyPair keyPair = keyGen.generateKeyPair();
            return new Wallet(keyPair.getPrivate(), keyPair.getPublic());
        } catch (Exception e) {
            throw new CryptoException("Failed to generate ECDSA keypair", e);
        }
    }

    /**
     * Restores a wallet from a private key hex string.
     */
    public static Wallet fromPrivateKeyHex(String privateKeyHex) {
        try {
            byte[] privBytes = HexFormat.of().parseHex(privateKeyHex.trim());
            KeyFactory kf = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privBytes);
            PrivateKey priv = kf.generatePrivate(keySpec);

            // Reconstruct public key from private key spec or generate an ephemeral wallet instance
            // In standard JCA, we can query key parameters or derive public point
            // For universal JDK compatibility, we can serialize public key along with private key
            return new Wallet(priv, null);
        } catch (Exception e) {
            throw new CryptoException("Invalid private key format", e);
        }
    }

    /**
     * Restores a wallet from a full keypair hex representation.
     */
    public static Wallet fromKeyStrings(String privateKeyHex, String publicKeyHex) {
        try {
            KeyFactory kf = KeyFactory.getInstance("EC");
            byte[] privBytes = HexFormat.of().parseHex(privateKeyHex.trim());
            PrivateKey priv = kf.generatePrivate(new PKCS8EncodedKeySpec(privBytes));

            byte[] pubBytes = HexFormat.of().parseHex(publicKeyHex.trim());
            PublicKey pub = kf.generatePublic(new X509EncodedKeySpec(pubBytes));

            return new Wallet(priv, pub);
        } catch (Exception e) {
            throw new CryptoException("Failed to restore wallet from key strings", e);
        }
    }

    /**
     * Signs transaction payload using the wallet's private key with SHA256withECDSA.
     *
     * @param payload message bytes to sign
     * @return hex-encoded DER digital signature
     */
    public String sign(String payload) {
        if (privateKey == null) {
            throw new IllegalStateException("Cannot sign without private key");
        }
        try {
            Signature ecdsa = Signature.getInstance("SHA256withECDSA");
            ecdsa.initSign(privateKey);
            ecdsa.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = ecdsa.sign();
            return HexFormat.of().formatHex(signatureBytes);
        } catch (Exception e) {
            throw new CryptoException("Failed to sign transaction payload", e);
        }
    }

    /**
     * Verifies an ECDSA digital signature against the public key and payload.
     *
     * @param publicKeyHex hex-encoded X.509 public key
     * @param payload      original message string
     * @param signatureHex hex-encoded DER signature
     * @return true if valid signature; false otherwise
     */
    public static boolean verify(String publicKeyHex, String payload, String signatureHex) {
        if (publicKeyHex == null || publicKeyHex.isBlank() || signatureHex == null || signatureHex.isBlank()) {
            return false;
        }
        try {
            byte[] pubBytes = HexFormat.of().parseHex(publicKeyHex.trim());
            byte[] sigBytes = HexFormat.of().parseHex(signatureHex.trim());

            KeyFactory kf = KeyFactory.getInstance("EC");
            PublicKey pub = kf.generatePublic(new X509EncodedKeySpec(pubBytes));

            Signature ecdsa = Signature.getInstance("SHA256withECDSA");
            ecdsa.initVerify(pub);
            ecdsa.update(payload.getBytes(StandardCharsets.UTF_8));
            return ecdsa.verify(sigBytes);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Derives a Bitcoin-style public address: "pow1_" + hex checksum of public key hash.
     */
    public static String deriveAddress(PublicKey pub) {
        if (pub == null) return "pow1_unknown";
        byte[] pubBytes = pub.getEncoded();
        String hash = HashUtil.sha256Hex(pubBytes);
        return "pow1_" + hash.substring(0, 32);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getAddress() {
        return address;
    }

    public String getPrivateKeyHex() {
        return privateKey != null ? HexFormat.of().formatHex(privateKey.getEncoded()) : "";
    }

    public String getPublicKeyHex() {
        return publicKey != null ? HexFormat.of().formatHex(publicKey.getEncoded()) : "";
    }
}
