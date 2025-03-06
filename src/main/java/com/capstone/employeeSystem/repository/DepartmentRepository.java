package com.capstone.employeeSystem.repository;

import com.capstone.employeeSystem.model.Department;
import com.capstone.employeeSystem.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Optional<Department> findByName(String name);

}
