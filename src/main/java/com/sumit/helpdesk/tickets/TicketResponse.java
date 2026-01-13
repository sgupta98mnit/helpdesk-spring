package com.sumit.helpdesk.tickets;

import java.time.Instant;

public record TicketResponse(
        String id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        String requesterEmail,
        String assigneeUserId,
        Instant createdAt,
        Instant updatedAt) {}
