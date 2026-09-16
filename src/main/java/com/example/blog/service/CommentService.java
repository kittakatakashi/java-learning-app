package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Comment> findByArticle(Article article) {
        return commentRepository.findByArticleOrderByCreatedAtAsc(article);
    }

    @Transactional
    public Comment create(Article article, String content, String authorUsername) {
        User author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new NoSuchElementException("ユーザーが見つかりません: " + authorUsername));
        Comment comment = new Comment(content, author, article);
        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long commentId, String currentUsername) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("コメントが見つかりません: " + commentId));
        if (!comment.getAuthor().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("このコメントを削除する権限がありません");
        }
        commentRepository.delete(comment);
    }
}
