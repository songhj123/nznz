package com.nz.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.nz.entity.DisinfectionStatusEntity;

public interface DisinfectionStatusRepository extends JpaRepository<DisinfectionStatusEntity, Long> {
    @Query("SELECT d FROM DisinfectionStatusEntity d ORDER BY d.updatedAt DESC")
    List<DisinfectionStatusEntity> findAllOrderByUpdatedAtDesc();

    Page<DisinfectionStatusEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    
    DisinfectionStatusEntity findTopByOrderByUpdatedAtDesc();
}
