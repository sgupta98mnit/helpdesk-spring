package com.sumit.helpdesk.tickets;

public record TicketUpdateRequest(
        TicketStatus status, TicketPriority priority, String assigneeUserId) {}
