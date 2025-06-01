package tech.biblio.BookListing.services;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.exceptions.KeyPairCreationException;

import java.security.*;
import java.util.Base64;

@Service
public class KeyService {


    @Getter
    private PublicKey publicKey;
    @Getter
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws KeyPairCreationException {
        try {
            generateNewKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyPairCreationException("Algorithm to create Key Pair not found");
        }
    }

    private void generateNewKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // Or 4096 for stronger security
        KeyPair pair = generator.generateKeyPair();

        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }



/*
//    @PostConstruct
//    public void init() throws Exception {
//        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr.replaceAll("\\s+", ""));
//        privateKey = KeyFactory.getInstance("RSA")
//                .generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
//
//
//        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr.replaceAll("\\s+", ""));
//        publicKey = KeyFactory.getInstance("RSA")
//                .generatePublic(new X509EncodedKeySpec(publicBytes));
//    }
//
//    public String getPublicKeyBase64() {
//        return publicKeyStr;
//    }
 */
}
