package com.sumit.helpdesk.search;

import com.sumit.helpdesk.kb.Article;
import com.sumit.helpdesk.tenant.TenantContext;
import com.sumit.helpdesk.tickets.Ticket;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final EntityManager entityManager;

    public SearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Ticket> searchTickets(String q) {
        String tenantId = TenantContext.getTenantId();
        return entityManager
                .createNativeQuery(
                        "select * from tickets where tenant_id = :tenantId and "
                                + "to_tsvector('english', title || ' ' || description) "
                                + "@@ plainto_tsquery('english', :q)",
                        Ticket.class)
                .setParameter("tenantId", tenantId)
                .setParameter("q", q)
                .getResultList();
    }

    public List<Article> searchKb(String q) {
        String tenantId = TenantContext.getTenantId();
        return entityManager
                .createNativeQuery(
                        "select a.* from kb_articles a join kb_article_versions v "
                                + "on v.article_id = a.id::text "
                                + "where a.tenant_id = :tenantId and a.status = 'PUBLISHED' "
                                + "and to_tsvector('english', a.title || ' ' || v.content) "
                                + "@@ plainto_tsquery('english', :q)",
                        Article.class)
                .setParameter("tenantId", tenantId)
                .setParameter("q", q)
                .getResultList();
    }
}
