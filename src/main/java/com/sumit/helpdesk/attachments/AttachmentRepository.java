package com.sumit.helpdesk.attachments;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    Optional<Attachment> findByIdAndTenantId(UUID id, String tenantId);
    List<Attachment> findByTicketIdOrderByCreatedAtDesc(String ticketId);
}
