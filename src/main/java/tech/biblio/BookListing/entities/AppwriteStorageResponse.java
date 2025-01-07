package tech.biblio.BookListing.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

/* Upload File Result :
                    File(id=efb83y28vq8b238bfbfbbfeib2, -> can use email
                    bucketId=677152700009cf0192c0,
                    createdAt=2025-01-01T08:33:11.353+00:00,
                    updatedAt=2025-01-01T08:33:11.353+00:00,
                    permissions=[read("any")],
                    name=Dec 15 Screenshot from Conversion.png, -> can be changed to Email+Timestamp
                    signature=f0b0c6939876f5aef93059340473e75e, -> Don't Know
                    mimeType=image/png,  -> NOT REQUIRED
                    sizeOriginal=917340, -> Need to compress
                    chunksTotal=1,
                    chunksUploaded=1)
                    */
@AllArgsConstructor
@Data
public class AppwriteStorageResponse {
    String id;
    String bucketId;
    Date createdAt;
    Date updatedAt;
    List<String> permissions;
    String name;
    String mimeType;
    Long sizeOriginal;
    int chuksTotal;
    int chunksUploaded;
}
