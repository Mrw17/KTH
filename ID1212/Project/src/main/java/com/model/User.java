package com.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Entity
@Table(name="user")
public class User {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Pattern(regexp = "^[a-zA-Z0-9åäöÅÄÖ]*$")
    @NotNull
    @Size(min = 5, max = 45)
    @Column(name="username", unique = true)
    private String username;

    @NotNull
    @Size(min = 3, max = 70, message="Password needs to be at least 3 characters long")
    @Column(name="password")
    private String password;

    @NotNull
    @Email(message = "Please enter your email")
    @Size(min = 1 , message = "Please enter your email")
    @Column(name="email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name= "id", nullable = false,updatable = false,insertable = false)
    private Role role;

    /**
     * Returns the a role variable that is connected to the table Role
     * @return Role this returns the role
     */
    public Role getRole() {
        return role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

