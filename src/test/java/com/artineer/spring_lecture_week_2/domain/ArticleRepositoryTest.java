package com.artineer.spring_lecture_week_2.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class ArticleRepositoryTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Test
    public void findByTitle_title_조회하면_일치하는_Article_객체들이_반환된다() {
        //given
        String javaStub = "Java";
        String pythonStub = "Python";

        articleRepository.save(Article.builder().title(javaStub).build());
        articleRepository.save(Article.builder().title(javaStub).build());
        articleRepository.save(Article.builder().title(pythonStub).build());

        //when
        List<Article> articles = articleRepository.findByTitle(javaStub);

        //then
        assertThat(articles.size()).isEqualTo(2);
        assertTrue(articles.stream().allMatch(a -> javaStub.equals(a.getTitle())), "title 필드 값이 모두" + javaStub + "인가?");
    }
}