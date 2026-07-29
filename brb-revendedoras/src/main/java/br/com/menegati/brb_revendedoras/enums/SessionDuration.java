package br.com.menegati.brb_revendedoras.enums;

import lombok.Getter;

import java.time.Duration;

public enum SessionDuration {

    // SESSION: JWT dura 8 horas, Cookie expira ao fechar o navegador (-1)
    SESSION(
            Duration.ofHours(8),
            -1
    ),

    // REMEMBERED: JWT e Cookie duram 30 dias
    REMEMBERED(
            Duration.ofDays(30),
            (int) Duration.ofDays(30).toSeconds()
    );

    @Getter
    private final Duration jwtExpiration;

    @Getter
    private final int cookieMaxAgeSeconds;

    SessionDuration(Duration jwtExpiration, int cookieMaxAgeSeconds) {
        this.jwtExpiration = jwtExpiration;
        this.cookieMaxAgeSeconds = cookieMaxAgeSeconds;
    }

    public static SessionDuration fromRememberMe(boolean rememberMe) {
        return rememberMe ? REMEMBERED : SESSION;
    }
}