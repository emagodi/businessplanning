package zw.co.zetdc.businessplanning.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import zw.co.zetdc.businessplanning.payload.request.MailBody;


@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailBody mailBody){

        SimpleMailMessage message =  new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom("magodiedwin@gmail.com");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        javaMailSender.send(message);

    }

}
