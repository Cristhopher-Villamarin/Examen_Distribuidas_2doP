package healthanalyzer.repository;

import healthanalyzer.model.MedicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalAlertRepository extends JpaRepository<MedicalAlert, String> {
    List<MedicalAlert> findByDeviceId(String deviceId);
}