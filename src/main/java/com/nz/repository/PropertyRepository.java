package com.nz.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nz.entity.PropertyEntity;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {
    @Query("SELECT p FROM PropertyEntity p WHERE p.latitude BETWEEN :swLat AND :neLat AND p.longitude BETWEEN :swLng AND :neLng")
    List<PropertyEntity> findPropertiesWithinBounds(double swLat, double swLng, double neLat, double neLng);
    
    @Query("SELECT p FROM PropertyEntity p WHERE p.latitude BETWEEN :southWestLat AND :northEastLat AND p.longitude BETWEEN :southWestLng AND :northEastLng")
    List<PropertyEntity> findPropertiesWithin(@Param("southWestLat") double southWestLat, @Param("southWestLng") double southWestLng, @Param("northEastLat") double northEastLat, @Param("northEastLng") double northEastLng);

    Page<PropertyEntity> findByProcessingStatus(String status, Pageable pageable);
    
    @Query("SELECT p FROM PropertyEntity p WHERE " +
    	       "(:status IS NULL OR p.processingStatus = :status) AND (" +
    	       "(:filterField = 'propertyNum' AND p.propertyNum LIKE %:keyword%) OR " +
    	       "(:filterField = 'propertyAddress' AND p.propertyAddress LIKE %:keyword%) OR " +
    	       "(:filterField = 'buildingName' AND p.buildingName LIKE %:keyword%) OR " +
    	       "(:filterField = 'roomInfo' AND p.roomInfo LIKE %:keyword%))")
    	Page<PropertyEntity> findByStatusAndKeyword(@Param("status") String status,
    	                                            @Param("filterField") String filterField,
    	                                            @Param("keyword") String keyword,
    	                                            Pageable pageable);

    	@Query("SELECT p FROM PropertyEntity p WHERE " +
    	       "(:filterField = 'propertyNum' AND p.propertyNum LIKE %:keyword%) OR " +
    	       "(:filterField = 'propertyAddress' AND p.propertyAddress LIKE %:keyword%) OR " +
    	       "(:filterField = 'buildingName' AND p.buildingName LIKE %:keyword%) OR " +
    	       "(:filterField = 'roomInfo' AND p.roomInfo LIKE %:keyword%)")
    	Page<PropertyEntity> findByKeyword(@Param("filterField") String filterField,
    	                                   @Param("keyword") String keyword,
    	                                   Pageable pageable);
}