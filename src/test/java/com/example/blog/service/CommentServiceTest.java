package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.CommentRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User alice;
    private User bob;
    private Article article;
    private Comment comment;

    @BeforeEach
    void setUp() {
        alice = new User("alice", "encoded");
        alice.setId(1L);
        bob = new User("bob", "encoded");
        bob.setId(2L);
        article = new Article("タイトル", "本文", alice);
        article.setId(10L);
        comment = new Comment("コメント本文", alice, article);
        comment.setId(100L);
    }

    @Test
    void 記事に紐づくコメント一覧を取得できる() {
        when(commentRepository.findByArticleOrderByCreatedAtAsc(article)).thenReturn(List.of(comment));

        List<Comment> result = commentService.findByArticle(article);

        assertThat(result).containsExactly(comment);
    }

    @Test
    void コメントを投稿できる() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment created = commentService.create(article, "新しいコメント", "bob");

        assertThat(created.getContent()).isEqualTo("新しいコメント");
        assertThat(created.getAuthor()).isEqualTo(bob);
        assertThat(created.getArticle()).isEqualTo(article);
    }

    @Test
    void 投稿者本人はコメントを削除できる() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        commentService.delete(100L, "alice");

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void 投稿者以外はコメントを削除できない() {
        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.delete(100L, "bob"))
                .isInstanceOf(AccessDeniedException.class);

        verify(commentRepository, times(0)).delete(any(Comment.class));
    }
}
