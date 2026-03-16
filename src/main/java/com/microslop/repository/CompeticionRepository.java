package com.microslop.repository;

import com.microslop.entity.Competicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompeticionRepository extends JpaRepository<Competicion, Long> {
}