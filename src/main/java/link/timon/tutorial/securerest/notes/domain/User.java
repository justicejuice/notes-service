package link.timon.tutorial.securerest.notes.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This model represents a user and functions as mongo db document.
 *
 * @author Timon
 */
@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private boolean enabled;

    @Indexed
    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    @Builder.Default
    private Set<Role> authorities = new HashSet<>();

    @DocumentReference
    private List<Note> notes;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

}
