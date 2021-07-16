package com.project.mega.triplus.service;

import com.project.mega.triplus.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.PreRemove;
import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @PreRemove
    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
