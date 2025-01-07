package tech.biblio.BookListing.utils;

import io.appwrite.Client;
import org.springframework.stereotype.Component;

@Component
public class AppwriteUtil {
    private Client appwriteClient = null;
    public Client getClient(String appId, String apiKey){
        if(appwriteClient==null){
            this.appwriteClient = new Client()
                    .setEndpoint("https://cloud.appwrite.io/v1") // Your API Endpoint
                    .setProject(appId) // Your project ID
                    .setKey(apiKey);
        }
        return this.appwriteClient;
    }
}
