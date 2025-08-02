package com.openjoyer.marketplace.profile_service.repository;

import com.openjoyer.marketplace.profile_service.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByConfirmationToken(String confirmationToken);

    boolean existsByEmail(String email);

}
