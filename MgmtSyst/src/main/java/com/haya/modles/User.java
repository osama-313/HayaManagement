package com.haya.modles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

@Entity()
@Table(name = "all_users")
public class User {

    @Id
    @GeneratedValue(generator = "custemer_id", strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "custemer_id", allocationSize = 1)
    @Column(name = "userid" , updatable = false)
    private int id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "user_name", nullable = false , unique = true)
    private String userName;

    @Column(name = "email", nullable = false , unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "age" , nullable = false)
    private int age;

    @Column(name = "proficiency")
    private String proficiency;

    @JoinColumn(name = "user_role" , nullable = false)
    @ManyToOne
    private Role role;

    public User() {
    }

    public User(int id, String firstName, String lastName, String userName, String email, String password, int age, String proficiency, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.proficiency = proficiency;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProficiency() {
        return proficiency;
    }

    public void setProficiency(String proficiency) {
        this.proficiency = proficiency;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
