package uk.gov.justice.laa.data.claims.certificated.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Entity representing a claim draft in the system. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "claim_drafts")
public class ClaimDraftEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "claim_type_id")
  private UUID claimTypeId;

  @Column(name = "certificate_id")
  private String certificateId;

  /** Enumeration representing the status of a claim draft. */
  public enum ClaimDraftStatus {
    DRAFT,
    DELETED
  }

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "status", nullable = false, columnDefinition = "claim_draft_status")
  private ClaimDraftStatus status;

  @Column(name = "source_system", nullable = false)
  private String sourceSystem;

  @Column(name = "created_by_user_id", nullable = false)
  private String createdByUserId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "data", columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> data;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata", columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> metadata;

  @Column(name = "created_at", updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;
}
