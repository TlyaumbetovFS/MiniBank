package com.minibank.service;

import com.minibank.dto.LoginInitRequest;
import com.minibank.dto.LoginResult;
import com.minibank.dto.VerifyRequest;
import com.minibank.entity.OperationSession;
import com.minibank.entity.SessionStatus;
import com.minibank.entity.SessionType;
import com.minibank.entity.UserStatus;
import com.minibank.repository.OperationSessionRepository;
import com.minibank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OperationSessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public UUID init(LoginInitRequest req) {
        var user = userRepository.findByPhone(req.phone())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User not active");
        }
        var session = new OperationSession();
        session.setPhone(req.phone());
        session.setOtpCode("1234");
        session.setType(SessionType.LOGIN);
        session.setStatus(SessionStatus.PENDING);
        session.setPayload("");                 // логину payload не нужен
        sessionRepository.save(session);
        return session.getId();
    }

    @Transactional
    public LoginResult confirm(VerifyRequest req) {
        var session = sessionRepository.findById(req.sessionId())
                .orElseThrow(() -> new IllegalStateException("Session not found"));
        if (session.getType() != SessionType.LOGIN) {
            throw new IllegalStateException("Wrong session type");
        }
        if (!session.getOtpCode().equals(req.otpCode())) {
            throw new IllegalStateException("Invalid OTP");
        }
        var user = userRepository.findByPhone(session.getPhone())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        session.setStatus(SessionStatus.CONFIRMED);
        sessionRepository.save(session);

        return new LoginResult(session.getId(), user.getId());
    }
}