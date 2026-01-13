package com.sumit.helpdesk.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumit.helpdesk.common.ApiException;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void log(String action, String resourceType, String resourceId, Object before, Object after) {
        AuditLog log = new AuditLog();
        log.setActorUserId(getActorUserId());
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setBeforeJson(toJson(before));
        log.setAfterJson(toJson(after));
        auditLogRepository.save(log);
    }

    private String getActorUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ApiException("Missing actor");
        }
        return auth.getPrincipal().toString();
    }

    private String toJson(Object value) {
        return Optional.ofNullable(value)
                .map(
                        v -> {
                            try {
                                return objectMapper.writeValueAsString(v);
                            } catch (Exception ex) {
                                return null;
                            }
                        })
                .orElse(null);
    }
}
