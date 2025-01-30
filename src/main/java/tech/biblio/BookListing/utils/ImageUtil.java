package tech.biblio.BookListing.utils;

import io.appwrite.Client;
import io.appwrite.coroutines.CoroutineCallback;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.InputFile;
import io.appwrite.services.Avatars;
import io.appwrite.services.Storage;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tech.biblio.BookListing.dto.UserDTO;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class ImageUtil {
    // read file : ID: efb83y28vq8b238bfbfbbfeib2

    @Autowired
    private AppwriteUtil appwriteUtil;

    public String getExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public File convertToFile(MultipartFile multipartFile) throws IOException {

        if(multipartFile.isEmpty()) {
            throw new FileNotFoundException("Uploaded file not found");
        }
        File tempFile = File.createTempFile("temp", this.getExtension(multipartFile.getOriginalFilename()));

        if (!tempFile.exists()) {
            throw new FileNotFoundException("File not found at: " + tempFile.getAbsolutePath());
        }
        FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(multipartFile.getBytes());
            fos.close();

        return tempFile;
    }

    public File compressProfileImage(File largeImage, String fileName) throws IOException {
        long fileSize = largeImage.length(); // Use .length() to get the actual size in bytes

        File compressedImage = new File(fileName);

        // Compression logic based on size
        if (fileSize > 1024 * 1024 && fileSize <= 2 * 1024 * 1024) { // Between 1MB and 2MB
            Thumbnails.of(largeImage)
                    .scale(0.5)         // Scale down by 50%
                    .outputQuality(0.3) // Set quality to 30% for ~350KB output
                    .toFile(compressedImage);
        } else if(fileSize>150*1024 && fileSize<=1024*1024){ // Files <= 1MB
            Thumbnails.of(largeImage)
                    .scale(0.7)           // No scaling for smaller files
                    .outputQuality(0.4) // Set quality to 40%
                    .toFile(compressedImage);
        }
        else {
            Thumbnails.of(largeImage)
                    .scale(1)           // No scaling for smaller files
                    .outputQuality(0.4) // Set quality to 40%
                    .toFile(compressedImage);
        }

        return compressedImage;
    }

    public io.appwrite.models.File uploadImage(String appId, String bucketId, String apiKey, File image, UserDTO user) throws AppwriteException, ExecutionException, InterruptedException {
        Client client = appwriteUtil.getClient(appId, apiKey);

        Storage storage = new Storage(client);
        CompletableFuture<io.appwrite.models.File> future = new CompletableFuture<>();

        storage.createFile(
                bucketId, // bucketId
                user.getProfileImageId(), // fileId (you can make it dynamic if required)
                InputFile.Companion.fromFile(image), // file
                List.of("read(\"any\")"), // permissions (optional)
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        future.completeExceptionally(error); // Propagate the error
                    } else {
                        System.out.println("Upload File Result: " + result.getName());
                        future.complete(result); // Complete the future with the result
                    }
                })
        );

        // Wait for the upload to complete and return the result
        io.appwrite.models.File uploadedFile = future.get();

        // Delete the file after the upload is complete
        if (image.exists() && image.delete()) {
            System.out.println("Temporary file deleted successfully.");
        } else {
            System.out.println("Failed to delete temporary file.");
        }

        return uploadedFile;
    }

    // delete existing file from Appwrite
    public void deleteImage(String appId, String bucketId, String apiKey, String fileId) throws AppwriteException{
        Client client = appwriteUtil.getClient(appId, apiKey);

        Storage storage = new Storage(client);
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        storage.deleteFile(
                bucketId, // bucketId
                fileId, // fileId (you can make it dynamic if required)
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        future.completeExceptionally(error); // Propagate the error
                    } else {
                        System.out.println("Delete File Result: " + result);
                        future.complete((Boolean) result); // Complete the future with the result
                    }
                })
        );

    }

    // get file preview
    public byte[] getImagePreview(String appId, String bucketId, String apiKey, String fileId) throws AppwriteException, ExecutionException, InterruptedException {
        Client client = appwriteUtil.getClient(appId, apiKey);

        Storage storage = new Storage(client);
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        storage.getFilePreview(
                bucketId, // bucketId
                fileId, // fileId (you can make it dynamic if required)
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        future.completeExceptionally(error); // Propagate the error
                    } else {
                        System.out.println("Get File Preview Result: " + result);
                        future.complete(result); // Complete the future with the result
                    }
                })
        );
        byte[] filePreview = future.get();

        return filePreview;
    }

    // user initials
    public byte [] getUserInitials(String appId, String apiKey, String username) throws AppwriteException, ExecutionException, InterruptedException {
        Client client = appwriteUtil.getClient(appId,apiKey);
        Avatars avatars = new Avatars(client);

        CompletableFuture<byte[]> future = new CompletableFuture<>();

        System.out.println("API KEY : "+apiKey+", USERNAME : "+username);
        avatars.getInitials(
                username,
                200L,
                200L,
                this.pastelColorHex(),
                new CoroutineCallback<byte[]>((result, error)-> {
                    if(error != null) {
                        error.printStackTrace();
                        future.completeExceptionally(error);
                    }
                    else {
                        System.out.println("Get avatars result : "+result);
                        future.complete((byte[]) result);
                    }
                })
        );
        byte [] avatar = future.get();

        return avatar;
    }

    // random pastel color
    public String pastelColorHex(){
        int red = (int)(Math.random()*128) + 127;
        int blue = (int)(Math.random()*128) + 127;
        int green = (int)(Math.random()*128) + 127;
        Color randomColor = new Color(red,green,blue);

        String hex = Integer.toHexString(randomColor.getRGB()).substring(2);
        System.out.println("Random HEX : "+hex);
        return hex;
    }

    // Post Image Utils
    public File compressPostImage(File largeImage, String fileName) throws IOException {
        long fileSize = largeImage.length(); // Use .length() to get the actual size in bytes

        File compressedImage = new File(fileName);

        // Compression logic based on size
        if (fileSize > 2 * 1024*1024 && fileSize <= 5 * 1024*1024) { // Between 2MB and 5MB
            Thumbnails.of(largeImage)
                    .scale(0.5)         // Scale down by 50%
                    .outputQuality(0.3) // Set quality to 30% for ~350KB output
                    .toFile(compressedImage);
        } else if(fileSize>1024*1024 && fileSize<= 2 * 1024*1024){ // Files <= 2MB
            Thumbnails.of(largeImage)
                    .scale(0.7)           // No scaling for smaller files
                    .outputQuality(0.4) // Set quality to 40%
                    .toFile(compressedImage);
        }
        else {
            Thumbnails.of(largeImage)
                    .scale(1)           // No scaling for smaller files
                    .outputQuality(0.4) // Set quality to 40%
                    .toFile(compressedImage);
        }

        return compressedImage;
    }

    public io.appwrite.models.File uploadPostImage(String appId, String bucketId, String apiKey, File image, String imageId) throws AppwriteException, ExecutionException, InterruptedException {
        Client client = appwriteUtil.getClient(appId, apiKey);

        Storage storage = new Storage(client);
        CompletableFuture<io.appwrite.models.File> future = new CompletableFuture<>();

        storage.createFile(
                bucketId, // bucketId
                imageId, // fileId (you can make it dynamic if required)
                InputFile.Companion.fromFile(image), // file
                List.of("read(\"any\")"), // permissions (optional)
                new CoroutineCallback<>((result, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        future.completeExceptionally(error); // Propagate the error
                    } else {
                        System.out.println("Upload File Result: " + result.getName());
                        future.complete(result); // Complete the future with the result
                    }
                })
        );

        // Wait for the upload to complete and return the result
        io.appwrite.models.File uploadedFile = future.get();

        // Delete the file after the upload is complete
        if (image.exists() && image.delete()) {
            System.out.println("Temporary file deleted successfully.");
        } else {
            System.out.println("Failed to delete temporary file.");
        }

        return uploadedFile;
    }
}
