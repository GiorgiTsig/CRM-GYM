package com.epam.gymcrm.repository;

import com.epam.gymcrm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> getUsersByUsername(String username);

    @NonNull
    @Override
    <S extends User> S save(@NonNull S entity);

    void deleteUserByUsername(String username);
}
