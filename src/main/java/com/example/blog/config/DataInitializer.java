package com.example.blog.config;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                            ArticleRepository articleRepository,
                            CommentRepository commentRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User alice = userRepository.save(new User("alice", passwordEncoder.encode("password")));
        User bob = userRepository.save(new User("bob", passwordEncoder.encode("password")));
        User carol = userRepository.save(new User("carol", passwordEncoder.encode("password")));
        User dave = userRepository.save(new User("dave", passwordEncoder.encode("password")));

        Article a1 = articleRepository.save(new Article(
                "Spring Bootを始めよう",
                "Spring Bootは設定より規約を重視したフレームワークです。今日はプロジェクトの立ち上げ方を紹介します。",
                alice));
        Article a2 = articleRepository.save(new Article(
                "JPAでハマったこと",
                "遅延ロードとN+1問題に苦しんだ話。フェッチ戦略の見直しで解決しました。",
                alice));
        Article a3 = articleRepository.save(new Article(
                "Spring Securityの基本",
                "フォームログインとセッション認証の仕組みをおさらいします。",
                bob));
        Article a4 = articleRepository.save(new Article(
                "Thymeleafのレイアウト機能",
                "th:fragmentとth:replaceを使った画面共通化のパターンを紹介します。",
                bob));
        Article a5 = articleRepository.save(new Article(
                "鉄フライパンを育てる",
                "個人開発の話ですが、鉄フライパンの育成もコツコツ続けるという意味ではプログラミングに似ています。",
                carol));
        Article a6 = articleRepository.save(new Article(
                "初めての社内LT登壇",
                "緊張しましたが、Spring Bootの魅力を伝えられたと思います。",
                dave));

        commentRepository.save(new Comment("参考になりました！", bob, a1));
        commentRepository.save(new Comment("私も同じところでつまずきました", carol, a1));
        commentRepository.save(new Comment("N+1問題、あるあるですね", dave, a2));
        commentRepository.save(new Comment("フォームログインの図解が欲しいです", alice, a3));
        commentRepository.save(new Comment("次回はJWTも解説してほしいです", carol, a3));
        commentRepository.save(new Comment("フラグメント便利ですよね", dave, a4));
        commentRepository.save(new Comment("育成、応援してます", alice, a5));
        commentRepository.save(new Comment("お疲れ様でした！", bob, a6));
    }
}
