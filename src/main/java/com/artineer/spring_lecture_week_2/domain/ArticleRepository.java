package com.artineer.spring_lecture_week_2.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    List<Article> findByTitle(String title);    //title명에 따라 조회
}
