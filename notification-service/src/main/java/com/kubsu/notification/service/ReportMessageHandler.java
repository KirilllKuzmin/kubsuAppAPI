package com.kubsu.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuzmin.notification.config.RabbitMQConfig;
import com.kuzmin.notification.service.dto.ReportResponseDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ReportMessageHandler {

    private final JavaMailSender javaMailSender;

    @Autowired
    public ReportMessageHandler(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REPORT)
    public void receive(Message message) throws JsonProcessingException {
        System.out.println(message);

        byte[] body = message.getBody();
        String jsonBody = new String(body);

        ObjectMapper objectMapper = new ObjectMapper();
        ReportResponseDTO reportResponseDTO = objectMapper.readValue(jsonBody, ReportResponseDTO.class);
        System.out.println(reportResponseDTO);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(reportResponseDTO.getMail());
        mailMessage.setFrom("test@gmail.com");

        mailMessage.setSubject("Report");
        mailMessage.setText("Make report, sum: " + reportResponseDTO.getAmount());

        try {
            javaMailSender.send(mailMessage);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}
