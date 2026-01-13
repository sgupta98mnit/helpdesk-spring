package com.sumit.helpdesk.common;

import com.sumit.helpdesk.kb.ArticleRepository;
import com.sumit.helpdesk.kb.ArticleStatus;
import com.sumit.helpdesk.tickets.TicketRepository;
import com.sumit.helpdesk.tickets.TicketStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final TicketRepository ticketRepository;
    private final ArticleRepository articleRepository;

    public DashboardController(
            TicketRepository ticketRepository, ArticleRepository articleRepository) {
        this.ticketRepository = ticketRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> stats() {
        long openTickets = ticketRepository.countByStatus(TicketStatus.OPEN);
        long unassignedTickets = ticketRepository.countByAssigneeUserIdIsNull();
        long publishedArticles = articleRepository.countByStatus(ArticleStatus.PUBLISHED);
        return ResponseEntity.ok(new DashboardStats(openTickets, unassignedTickets, publishedArticles));
    }
}
