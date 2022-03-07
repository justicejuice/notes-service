package link.timon.tutorial.securerest.notes.controller;

import link.timon.tutorial.securerest.notes.common.RestConstants;
import link.timon.tutorial.securerest.notes.domain.dto.UserLoginRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserRegisterRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserView;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Since this is a Integration test for jwt functionality, we create a whole Web Environment and mongo container.
 *
 * @author Timon
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private static final String URL_FORMAT = "http://localhost:%d/%s/users%s";

    @Container
    private static final MongoDBContainer container = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldRegister() {
        // Act
        ResponseEntity<UserView> result = register("testuser@gmail.com", "abcdefg", "Testmaster");

        // Assert
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody().getId()).isNotBlank();
    }

    @Test
    public void shouldNotRegisterExistingEmail() {
        // Act
        register("testuser@gmail.com", "abcdefg", "Testmaster");
        ResponseEntity<UserView> result = register("testuser@gmail.com", "abcdefg", "Testmaster");

        // Assert
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        Assertions.assertThat(result.getBody().getId()).isBlank();
    }

    @Test
    public void shouldLogin() {
        // Act
        ResponseEntity<UserView> registered = register("user1@mail.de", "asdasd", "Hans");
        ResponseEntity<UserView> result = login("user1@mail.de", "asdasd");

        // Assert
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody().getId()).isEqualTo(registered.getBody().getId());
    }

    @Test
    public void shouldNotLoginUserNotFound() {
        // Act
        ResponseEntity<UserView> result = login("fail@failmail.de", "asdasdasd");

        // Assert
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotLoginWrongPassword() {
        // Act
        ResponseEntity<UserView> registered = register("user2@mail.de", "asdasdasdasd", "Hans");
        ResponseEntity<UserView> result = login("user2@mail.de", "wrong pw");

        // Assert
        Assertions.assertThat(registered.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void shouldDelete() {
        register("test2@gmail.com", "asdf", "Hanswurst");
        ResponseEntity<UserView> user = login("test2@gmail.com", "asdf");

        String token = user.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        ResponseEntity<Void> result = delete(user.getBody().getId(), token);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void shouldNotDeleteOtherUser() {
        ResponseEntity<UserView> user1 = register("test3@gmail.com", "asdf", "Hanswurst");
        ResponseEntity<UserView> user2 = register("test4@gmail.com", "asdfg", "Testuser");
        ResponseEntity<UserView> userLoggedIn = login("test3@gmail.com", "asdf");

        String token = userLoggedIn.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        ResponseEntity<Void> result = delete(user2.getBody().getId(), token);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Void> delete(String userId, String token) {
        String url = getUrl(String.format("/%s", userId));
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, token);
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
    }

    private ResponseEntity<UserView> login(String mail, String password) {
        String loginUrl = getUrl("/login");

        UserLoginRequestDto loginRequest = UserLoginRequestDto.builder()
                .email(mail)
                .password(password)
                .build();

        return restTemplate.postForEntity(loginUrl, loginRequest, UserView.class);
    }

    private ResponseEntity<UserView> register(String mail, String password, String name) {
        String registerUrl = getUrl("/register");

        UserRegisterRequestDto registerRequest = UserRegisterRequestDto.builder()
                .email(mail)
                .password(password)
                .name(name)
                .build();

        return restTemplate.postForEntity(registerUrl, registerRequest, UserView.class);
    }

    private String getUrl(String endpoint) {
        return String.format(URL_FORMAT, port, RestConstants.API_V1, endpoint);
    }

}
