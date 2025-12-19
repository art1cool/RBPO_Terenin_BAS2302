package repository;

import entity.UserSessionEntity;
import enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, UUID> {

    Optional<UserSessionEntity> findByRefreshTokenAndStatus(String refreshToken, SessionStatus status);

    Optional<UserSessionEntity> findByRefreshToken(String refreshToken);

    List<UserSessionEntity> findByUserIdAndStatus(UUID userId, SessionStatus status);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.status = :status WHERE s.expiresAt < :now")
    void expireExpiredSessions(@Param("status") SessionStatus status, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM UserSessionEntity s WHERE s.expiresAt < :expirationTime")
    void deleteExpiredSessions(@Param("expirationTime") LocalDateTime expirationTime);

    Optional<UserSessionEntity> findByUserIdAndDeviceInfo(UUID userId, String deviceInfo);

    Optional<UserSessionEntity> findByUserIdAndDeviceInfoAndStatus(UUID userId, String deviceInfo, SessionStatus status);

    List<UserSessionEntity> findByUserId(UUID userId);
}