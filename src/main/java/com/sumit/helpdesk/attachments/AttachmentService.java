package com.sumit.helpdesk.attachments;

import com.sumit.helpdesk.common.ApiException;
import com.sumit.helpdesk.tenant.TenantContext;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final S3Client s3Client;
    private final String bucket;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            S3Client s3Client,
            @Value("${app.s3.bucket}") String bucket) {
        this.attachmentRepository = attachmentRepository;
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public Attachment upload(String ticketId, MultipartFile file) throws IOException {
        String tenantId = TenantContext.getTenantId();
        String key =
                tenantId
                        + "/"
                        + ticketId
                        + "/"
                        + UUID.randomUUID()
                        + "-"
                        + file.getOriginalFilename();
        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .build();
        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        Attachment attachment = new Attachment();
        attachment.setTicketId(ticketId);
        attachment.setFilename(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setS3Key(key);
        attachment.setSizeBytes(file.getSize());
        return attachmentRepository.save(attachment);
    }

    public java.util.List<Attachment> listForTicket(String ticketId) {
        return attachmentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    public Attachment getAttachment(UUID attachmentId) {
        String tenantId = TenantContext.getTenantId();
        return attachmentRepository
                .findByIdAndTenantId(attachmentId, tenantId)
                .orElseThrow(() -> new ApiException("Attachment not found"));
    }

    public ResponseInputStream<?> download(UUID attachmentId) {
        Attachment attachment = getAttachment(attachmentId);
        try {
            GetObjectRequest request =
                    GetObjectRequest.builder().bucket(bucket).key(attachment.getS3Key()).build();
            return s3Client.getObject(request);
        } catch (S3Exception ex) {
            throw new ApiException("Failed to download attachment");
        }
    }
}
