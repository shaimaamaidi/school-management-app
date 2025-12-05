package com.example.backend.repository;

import com.example.backend.entity.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {

    @Query(value = """
      select t from Token t inner join Admin u\s
      on t.admin.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByAdmin(Long id);

    Optional<Token> findByToken(String token);

    @Query(value = """
      select t from Token t inner join Admin u\s
      on t.admin.id = u.id\s
      where u.id = :id\s
      """)
    List<Token> findAllByAdmin(Long id);
}

