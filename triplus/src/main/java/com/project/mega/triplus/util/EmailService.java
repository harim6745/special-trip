package com.project.mega.triplus.util;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
