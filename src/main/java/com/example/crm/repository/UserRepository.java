package com.example.crm.repository;

import com.example.crm.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Колонка role служит дискриминатором, поэтому ищем обычным SQL. Роль передается строкой: ADMIN, MANAGER, CUSTOMER. */
    @Query(value = "select * from users where role = :role order by id", nativeQuery = true)
    List<User> findByRole(@Param("role") String role);
}
