package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.LocalDateAdapter;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class FilmorateApplicationTests {
    private URI url;
    Gson gson;
    String filmJson;
    HttpClient client;
    Film film;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    public void setUp() {
        LocalDate date = LocalDate.now().minusYears(20);
        Integer duration = 220;
        film = new Film("KinDzaDza", "description", date, duration);
        client = HttpClient.newHttpClient();
        url = URI.create("http://localhost:8080/films");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        this.gson = gsonBuilder.create();

    }

    @Test
    public void postCorrectFilmTest() throws IOException, InterruptedException {
        filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Film film3 = gson.fromJson(filmJson, Film.class);
        System.out.println(film3.getDuration());
    }

    @Test
    public void postBadReleaseValidationFilmTest() throws IOException, InterruptedException {
        film.setReleaseDate(LocalDate.MIN);
        filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertThrows(ValidationException.class, () -> {
            throw new ValidationException("Дата релиза неверна");
        });
    }

    @Test
    public void postBadDescriptionValidationFilmTest() throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i != 210) {
            sb.append("f");
            i++;
        }
        String description = sb.toString();
        film.setDescription(description);
        filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void putFilmTest() throws IOException, InterruptedException {
        filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        film.setDescription("newDescription");
        film.setId(1);
        String filmJsonTwo = gson.toJson(film);
        HttpRequest requestTwo = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString(filmJsonTwo)).build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseTwo.statusCode());
    }

    @Test
    public void getFilmTest() throws IOException, InterruptedException {
        filmJson = gson.toJson(film);
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestTwo = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/json")
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(filmJson)).build();
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



