package com.sumit.helpdesk.tickets;

import com.sumit.helpdesk.audit.AuditService;
import com.sumit.helpdesk.common.ApiException;
import com.sumit.helpdesk.events.OutboxService;
import com.sumit.helpdesk.notifications.NotificationService;
import com.sumit.helpdesk.tenant.TenantContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TicketCommentRepository ticketCommentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final AuditService auditService;
    private final OutboxService outboxService;
    private final NotificationService notificationService;

    @Transactional
    public Ticket create(TicketRequest request, String idempotencyKey) {
        if (idempotencyKey != null) {
            IdempotencyKey existing =
                    idempotencyKeyRepository
                            .findByKeyValueAndTenantId(idempotencyKey, TenantContext.getTenantId())
                            .orElse(null);
            if (existing != null) {
                return ticketRepository
                        .findById(UUID.fromString(existing.getResourceId()))
                        .orElseThrow(() -> new ApiException("Ticket not found"));
            }
        }
        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setRequesterEmail(request.requesterEmail());
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }
        ticket.setAssigneeUserId(request.assigneeUserId());
        Ticket saved = ticketRepository.save(ticket);
        if (idempotencyKey != null) {
            IdempotencyKey key = new IdempotencyKey();
            key.setKeyValue(idempotencyKey);
            key.setResourceId(saved.getId().toString());
            idempotencyKeyRepository.save(key);
        }
        auditService.log("TICKET_CREATED", "Ticket", saved.getId().toString(), null, saved);
        outboxService.record("ticket.created", saved);
        if (saved.getAssigneeUserId() != null) {
            notificationService.notifyUser(
                    saved.getAssigneeUserId(), "TICKET_ASSIGNED", "Ticket assigned");
        }
        return saved;
    }

    public Page<Ticket> list(TicketStatus status, String q, int page, int size) {
        return ticketRepository.search(status, q, PageRequest.of(page, size));
    }

    public Ticket get(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new ApiException("Not found"));
    }

    @Transactional
    public Ticket update(UUID id, TicketUpdateRequest request) {
        Ticket ticket = get(id);
        Ticket before = new Ticket();
        before.setTitle(ticket.getTitle());
        before.setDescription(ticket.getDescription());
        before.setStatus(ticket.getStatus());
        before.setPriority(ticket.getPriority());
        before.setRequesterEmail(ticket.getRequesterEmail());
        before.setAssigneeUserId(ticket.getAssigneeUserId());
        if (request.status() != null) {
            ticket.setStatus(request.status());
            if (ticket.getAssigneeUserId() != null) {
                notificationService.notifyUser(
                        ticket.getAssigneeUserId(), "TICKET_STATUS", "Ticket status changed");
            }
        }
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }
        if (request.assigneeUserId() != null) {
            ticket.setAssigneeUserId(request.assigneeUserId());
            notificationService.notifyUser(
                    request.assigneeUserId(), "TICKET_ASSIGNED", "Ticket assigned");
        }
        Ticket saved = ticketRepository.save(ticket);
        auditService.log("TICKET_UPDATED", "Ticket", saved.getId().toString(), before, saved);
        outboxService.record("ticket.updated", saved);
        return saved;
    }

    public TicketComment addComment(UUID ticketId, String body, String authorUserId) {
        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId.toString());
        comment.setAuthorUserId(authorUserId);
        comment.setBody(body);
        TicketComment saved = ticketCommentRepository.save(comment);
        auditService.log(
                "TICKET_COMMENTED", "Ticket", ticketId.toString(), null, saved);
        outboxService.record("ticket.commented", saved);
        return saved;
    }

    public List<TicketComment> comments(UUID ticketId) {
        return ticketCommentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId.toString());
    }
}
