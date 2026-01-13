package com.sumit.helpdesk.kb;

import com.sumit.helpdesk.common.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kb_article_versions")
public class ArticleVersion extends TenantEntity {
    @Column(nullable = false)
    private String articleId;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(nullable = false, columnDefinition = "text")
    private String content;
}
