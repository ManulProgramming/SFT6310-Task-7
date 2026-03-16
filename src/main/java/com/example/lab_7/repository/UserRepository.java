package com.example.lab_7.repository;

import com.example.lab_7.model.FullUser;
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
public class UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void createTable() {
        jdbcTemplate.getJdbcOperations().execute("""
        CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            name VARCHAR(50) NOT NULL UNIQUE,
            password VARCHAR(161)
        )
        """);
        jdbcTemplate.getJdbcOperations().execute("""
        CREATE TABLE IF NOT EXISTS profile (
            id BIGINT PRIMARY KEY,
            biography VARCHAR(2500) NOT NULL,
            FOREIGN KEY (id) REFERENCES users(id)
        );
        """);
    }
    public FullUser insertUser(FullUser user) {
        String sql = "INSERT INTO users (name, password) VALUES (:name, :password)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("password", user.getPassword());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        if (rowsAffected != 1) {
            throw new DataAccessResourceFailureException("Could not insert user");
        }
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        sql = "INSERT INTO profile (id, biography) VALUES (:id, :biography)";
        params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("biography", user.getBiography());
        rowsAffected = jdbcTemplate.update(sql, params);
        if (rowsAffected != 1) {
            throw new DataAccessResourceFailureException("Could not insert user");
        }
        return user;
    }
    public FullUser getUserById(long id) {
        String sql = "SELECT * FROM users INNER JOIN profile ON users.id = profile.id WHERE users.id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(FullUser.class));
    }
    public FullUser getUserByName(String name) {
        String sql = "SELECT * FROM users WHERE name = :name";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(FullUser.class));
    }
    public List<FullUser> getAllUsers(Integer page, Integer size) {
        String sql = "SELECT * FROM users INNER JOIN profile ON users.id = profile.id LIMIT :size OFFSET :page";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("size", size)
                .addValue("page", page*size);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(FullUser.class));
    }
    public void updateUser(FullUser user) {
        String set_query = "";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId());
        if (user.getName() != null) {
            set_query+=" name = :name ";
            params.addValue("name", user.getName());
        }
        if (user.getPassword() != null) {
            if (!set_query.isEmpty()) {
                set_query+=",";
            }
            set_query+=" password = :password ";
            params.addValue("password", user.getPassword());
        }
        String sql = "";
        if (!set_query.isEmpty()) {
            sql = "UPDATE users SET" + set_query + "WHERE id = :id";
            jdbcTemplate.update(sql, params);
        }
        if (user.getBiography() != null) {
            sql = "UPDATE profile SET biography = :biography WHERE id = :id";
            params = new MapSqlParameterSource()
                    .addValue("id", user.getId())
                    .addValue("biography", user.getBiography());
            jdbcTemplate.update(sql, params);
        }
    }
    public void deleteUser(long id) {
        String sql = "DELETE FROM profile WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
        sql = "DELETE FROM users WHERE id = :id";
        params = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(sql, params);
    }
}
