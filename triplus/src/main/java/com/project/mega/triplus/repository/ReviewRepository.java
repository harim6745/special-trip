package com.project.mega.triplus.repository;

import com.project.mega.triplus.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

//    @Query("delete from review where id = ?")
//    boolean deleteById(Long id);
}
