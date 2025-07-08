package espe.edu.ec.HealthAnalyzer.repository;

import espe.edu.ec.HealthAnalyzer.model.MedicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalAlertRepository extends JpaRepository<MedicalAlert, String> {
}