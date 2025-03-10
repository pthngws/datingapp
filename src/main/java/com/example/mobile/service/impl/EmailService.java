package com.example.mobile.service.impl;

import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        // Kiểm tra định dạng email cơ bản
        if (toEmail == null || !toEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new AppException(ErrorCode.EMAIL_INVALID);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã OTP của bạn");
        message.setText("Mã OTP của bạn là: " + otp +
                "\nVui lòng không chia sẻ mã này với ai khác.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED, "Gửi email thất bại: " + e.getMessage());
        }
    }
}