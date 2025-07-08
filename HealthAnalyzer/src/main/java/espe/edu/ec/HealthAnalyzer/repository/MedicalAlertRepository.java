package espe.edu.ec.HealthAnalyzer.repository;

import espe.edu.ec.HealthAnalyzer.model.MedicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface MedicalAlertRepository extends JpaRepository<MedicalAlert, Long> {
    @Query("SELECT DISTINCT m.deviceId FROM MedicalAlert m")
    List<String> findDistinctDeviceIds();

    @Query("SELECT m FROM MedicalAlert m WHERE m.deviceId = :deviceId AND m.timestamp >= :since")
    List<MedicalAlert> findByDeviceIdAndTimestampAfter(String deviceId, Instant since);
}