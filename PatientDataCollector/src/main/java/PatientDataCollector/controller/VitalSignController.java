package PatientDataCollector.controller;

import PatientDataCollector.dto.VitalSignRequest;
import PatientDataCollector.dto.VitalSignResponse;
import PatientDataCollector.service.VitalSignService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vital-signs")
public class VitalSignController {
    private final VitalSignService service;

    public VitalSignController(VitalSignService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VitalSignResponse> createVitalSign(@Valid @RequestBody VitalSignRequest request) {
        VitalSignResponse response = service.saveVitalSign(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<VitalSignResponse>> getVitalSigns(@PathVariable String deviceId) {
        List<VitalSignResponse> vitalSigns = service.getVitalSignsByDeviceId(deviceId);
        return ResponseEntity.ok(vitalSigns);
    }
}