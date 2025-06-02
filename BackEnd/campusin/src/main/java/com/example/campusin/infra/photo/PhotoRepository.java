package com.example.campusin.infra.photo;

import com.example.campusin.domain.photo.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by kok8454@gmail.com on 2023-05-07
 * Github : http://github.com/perArdua
 */
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
