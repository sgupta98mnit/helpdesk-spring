package com.sumit.helpdesk.search;

import com.sumit.helpdesk.kb.Article;
import com.sumit.helpdesk.tickets.Ticket;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> tickets(@RequestParam String q) {
        return ResponseEntity.ok(searchService.searchTickets(q));
    }

    @GetMapping("/kb")
    public ResponseEntity<List<Article>> kb(@RequestParam String q) {
        return ResponseEntity.ok(searchService.searchKb(q));
    }
}
