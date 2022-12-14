package site.metacoding.bank.dto.user;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.bank.config.enums.UserEnum;
import site.metacoding.bank.domain.user.User;

public class UserReqDto {
    @Setter
    @Getter
    public static class UserJoinReqDto {
        private String username;
        private String password;
        private String email;

        public User toEntity(UserEnum userEnum) {
            return User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(userEnum)
                    .build();
        }
    }
}
