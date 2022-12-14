package site.metacoding.bank.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.context.annotation.Profile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.AudingTime;

@NoArgsConstructor
@Getter
@Table(name = "users")
@Entity
public class User extends AudingTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserEnum role; // ADMIN, CUSTOMER

    @Builder
    public User(Long id, String username, String password, String email, UserEnum role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    @Profile("test")
    public void setMockData(Long id) {
        this.id = id;
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }
}