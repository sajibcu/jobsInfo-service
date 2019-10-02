package com.redcode.jobsinfo.repository;



import com.redcode.jobsinfo.database.entity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findOneById(Long id);
}
