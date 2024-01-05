package com.vicheak.coreapp.api.mail;

import jakarta.mail.MessagingException;

public interface MailService {

    void sendMail(Mail<?> mail) throws MessagingException;

}
