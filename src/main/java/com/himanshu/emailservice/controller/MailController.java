package com.himanshu.emailservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.himanshu.emailservice.model.EmailRequest;
import com.himanshu.emailservice.model.Mail;
import com.himanshu.emailservice.service.MailService;
import com.himanshu.emailservice.utils.CSVUtils;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/send")
    public CompletableFuture<ResponseEntity<String>> sendMail(@RequestBody EmailRequest request) {
        Mail mail = new Mail();
        mail.setMailTo(request.getMailTo());       
        mail.setMailFrom(request.getMailFrom());   
        mail.setMailSubject(request.getMailSubject()); 
        CompletableFuture<String> futureResult;
        if (mail.getMailTo() == null || mail.getMailTo().isEmpty()) {
            List<String> recipients = CSVUtils.readEmailsFromCSV("recipients.csv");
            futureResult=mailService.sendEmailInBatches(mail, request.getProducts(), recipients);

        } else {
            List<String> singleRecipient = new ArrayList<>();
            singleRecipient.add(request.getMailTo());
            futureResult=mailService.sendEmailInBatches(mail, request.getProducts(), singleRecipient);
        }
        // Wait for the result asynchronously
        return futureResult
            .thenApply(result -> {
                if (result.contains("successfully")) {
                    return ResponseEntity.ok(result);
                } else {
                    return ResponseEntity.status(500).body(result);
                }
            })
            .exceptionally(ex -> ResponseEntity.status(500).body("Error: " + ex.getMessage()));
    }
}
