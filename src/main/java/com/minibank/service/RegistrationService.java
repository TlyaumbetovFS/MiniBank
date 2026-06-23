package com.minibank.service;

import com.minibank.dto.RegistrationInitRequest;
import com.minibank.dto.RegistrationResult;
import com.minibank.dto.SessionRequest;
import com.minibank.dto.VerifyRequest;
import com.minibank.entity.*;
import com.minibank.repository.AccountRepository;
import com.minibank.repository.OperationSessionRepository;
import com.minibank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final OperationSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public UUID init(RegistrationInitRequest req) {
        if (userRepository.existsByPhone(req.phone())) {
            throw new IllegalStateException("Phone already registered");
        }
        var session = new OperationSession();
        session.setPhone(req.phone());
        session.setOtpCode("1234");                       // заглушка OTP
        session.setType(SessionType.REGISTRATION);
        session.setStatus(SessionStatus.PENDING);
        session.setPayload(req.fullName() + "|" + req.passportData());
        sessionRepository.save(session);
        return session.getId();                           // это sessionId
    }

    @Transactional
    public void verify(VerifyRequest req) {
        var session = sessionRepository.findById(req.sessionId())
                .orElseThrow(() -> new IllegalStateException("Session not found"));
        if (!session.getOtpCode().equals(req.otpCode())) {
            throw new IllegalStateException("Invalid OTP");
        }
        session.setStatus(SessionStatus.CONFIRMED);
        sessionRepository.save(session);
    }

    @Transactional
    public RegistrationResult complete(SessionRequest req) {
        var session = sessionRepository.findById(req.sessionId())
                .orElseThrow(() -> new IllegalStateException("Session not found"));
        if (session.getStatus() != SessionStatus.CONFIRMED) {
            throw new IllegalStateException("Session not confirmed");
        }

        var payload = session.getPayload().split("\\|");

        var user = new User();
        user.setFullName(payload[0]);
        user.setPassportData(payload[1]);
        user.setPhone(session.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        var account = new Account();
        account.setAccountNumber(generateNumber(20));
        account.setBalance(BigDecimal.valueOf(1000));     // стартовый баланс
        account.setUser(user);
        accountRepository.save(account);

        sessionRepository.delete(session);

        return new RegistrationResult(user.getId(), account.getAccountNumber());
    }

    private String generateNumber(int length) {
        var sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }
}