package com.buddy.studybuddy.repositories;

import com.buddy.studybuddy.entities.Role;
import com.buddy.studybuddy.entities.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
//The Role repository interface
@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}