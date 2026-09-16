package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final ArticleService articleService;

    public CommentController(CommentService commentService, ArticleService articleService) {
        this.commentService = commentService;
        this.articleService = articleService;
    }

    @PostMapping("/articles/{articleId:[0-9]+}/comments")
    public String create(@PathVariable Long articleId,
                          @RequestParam String content,
                          Principal principal) {
        Article article = articleService.findById(articleId);
        commentService.create(article, content, principal.getName());
        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/articles/{articleId:[0-9]+}/comments/{commentId:[0-9]+}/delete")
    public String delete(@PathVariable Long articleId,
                          @PathVariable Long commentId,
                          Principal principal) {
        commentService.delete(commentId, principal.getName());
        return "redirect:/articles/" + articleId;
    }
}
