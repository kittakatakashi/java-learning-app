package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Article> findAll() {
        return articleRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("記事が見つかりません: " + id));
    }

    @Transactional
    public Article create(String title, String content, String authorUsername) {
        User author = findUserByUsername(authorUsername);
        Article article = new Article(title, content, author);
        return articleRepository.save(article);
    }

    @Transactional
    public Article update(Long articleId, String title, String content, String currentUsername) {
        Article article = findById(articleId);
        requireAuthor(article, currentUsername);
        article.setTitle(title);
        article.setContent(content);
        return articleRepository.save(article);
    }

    @Transactional
    public void delete(Long articleId, String currentUsername) {
        Article article = findById(articleId);
        requireAuthor(article, currentUsername);
        articleRepository.delete(article);
    }

    private void requireAuthor(Article article, String currentUsername) {
        if (!article.getAuthor().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("この記事を操作する権限がありません");
        }
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("ユーザーが見つかりません: " + username));
    }
}
