package com.nas.mail.listener;

import com.nas.mail.event.PreAuthenticationEvent;
import com.nas.mail.service.SendEmailService;
import com.nas.persistence.dto.OperationDTO;
import com.nas.persistence.model.User;
import com.nas.persistence.model.VerificationToken;
import com.nas.persistence.repository.VerificationTokenRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PreAuthenticationListener implements ApplicationListener<PreAuthenticationEvent> {
    @NonNull
    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @NonNull
    @Autowired
    SendEmailService sendEmailService;


    @Override
    public void onApplicationEvent(PreAuthenticationEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenRepository.save(new VerificationToken(token, event.getOperation(), user));

        StringBuilder emailTemplate = new StringBuilder();
        String emailTemplateString = "";
        String line;
        String urlPath = "";
        String subject = "";

        if (OperationDTO.types.REGISTER.toString().equals(event.getOperation())) {
            subject = "Registration confirm";
            urlPath = "/registerConfirm";
        } else if (OperationDTO.types.RESET_PASSWORD.toString().equals(event.getOperation())) {
            subject = "Reset password";
            urlPath = "/resetPassword";
        }

        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/templates" + urlPath + "Template.html"))) {
            urlPath += "?token=" + token;

            while ((line = br.readLine()) != null) {
                emailTemplate.append(line);
            }

            InetAddress inetAddress = InetAddress.getLocalHost();
            String url = " http://" + inetAddress.getHostAddress() + ":8080" + urlPath;
            emailTemplateString = emailTemplate.toString().replace("{:1}", url);
        } catch (IOException e) {
            e.printStackTrace(); //NOSONAR
        }

        sendEmailService.sendEmail(user.getEmail(), subject, emailTemplateString);
    }
}

    