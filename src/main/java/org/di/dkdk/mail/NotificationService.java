package org.di.dkdk.mail;

import org.di.dkdk.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.di.dkdk.service.UserService;

import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class NotificationService {

    private final UserService userService;
    private final JavaMailSender mailSender;

    @Value("${org.di.dkdk.bot.email.subject}")
    private String emailSubject;

    @Value("${org.di.dkdk.bot.email.from}")
    private String emailFrom;

    @Value("${org.di.dkdk.bot.email.to}")
    private String emailTo;

    public NotificationService(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 10000)//каждые 10 сек проверяет есть ли новые пользователи и отправляет данные о них на емаил
    public void sendNewApplications() {
        List<User> users = userService.findNewUsers();
        if (users.size() == 0)
            return;

        StringBuilder sb = new StringBuilder();

        users.forEach(user -> sb.append("Phone: ")
                .append(user.getPhone())
                .append("\r\n")
                .append("Email: ")
                .append(user.getEmail())
                .append("\r\n\r\n")
        );

        sendEmail(sb.toString());
    }

    private void sendEmail(String text){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(emailTo);
        mailMessage.setFrom(emailFrom);
        mailMessage.setSubject(emailSubject);
        mailMessage.setText(text);

        mailSender.send(mailMessage);
    }

}
