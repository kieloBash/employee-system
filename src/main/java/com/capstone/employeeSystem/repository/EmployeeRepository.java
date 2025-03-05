package com.capstone.employeeSystem.repository;

import com.capstone.employeeSystem.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    Optional<Employee> findByEmployeeId(String employeeId);

    @Query("SELECT e FROM Employee e WHERE (:nameFilter IS NULL OR LOWER(e.name) LIKE %:nameFilter%)")
    Page<Employee> findEmployeesByFilters(@Param("nameFilter") String nameFilter, Pageable pageable);
}
