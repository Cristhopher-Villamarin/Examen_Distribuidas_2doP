package healthanalyzer.controller;

import healthanalyzer.model.MedicalAlert;
import healthanalyzer.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<MedicalAlert>> getAlertsByDeviceId(@PathVariable String deviceId) {
        List<MedicalAlert> alerts = alertService.getAlertsByDeviceId(deviceId);
        return ResponseEntity.ok(alerts);
    }
}
