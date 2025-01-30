package tech.biblio.BookListing.utils;

import io.appwrite.Client;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AppwriteUtil {
    private Client appwriteClient = null;
    public Client getClient(String appId, String apiKey){
        if(appwriteClient==null){
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)  // Increase connection timeout
                    .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
                    .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
                    .build();
            this.appwriteClient = new Client()
                    .setEndpoint("https://cloud.appwrite.io/v1") // Your API Endpoint
                    .setProject(appId) // Your project ID
                    .setKey(apiKey);

            this.appwriteClient.setHttp(httpClient);
        }
        return this.appwriteClient;
    }
}
