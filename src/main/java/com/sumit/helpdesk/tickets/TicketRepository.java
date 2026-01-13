package com.sumit.helpdesk.tickets;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    @Query(
            "select t from Ticket t where (:status is null or t.status = :status) "
                    + "and (:q is null or lower(t.title) like lower(concat('%', :q, '%')) "
                    + "or lower(t.description) like lower(concat('%', :q, '%')))")
    Page<Ticket> search(
            @Param("status") TicketStatus status, @Param("q") String q, Pageable pageable);

    long countByStatus(TicketStatus status);

    long countByAssigneeUserIdIsNull();
}
