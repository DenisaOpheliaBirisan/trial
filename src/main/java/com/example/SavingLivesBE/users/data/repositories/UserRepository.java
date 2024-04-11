package com.example.SavingLivesBE.users.data.repositories;

import com.example.SavingLivesBE.users.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findFirstByEmail(String email);
}

