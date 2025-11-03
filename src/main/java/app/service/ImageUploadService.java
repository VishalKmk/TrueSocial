package app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    /**
     * Upload image to Cloudinary
     * @param file Image file
     * @param folder Folder in Cloudinary (e.g., "posts" or "profiles")
     * @return Image URL
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto",
                        "quality", "auto" // Auto-optimize
                )
        );

        return (String) uploadResult.get("secure_url");
    }

    /**
     * Delete image from Cloudinary
     * @param publicId Image public ID from Cloudinary
     */
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}