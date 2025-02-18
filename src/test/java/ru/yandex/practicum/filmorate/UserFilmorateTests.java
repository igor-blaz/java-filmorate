package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.DurationAdapter;
import ru.yandex.practicum.filmorate.controller.LocalDateAdapter;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserFilmorateTests {
    private URI url;
    Gson gson;
    String userJson;
    HttpClient client;
    User user;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    public void setUp() {
        LocalDate date = LocalDate.now().minusYears(20);
        user = new User("@VasiliyVIP", "Vas1337", "Василий.К", date);
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/users");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();

        this.gson = gsonBuilder.create();

    }

    @Test
    public void postCorrectUserTest() throws IOException, InterruptedException {
        userJson = gson.toJson(user);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    public void postBadReleaseValidationUserTest() throws IOException, InterruptedException {
        user.setBirthday(LocalDate.MAX);
        userJson = gson.toJson(user);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
        assertThrows(ValidationException.class, () -> {
            throw new ValidationException("Дата релиза неверна");
        });
    }

    @Test
    public void postBadEmailValidationUserTest() throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i != 210) {
            sb.append("f");
            i++;
        }
        String description = sb.toString();
        user.setEmail("l ld d w 99");
        userJson = gson.toJson(user);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

    @Test
    public void putUserTest() throws IOException, InterruptedException {
        userJson = gson.toJson(user);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        user.setLogin("newLogin");
        user.setId(1);
        String userJsonTwo = gson.toJson(user);
        HttpRequest requestTwo = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(userJsonTwo)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTwo.statusCode());
    }

    @Test
    public void getUserTest() throws IOException, InterruptedException {
        userJson = gson.toJson(user);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestTwo = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(userJson)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestThree = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .GET().build();
        HttpResponse<String> responseThree = client.send(requestThree, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseThree.statusCode());

    }
}
