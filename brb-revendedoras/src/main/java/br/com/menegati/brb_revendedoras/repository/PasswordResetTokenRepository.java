package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.PasswordResetToken;
import br.com.menegati.brb_revendedoras.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserId(Long userId);

}
