package com.vicheak.coreapp.api.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long>, JpaSpecificationExecutor<Video> {

    Optional<Video> findByUuid(String uuid);

    boolean existsByTitleIgnoreCase(String title);

}
