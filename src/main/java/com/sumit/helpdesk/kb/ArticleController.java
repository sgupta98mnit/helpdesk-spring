package com.sumit.helpdesk.kb;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kb/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<Article> create(@RequestBody ArticleRequest request) {
        return ResponseEntity.ok(articleService.create(request));
    }

    @PostMapping("/{id}/versions")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<ArticleVersion> createVersion(
            @PathVariable UUID id, @RequestBody ArticleVersionRequest request) {
        Article article = articleService.getById(id);
        return ResponseEntity.ok(articleService.createVersion(article, request.content()));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<Article> publish(
            @PathVariable UUID id, @RequestBody PublishRequest request) {
        return ResponseEntity.ok(articleService.publish(id, request.versionNumber()));
    }

    @GetMapping
    public ResponseEntity<Page<Article>> list(
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (isViewer()) {
            status = ArticleStatus.PUBLISHED;
        }
        return ResponseEntity.ok(articleService.list(status, q, page, size));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Article> get(@PathVariable String slug) {
        if (isViewer()) {
            return ResponseEntity.ok(articleService.getPublishedBySlug(slug));
        }
        return ResponseEntity.ok(articleService.getBySlug(slug));
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','AGENT')")
    public ResponseEntity<List<ArticleVersion>> versions(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.versions(id));
    }

    private boolean isViewer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_VIEWER"));
    }
}
