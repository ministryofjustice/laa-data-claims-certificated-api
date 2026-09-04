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

/** Entity representing a draft claim in the system. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "draft_claims")
public class DraftClaimEntity {
  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "draft_type_id")
  private UUID draftTypeId;

  @Column(name = "certificate_id")
  private String certificateId;

  /** Enumeration representing the status of a draft claim. */
  public enum DraftClaimStatus {
    DRAFT,
    DELETED
  }

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "status", nullable = false, columnDefinition = "draft_claim_status")
  private DraftClaimStatus status;

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
