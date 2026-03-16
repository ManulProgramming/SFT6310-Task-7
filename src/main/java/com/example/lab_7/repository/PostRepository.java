package com.example.lab_7.repository;

import com.example.lab_7.model.Post;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PostRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public PostRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void createTable() {
        jdbcTemplate.getJdbcOperations().execute("""
        CREATE TABLE IF NOT EXISTS posts (
            id SERIAL PRIMARY KEY,
            userId BIGINT NOT NULL,
            title VARCHAR(100) NOT NULL,
            content VARCHAR(2500) NOT NULL,
            FOREIGN KEY (userId) REFERENCES users(id)
        );
        """);
    }
    public Post insertPost(Post post) {
        String sql = "INSERT INTO posts (userId, title, content) VALUES (:userId, :title, :content)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", post.getUserId())
                .addValue("title", post.getTitle())
                .addValue("content", post.getContent());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        if (rowsAffected != 1) {
            throw new DataAccessResourceFailureException("Could not insert post");
        }
        post.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return post;
    }
    public Post getPostById(long id) {
        String sql = "SELECT * FROM posts WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Post.class));
    }
    public List<Post> getAllPosts(Integer page, Integer size) {
        String sql = "SELECT * FROM posts ORDER BY id DESC LIMIT :size OFFSET :page";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("page", page*size);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Post.class));
    }
    public void updatePost(Post post) {
        String set_query = "";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", post.getId());
        if (post.getTitle() != null) {
            set_query+=" title = :title ";
            params.addValue("title", post.getTitle());
        }
        if (post.getContent() != null) {
            if (!set_query.isEmpty()) {
                set_query+=",";
            }
            set_query+=" content = :content ";
            params.addValue("content", post.getContent());
        }
        if (!set_query.isEmpty()) {
            String sql = "UPDATE posts SET"+set_query+"WHERE id = :id";
            jdbcTemplate.update(sql, params);
        }
    }
    public void deletePost(long id) {
        String sql = "DELETE FROM posts WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }
}
