/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package link.timon.tutorial.securerest.notes.domain.dto;

import java.time.LocalDateTime;
import link.timon.tutorial.securerest.notes.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User data-transfer-object for UI clients.
 *
 * @author Timon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserView {

    private String id;
    private String email;
    private String name;
    private LocalDateTime createdAt;

    public static UserView map(User user) {
        return Mapper.fromUser(user);
    }

    private static class Mapper {

        private static UserView fromUser(User user) {
            return UserView.builder()
                    .createdAt(user.getCreatedAt())
                    .email(user.getEmail())
                    .id(user.getId())
                    .name(user.getName())
                    .build();
        }

    }

}
