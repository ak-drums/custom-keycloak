package kz.air.keycloak.spi.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_code")
@NamedQueries({
        @NamedQuery(name = "VerificationCode.findMostRecent",
                query = "SELECT t FROM VerificationCode t WHERE t.realmId = :realmId AND t.identifier = :identifier ORDER BY createdAt DESC LIMIT 1")
})
public class VerificationCode {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID();

    @Column(name = "realm_id", nullable = false)
    private String realmId;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "code", nullable = false)
    private String code;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "attempts")
    private Integer attempts = 0;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", realmId='" + realmId + '\'' +
                ", identifier='" + identifier + '\'' +
                ", code='" + code + '\'' +
                ", expiresAt=" + expiresAt +
                ", createdAt=" + createdAt +
                ", attempts=" + attempts +
                '}';
    }
}