package com.sumit.helpdesk.attachments;

import com.sumit.helpdesk.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment extends TenantEntity {
    @Column(nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private String filename;

    private String contentType;

    @Column(nullable = false)
    private String s3Key;

    private Long sizeBytes;
}
