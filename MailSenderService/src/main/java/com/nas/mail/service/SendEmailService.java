package com.nas.mail.service;

import com.nas.persistence.repository.ParameterRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class SendEmailService {
    @NonNull
    @Autowired
    private JavaMailSenderImpl mailSender;

    @NonNull
    @Autowired
    private ParameterRepository parameterRepository;

    public void sendEmail(String target, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            mimeMessage.setContent(body, "text/html");
            helper.setText(body, true);
            helper.setTo(target);
            helper.setSubject(subject);
            mailSender.setUsername(parameterRepository.findByName("mail.user").getValue());
            mailSender.setPassword(parameterRepository.findByName("mail.password").getValue());
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException e1) {
            e1.printStackTrace(); //NOSONAR
        }

    }
}