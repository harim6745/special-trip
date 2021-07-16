package com.project.mega.triplus.repository;

import com.project.mega.triplus.entity.Place;
import com.project.mega.triplus.entity.Review;
import com.project.mega.triplus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    List<User> findAllByPlaceLikes(String liked);

    boolean existsNickNameByNickName(String nickName);

    boolean existsEmailByEmail(String email);
}
