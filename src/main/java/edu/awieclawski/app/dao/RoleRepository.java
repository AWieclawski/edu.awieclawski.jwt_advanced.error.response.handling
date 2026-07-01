package edu.awieclawski.app.dao;

import edu.awieclawski.app.model.RoleEntity;
import edu.awieclawski.app.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    RoleEntity findByUserRole(UserRole userRole);
}
