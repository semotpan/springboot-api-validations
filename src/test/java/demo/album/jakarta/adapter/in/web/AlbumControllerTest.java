package demo.album.jakarta.adapter.in.web;

import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static demo.album.DataSamples.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Tag("integration")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AlbumControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("Should create a new album successfully")
    void createNewAlbum() {
        // given
        var request = newValidRequest();

        // when
        var resp = postNewAlbum(request);

        // then
        assertThat(resp.getStatusCode())
                .isEqualTo(CREATED);

        // and
        assertThat(resp.getHeaders().getLocation())
                .isNotNull();
    }

    @Test
    @DisplayName("Should fail album creation when request has validation failures")
    void failCreationWhenRequestHasValidationFailures() throws JSONException {
        // given
        var request = newInvalidRequest();

        // when
        var resp = postNewAlbum(request);

        // then
        assertThat(resp.getStatusCode())
                .isEqualTo(UNPROCESSABLE_ENTITY);

        // and
        JSONAssert.assertEquals(expectedValidationFailure(), resp.getBody(), LENIENT);
    }

    private ResponseEntity<String> postNewAlbum(String request) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return restTemplate.postForEntity("/jakarta/albums", new HttpEntity<>(request, headers), String.class);
    }
}
