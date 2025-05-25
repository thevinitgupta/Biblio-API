package tech.biblio.BookListing.services;

import io.appwrite.exceptions.AppwriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.biblio.BookListing.dto.UserDTO;
import tech.biblio.BookListing.entities.User;
import tech.biblio.BookListing.exceptions.FileTypeNotAllowedException;
import tech.biblio.BookListing.exceptions.UserNotFoundException;
import tech.biblio.BookListing.mappers.UserMapper;
import tech.biblio.BookListing.repositories.UserRepository;
import tech.biblio.BookListing.utils.ImageUtil;
import tech.biblio.BookListing.utils.UniqueID;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private ImageUtil imageUtil;

    @Value("${APPWRITE_PROJECT_ID}")
    private String projectId;

    @Value("${APPWRITE_SECRET_KEY}")
    private String apiKey;

    @Value("${APPWRITE_PROFILE_IMAGE_BUCKET}")
    private String profileImageBucket;

    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(UserDTO user) {
        User dbUser = userRepository.findFirstByEmail(user.getEmail());
        dbUser.setPosts(user.getPosts());
        dbUser.setProfileImageId(user.getProfileImageId());
        dbUser.setProfileImageAdded(user.isProfileImageAdded());
        return userRepository.save(dbUser);
    }

    public boolean checkUserExists(String email) {
        User dbUser = userRepository.findFirstByEmail(email);
        return dbUser != null;
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(user -> UserMapper.userDTO(user, false)).toList();
    }

    public List<UserDTO> getAllByFirstName(String firstName) {
        return userRepository.findByFirstName(firstName).stream().map(user -> UserMapper.userDTO(user, false)).toList();
    }

    public UserDTO getUserByEmail(String email, boolean allowPosts) {
        User dbUser = userRepository.findFirstByEmail(email);
        if (dbUser == null) throw new UserNotFoundException("No User with email: " + email + " found!", email);
        return UserMapper.userDTO(dbUser, allowPosts);
    }

    public Collection<GrantedAuthority> getUserAuthorities(String email) {
        User dbUser = userRepository.findFirstByEmail(email);
        if (dbUser == null) throw new UserNotFoundException("No User with email: " + email + " found!", email);
        return new ArrayList<>(dbUser.getAuthorities());
    }

    public void deleteUser(User user) {
        userRepository.deleteById(user.getId());
    }

    public boolean uploadProfileImage(MultipartFile multipartFile, String email) throws
            FileNotFoundException, FileTypeNotAllowedException, AppwriteException, IOException, ExecutionException, InterruptedException {
        File file = null;
        file = imageUtil.convertToFile(multipartFile);
        if (file.length() > 2 * 1024 * 1024) {
            throw new FileTypeNotAllowedException("Max file size 2MB allowed", file.length() / 1024 + "mb", "2MB");
        }
        System.out.println("Project ID : " + projectId);
        System.out.println("profileImageBucket : " + profileImageBucket);
        System.out.println("apiKey : " + apiKey);
        System.out.println("File before compression : " + file);
        String extension = imageUtil.getExtension(file.getName());
        if (!List.of(".png", ".jpg", ".jpeg").contains(extension)) {
            throw new FileTypeNotAllowedException("Only images of max size 5mb allowed in Profile Image",
                    extension,
                    "jpg/png/jpeg");
        }

        file = imageUtil.compressProfileImage(file, email + "_prof_img" + extension);
        // check if file exists
        UserDTO user = this.getUserByEmail(email, false);
        boolean existingFileDeleted = true;
        io.appwrite.models.File uploadedFile = null;

        // delete previous file with email id
        if (user.isProfileImageAdded()) {
            existingFileDeleted = this.deleteProfileImage(user);
        } else {
            user.setProfileImageId(UniqueID.generateLongId());
        }
        // create new file
        if (existingFileDeleted) {
            uploadedFile = imageUtil.uploadImage(projectId, profileImageBucket, apiKey, file, user);
        }
        // if uploaded, update user object to add profImage = true flag
        if (uploadedFile != null) {
            user.setProfileImageAdded(true);
            this.updateUser(user);
        }


        return true;
    }

    public byte[] getUserProfileImage(UserDTO user) throws AppwriteException, ExecutionException, InterruptedException {
        if (!user.isProfileImageAdded()) {
            return this.getUserAvatar(user.getFirstName() + " " + user.getLastName());
        }
        return imageUtil.getImagePreview(projectId, profileImageBucket, apiKey, user.getProfileImageId());

    }

    public boolean deleteProfileImage(UserDTO user) throws AppwriteException {
        imageUtil.deleteImage(projectId, profileImageBucket, apiKey, user.getProfileImageId());
        user.setProfileImageAdded(false);
        this.updateUser(user);
        return true;
    }

    public byte[] getUserAvatar(String username) throws AppwriteException, ExecutionException, InterruptedException {
        byte[] initials = null;
        if (username != null) {
            initials = imageUtil.getUserInitials(projectId, apiKey, username);
        }
        return initials;
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }
}
