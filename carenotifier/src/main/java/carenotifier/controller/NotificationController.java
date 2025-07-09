package carenotifier.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class NotificationController {
    @PostMapping("/email")
    public ResponseEntity<String> mockEmail(@RequestBody String message) {
        System.out.println("Simulación de correo enviado: " + message);
        return ResponseEntity.ok("Correo simulado enviado: " + message);
    }

    @PostMapping("/sms")
    public ResponseEntity<String> mockSms(@RequestBody String message) {
        System.out.println("Simulación de SMS enviado: " + message);
        return ResponseEntity.ok("SMS simulado enviado: " + message);
    }
}