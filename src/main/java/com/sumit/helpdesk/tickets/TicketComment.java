package com.sumit.helpdesk.tickets;

import com.sumit.helpdesk.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticket_comments")
public class TicketComment extends TenantEntity {
    @Column(nullable = false)
    private String ticketId;

    @Column(nullable = false)
    private String authorUserId;

    @Column(nullable = false, columnDefinition = "text")
    private String body;
}
