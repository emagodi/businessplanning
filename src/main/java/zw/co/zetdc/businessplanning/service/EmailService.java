package zw.co.zetdc.businessplanning.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import zw.co.zetdc.businessplanning.payload.request.MailBody;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom(fromEmail); // Use the email from properties
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        try {
            javaMailSender.send(message);
            logger.info("Email sent to: {}", mailBody.to());
        } catch (MailException e) {
            logger.error("Error while sending email to {}: {}", mailBody.to(), e.getMessage());
        }
    }
}