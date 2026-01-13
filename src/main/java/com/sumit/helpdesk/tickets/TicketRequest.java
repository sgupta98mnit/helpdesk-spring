package com.sumit.helpdesk.tickets;

public record TicketRequest(
        String title,
        String description,
        String requesterEmail,
        TicketPriority priority,
        String assigneeUserId) {}
