package com.br.pwsorvete.repository;

import com.br.pwsorvete.model.Sorvete;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SorveteRepository extends JpaRepository<Sorvete, Long> {

    List<Sorvete> findByIsDeletedIsNull();
}
