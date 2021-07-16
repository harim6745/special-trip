package com.project.mega.triplus.repository;

import com.project.mega.triplus.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // 임시
    Optional<Place> findById(Long id);

    // Liked 내림차순 처음 6개
    List<Place> findFirst6ByOrderByLikedDesc();

    // 카테고리별 타입 아이디
    List<Place> findAllByContentType(String type);

    Place findByContentId(String contentId);

    Page<Place> findAllByContentType(Pageable pageable, String type);

    Page<Place> findAllByContentTypeAndAreaCode(Pageable pageable, String type, String areaCode);

}
