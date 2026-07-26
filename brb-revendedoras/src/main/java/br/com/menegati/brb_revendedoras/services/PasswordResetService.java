package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.entity.PasswordResetToken;
import br.com.menegati.brb_revendedoras.entity.User;
import br.com.menegati.brb_revendedoras.exception.BusinessException;
import br.com.menegati.brb_revendedoras.exception.ForbiddenException;
import br.com.menegati.brb_revendedoras.exception.ResourceNotFoundException;
import br.com.menegati.brb_revendedoras.repository.PasswordResetTokenRepository;
import br.com.menegati.brb_revendedoras.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final int EXPIRATION_TIME_IN_MINUTES = 15;

    @Transactional
    public String createPasswordResetToken(User user) {
        String newTokenString = UUID.randomUUID().toString();

        PasswordResetToken token = passwordResetTokenRepository.findByUserId(user.getId()).orElse(
                new PasswordResetToken()
        );
        token.setToken(newTokenString);
        token.setUser(user);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(EXPIRATION_TIME_IN_MINUTES));

        passwordResetTokenRepository.save(token);

        return newTokenString;
    }

    public boolean isTokenValid(PasswordResetToken passwordResetToken) {
        return !passwordResetToken.isExpired() && !passwordResetToken.isUsed();
    }
}
