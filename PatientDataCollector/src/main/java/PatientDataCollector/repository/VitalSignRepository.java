package patientdatacollector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import patientdatacollector.model.VitalSign;

import java.util.List;

public interface VitalSignRepository extends JpaRepository<VitalSign, String> {
    List<VitalSign> findByDeviceId(String deviceId);
}
