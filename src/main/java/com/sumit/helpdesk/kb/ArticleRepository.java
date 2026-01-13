package com.sumit.helpdesk.kb;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findBySlug(String slug);

    @Query(
            "select a from Article a where (:status is null or a.status = :status) "
                    + "and (:q is null or lower(a.title) like lower(concat('%', :q, '%')))")
    Page<Article> search(@Param("status") ArticleStatus status, @Param("q") String q, Pageable pageable);

    long countByStatus(ArticleStatus status);
}
