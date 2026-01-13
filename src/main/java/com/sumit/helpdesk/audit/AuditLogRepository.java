package com.sumit.helpdesk.audit;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @Query(
            "select a from AuditLog a where (:resourceType is null or a.resourceType = :resourceType) "
                    + "and (:resourceId is null or a.resourceId = :resourceId)")
    Page<AuditLog> search(
            @Param("resourceType") String resourceType,
            @Param("resourceId") String resourceId,
            Pageable pageable);
}
