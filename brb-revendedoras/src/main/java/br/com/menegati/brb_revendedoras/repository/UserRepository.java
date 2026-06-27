package br.com.menegati.brb_revendedoras.repository;

import br.com.menegati.brb_revendedoras.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCpf(String cpf);
    Optional<User> findByCpfOrEmail(String cpf, String email);

    boolean existsByCpf(String cpf);

    String cpf(String cpf);
}
