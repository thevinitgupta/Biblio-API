package tech.biblio.BookListing.services;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class KeyService {
    @Value("${RSA_PUBLIC_KEY}")
    private String publicKeyStr;

    @Value("${RSA_PRIVATE_KEY}")
    private String privateKeyStr;

    @Getter
    private PublicKey publicKey;
    @Getter
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws Exception {
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr.replaceAll("\\s+", ""));
        privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateBytes));


        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr.replaceAll("\\s+", ""));
        publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicBytes));
    }

    public String getPublicKeyBase64() {
        return publicKeyStr;
    }
}
