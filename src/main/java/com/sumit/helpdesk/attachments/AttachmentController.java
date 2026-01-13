package com.sumit.helpdesk.attachments;

import java.io.IOException;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/tickets/{id}/attachments")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<Attachment> upload(
            @PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(attachmentService.upload(id, file));
    }

    @GetMapping("/tickets/{id}/attachments")
    public ResponseEntity<List<Attachment>> list(@PathVariable String id) {
        return ResponseEntity.ok(attachmentService.listForTicket(id));
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
        Attachment attachment = attachmentService.getAttachment(id);
        var stream = attachmentService.download(id);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }
}
