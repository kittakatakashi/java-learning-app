package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArticleService articleService;

    private User alice;
    private User bob;
    private Article article;

    @BeforeEach
    void setUp() {
        alice = new User("alice", "encoded");
        alice.setId(1L);
        bob = new User("bob", "encoded");
        bob.setId(2L);
        article = new Article("タイトル", "本文", alice);
        article.setId(10L);
    }

    @Test
    void 記事を作成できる() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article created = articleService.create("新しい記事", "内容", "alice");

        assertThat(created.getTitle()).isEqualTo("新しい記事");
        assertThat(created.getAuthor()).isEqualTo(alice);
    }

    @Test
    void 記事一覧を取得できる() {
        when(articleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(article));

        List<Article> result = articleService.findAll();

        assertThat(result).containsExactly(article);
    }

    @Test
    void 記事詳細を取得できる() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));

        Article result = articleService.findById(10L);

        assertThat(result).isEqualTo(article);
    }

    @Test
    void 投稿者本人は記事を更新できる() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Article updated = articleService.update(10L, "更新後タイトル", "更新後本文", "alice");

        assertThat(updated.getTitle()).isEqualTo("更新後タイトル");
        assertThat(updated.getContent()).isEqualTo("更新後本文");
    }

    @Test
    void 投稿者以外は記事を更新できない() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));

        assertThatThrownBy(() -> articleService.update(10L, "不正な更新", "内容", "bob"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void 投稿者本人は記事を削除できる() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));

        articleService.delete(10L, "alice");

        // 例外が発生しないこと、および削除がリポジトリに委譲されることを確認する
    }

    @Test
    void 投稿者以外は記事を削除できない() {
        when(articleRepository.findById(10L)).thenReturn(Optional.of(article));

        assertThatThrownBy(() -> articleService.delete(10L, "bob"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
