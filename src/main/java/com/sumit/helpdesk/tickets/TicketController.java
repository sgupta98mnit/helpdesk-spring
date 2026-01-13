package com.sumit.helpdesk.tickets;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<TicketResponse> create(
            @RequestBody TicketRequest request,
            @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey) {
        Ticket ticket = ticketService.create(request, idempotencyKey);
        return ResponseEntity.ok(toResponse(ticket));
    }

    @GetMapping
    public ResponseEntity<Page<Ticket>> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ticketService.list(status, q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.get(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<Ticket> update(
            @PathVariable UUID id, @RequestBody TicketUpdateRequest request) {
        return ResponseEntity.ok(ticketService.update(id, request));
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<TicketComment> comment(
            @PathVariable UUID id, @RequestBody TicketCommentRequest request) {
        String userId = currentUserId();
        return ResponseEntity.ok(ticketService.addComment(id, request.body(), userId));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketComment>> comments(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.comments(id));
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getPrincipal().toString();
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId().toString(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getRequesterEmail(),
                ticket.getAssigneeUserId(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt());
    }
}
