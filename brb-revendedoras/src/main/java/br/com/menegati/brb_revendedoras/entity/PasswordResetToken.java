package br.com.menegati.brb_revendedoras.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "password_reset_token")
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "used",nullable = false)
    private boolean used = false;

    public PasswordResetToken(String token, User user, int expiryTimeInMinutes) {
        this.token = token;
        this.user = user;
        this.expirationDate = LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationDate);
    }

}
