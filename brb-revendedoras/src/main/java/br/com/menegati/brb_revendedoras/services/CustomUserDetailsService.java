package br.com.menegati.brb_revendedoras.services;

import br.com.menegati.brb_revendedoras.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String cpfOrEmail) throws UsernameNotFoundException {
        return userRepository.findByCpfOrEmail(cpfOrEmail, cpfOrEmail).orElseThrow(()-> new UsernameNotFoundException("Usuário nao encontrado"));
    }
}
