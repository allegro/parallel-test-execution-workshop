package pl.allegro.tech.workshops.testsparallelexecution.email.rest;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/{id}")
    public Email createEmail(@PathVariable String id) {
        return emailService.getEmail(id);
    }

    @PostMapping()
    public void createEmail(@Valid @RequestBody Email email) {
        emailService.sendEmail(email);
    }

}
