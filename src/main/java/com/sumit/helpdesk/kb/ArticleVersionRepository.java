package com.sumit.helpdesk.kb;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleVersionRepository extends JpaRepository<ArticleVersion, UUID> {
    List<ArticleVersion> findByArticleIdOrderByVersionNumberDesc(String articleId);
}
