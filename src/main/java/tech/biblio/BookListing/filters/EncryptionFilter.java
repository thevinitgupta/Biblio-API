package tech.biblio.BookListing.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import tech.biblio.BookListing.services.KeyService;
import tech.biblio.BookListing.utils.CachedBodyHttpServletRequest;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Order(1)
public class EncryptionFilter extends OncePerRequestFilter {

    private final KeyService keyService;

    private final MultipartResolver multipartResolver;

    public EncryptionFilter(KeyService keyService) {
        this.keyService = keyService;
        this.multipartResolver = new StandardServletMultipartResolver(); // manually initialize
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("HEADER Encrypted : " + request.getHeader("X-ENCRYPTED"));
        if (Objects.equals(request.getMethod(), "GET") || request.getHeader("X-ENCRYPTED") == null || request.getHeader("X-ENCRYPTED").equals("false")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            boolean multipartContentType = request.getContentType().contains("multipart/form-data");
            HttpServletRequest decryptedRequest = null;
            if (multipartContentType) {
                filterChain.doFilter(request, response);
            } else {
                decryptedRequest = this.processJsonBody(request);
            }
            filterChain.doFilter(decryptedRequest, response);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Failed to decrypt request");
        }

    }

    private CachedBodyHttpServletRequest processFormBody(HttpServletRequest request) throws Exception {
        if (multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);

            String encryptedValue = multipartRequest.getParameter("encrypted");
            if (encryptedValue != null && !encryptedValue.isBlank()) {
                byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue.trim());
                byte[] decryptedData = decrypt(encryptedBytes, keyService.getPrivateKey(), keyService.getPublicKey());
                return new CachedBodyHttpServletRequest(request, decryptedData);
            }
        }
        return null;
    }

    private CachedBodyHttpServletRequest processJsonBody(HttpServletRequest request) throws Exception {
        String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        System.out.println("Encrypted Request Body: " + body);


// Parse JSON to extract the "encrypted" field
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);
        String encryptedString = jsonNode.get("encrypted").asText().trim(); // ✅ Just the Base64 string
        System.out.println("Encrypted Data as text : " + encryptedString);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
        byte[] decryptedData = this.decrypt(encryptedBytes, keyService.getPrivateKey(), keyService.getPublicKey());

// Wrap the request with the decrypted body
        return new CachedBodyHttpServletRequest(request, decryptedData);
    }

    private byte[] decrypt(byte[] encrypted, PrivateKey privateKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        ByteArrayOutputStream ptStream = new ByteArrayOutputStream();
        int keySizeBytes = ((RSAPublicKey) publicKey).getModulus().bitLength() / Byte.SIZE;
        int off = 0;
        while (off < encrypted.length) {
            int toCrypt = Math.min(keySizeBytes, encrypted.length - off);
            byte[] partialPT = cipher.doFinal(encrypted, off, toCrypt);
            ptStream.write(partialPT);
            off += toCrypt;
        }
        return ptStream.toByteArray();
    }
}
