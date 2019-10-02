package com.redcode.jobsinfo.repository;

import com.redcode.jobsinfo.database.entity.model.Role;
import com.redcode.jobsinfo.database.entity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneById(Long id);

    List<User> findAllByUserType(Role userType);

}
