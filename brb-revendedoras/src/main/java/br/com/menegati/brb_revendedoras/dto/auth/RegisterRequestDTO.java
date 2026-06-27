package br.com.menegati.brb_revendedoras.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RegisterRequestDTO(String cpf,
                                 String password,
                                 String name,
                                 String email,
                                 String phone,
                                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
                                 LocalDate birthDate) {
}
