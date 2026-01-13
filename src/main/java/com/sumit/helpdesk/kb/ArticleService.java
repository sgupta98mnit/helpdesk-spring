package com.sumit.helpdesk.kb;

import com.sumit.helpdesk.audit.AuditService;
import com.sumit.helpdesk.common.ApiException;
import com.sumit.helpdesk.events.OutboxService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleVersionRepository versionRepository;
    private final AuditService auditService;
    private final OutboxService outboxService;

    @Transactional
    public Article create(ArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.title());
        article.setSlug(request.slug());
        Article saved = articleRepository.save(article);
        createVersion(saved, request.content());
        auditService.log("ARTICLE_CREATED", "Article", saved.getId().toString(), null, saved);
        outboxService.record("kb.article.created", saved);
        return saved;
    }

    @Transactional
    public ArticleVersion createVersion(Article article, String content) {
        int nextVersion = nextVersion(article.getId().toString());
        ArticleVersion version = new ArticleVersion();
        version.setArticleId(article.getId().toString());
        version.setVersionNumber(nextVersion);
        version.setContent(content);
        ArticleVersion saved = versionRepository.save(version);
        article.setCurrentVersion(nextVersion);
        articleRepository.save(article);
        auditService.log(
                "ARTICLE_VERSION_CREATED", "Article", article.getId().toString(), null, saved);
        outboxService.record("kb.article.versioned", saved);
        return saved;
    }

    public Page<Article> list(ArticleStatus status, String q, int page, int size) {
        return articleRepository.search(status, q, PageRequest.of(page, size));
    }

    public Article getBySlug(String slug) {
        return articleRepository.findBySlug(slug).orElseThrow(() -> new ApiException("Not found"));
    }

    @Cacheable(value = "kb-articles", key = "#slug")
    public Article getPublishedBySlug(String slug) {
        Article article = getBySlug(slug);
        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new ApiException("Not published");
        }
        return article;
    }

    public Article getById(UUID id) {
        return articleRepository.findById(id).orElseThrow(() -> new ApiException("Not found"));
    }

    public List<ArticleVersion> versions(UUID articleId) {
        return versionRepository.findByArticleIdOrderByVersionNumberDesc(articleId.toString());
    }

    @Transactional
    @CacheEvict(value = "kb-articles", allEntries = true)
    public Article publish(UUID articleId, Integer versionNumber) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.setStatus(ArticleStatus.PUBLISHED);
        if (versionNumber != null) {
            article.setCurrentVersion(versionNumber);
        }
        Article saved = articleRepository.save(article);
        auditService.log("ARTICLE_PUBLISHED", "Article", saved.getId().toString(), null, saved);
        outboxService.record("kb.article.published", saved);
        return saved;
    }

    private int nextVersion(String articleId) {
        return versionRepository.findByArticleIdOrderByVersionNumberDesc(articleId).stream()
                .findFirst()
                .map(ArticleVersion::getVersionNumber)
                .orElse(0)
                + 1;
    }
}
