package patientdatacollector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import patientdatacollector.dto.VitalSignDto;
import patientdatacollector.model.VitalSign;
import patientdatacollector.service.VitalSignService;

import java.util.List;

@RestController
@RequestMapping("/vital-signs")
public class VitalSignsController {
    @Autowired
    private VitalSignService vitalSignService;

    @PostMapping
    public ResponseEntity<Void> receiveVitalSign(@RequestBody VitalSignDto dto) {
        vitalSignService.saveVitalSign(dto);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<VitalSign>> getVitalSignsByDeviceId(@PathVariable String deviceId) {
        List<VitalSign> vitalSigns = vitalSignService.getVitalSignsByDeviceId(deviceId);
        return ResponseEntity.ok(vitalSigns);
    }
}
