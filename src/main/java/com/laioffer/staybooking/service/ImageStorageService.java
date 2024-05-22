package com.laioffer.staybooking.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.laioffer.staybooking.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ImageStorageService {
    @Value("${gcs.bucket}")
    private String bucketName;

    private Storage storage;

    @Autowired
    public ImageStorageService(Storage storage) {
        this.storage = storage;
    }

    public String save(MultipartFile file) throws GCSUploadException { //分成好几段上传，在server端形成完整文件
        String filename = UUID.randomUUID().toString();//uuid unique identifier
        BlobInfo blobInfo = null;
        try {
            blobInfo = storage.createFrom(
                    BlobInfo
                            .newBuilder(bucketName, filename)
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)))) //set access，设定权限
                            .build(),
                    file.getInputStream());
        } catch (IOException exception) {
            throw new GCSUploadException("Failed to upload file to GCS");
        }

        return blobInfo.getMediaLink();
    }
}


