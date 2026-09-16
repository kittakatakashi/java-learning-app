package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/articles";
    }

    @GetMapping("/articles")
    public String list(Model model) {
        model.addAttribute("articles", articleService.findAll());
        return "articles/list";
    }

    @GetMapping("/articles/{id:[0-9]+}")
    public String detail(@PathVariable Long id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article", article);
        model.addAttribute("comments", commentService.findByArticle(article));
        return "articles/detail";
    }

    @GetMapping("/articles/new")
    public String newForm() {
        return "articles/form";
    }

    @PostMapping("/articles")
    public String create(@RequestParam String title,
                          @RequestParam String content,
                          Principal principal) {
        Article article = articleService.create(title, content, principal.getName());
        return "redirect:/articles/" + article.getId();
    }

    @GetMapping("/articles/{id:[0-9]+}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.findById(id));
        return "articles/form";
    }

    @PostMapping("/articles/{id:[0-9]+}/edit")
    public String update(@PathVariable Long id,
                          @RequestParam String title,
                          @RequestParam String content,
                          Principal principal) {
        articleService.update(id, title, content, principal.getName());
        return "redirect:/articles/" + id;
    }

    @PostMapping("/articles/{id:[0-9]+}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        articleService.delete(id, principal.getName());
        return "redirect:/articles";
    }

    @ModelAttribute
    public void addCurrentUsername(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("currentUsername", principal.getName());
        }
    }
}
