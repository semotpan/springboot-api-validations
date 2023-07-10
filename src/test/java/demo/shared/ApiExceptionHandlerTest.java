package demo.shared;

import demo.shared.ApiExceptionHandlerTest.Config.TestResource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.HttpHeaders.ALLOW;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
    Error Response Format:
    ---------------------
    {
      "timestamp": "2022-07-23T15:04:04.320363Z",
      "status": 400,
      "errorCode": "BAD_REQUEST",
      "message": "Malformed JSON request"
      "errors" : []
    }
 */

@Tag("integration")
@WebMvcTest(controllers = TestResource.class)
@Import(ApiExceptionHandler.class)
class ApiExceptionHandlerTest {

    private static final String ISO_8601 = "^\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d{3,}([+-][0-2]\\d(:?[0-5]\\d)?|Z)$";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void messageNotReadable() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 400,
                  "errorCode": "BAD_REQUEST",
                  "message": "Malformed JSON request"
                }""";

        mockMvc.perform(post("/test-url2")
                        .contentType(APPLICATION_JSON)
                        .content("{\"value\": "))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @Test
    void typeMismatch() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 400,
                  "errorCode": "BAD_REQUEST",
                  "message": "Type mismatch request"
                }""";

        mockMvc.perform(post("/test-url3/1234")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(CONTENT_TYPE, is(APPLICATION_JSON_VALUE)))
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @Test
    void notFound() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 404,
                  "errorCode": "NOT_FOUND",
                  "message": "Resource '/unknown' not found"
                }""";

        mockMvc.perform(get("/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(header().string(CONTENT_TYPE, is(APPLICATION_JSON_VALUE)))
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @Test
    void methodNotSupported() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 405,
                  "errorCode": "METHOD_NOT_ALLOWED",
                  "message": "Request method 'POST' not supported"
                }""";

        mockMvc.perform(post("/test-url"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(CONTENT_TYPE, is(APPLICATION_JSON_VALUE)))
                .andExpect(header().string(ALLOW, is(GET.name())))
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @Test
    void mediaTypeNotAcceptable() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 406,
                  "errorCode": "NOT_ACCEPTABLE",
                  "message": "Could not find acceptable representation"
                }""";

        mockMvc.perform(get("/test-url")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andExpect(header().string(CONTENT_TYPE, is(APPLICATION_JSON_VALUE)))
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @Test
    void mediaTypeNotSupported() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 415,
                  "errorCode": "UNSUPPORTED_MEDIA_TYPE",
                  "message": "Content type 'application/xml' not supported"
                }""";

        mockMvc.perform(post("/test-url3/" + UUID.randomUUID())
                        .contentType(APPLICATION_XML))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string(CONTENT_TYPE, is(APPLICATION_JSON_VALUE)))
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @Test
    void internalServerError() throws Exception {
        var lenientJSONRes = """
                {
                  "status": 500,
                  "errorCode": "INTERNAL_SERVER_ERROR",
                  "message": "An unexpected error occurred"
                }""";

        mockMvc.perform(post("/test-url3/" + UUID.randomUUID())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string(CONTENT_TYPE, is(APPLICATION_JSON_VALUE)))
                .andExpect(content().json(lenientJSONRes))
                .andExpect(jsonPath("$.timestamp").value(matchesPattern(ISO_8601)));
    }

    @TestConfiguration
    static class Config {

        @RestController
        static class TestResource {

            @GetMapping(path = "/test-url", produces = TEXT_HTML_VALUE)
            String get() {
                return "test";
            }

            @GetMapping(path = "/test-url2", produces = APPLICATION_JSON_VALUE)
            ResponseEntity<?> getQuery(Request request) {
                return ResponseEntity.ok("{}");
            }

            @PostMapping(path = "/test-url2", consumes = APPLICATION_JSON_VALUE)
            void post(@RequestBody Request request) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @PostMapping(path = "/test-url3/{id}", consumes = APPLICATION_JSON_VALUE)
            void post(@PathVariable UUID id) {
                throw new UnsupportedOperationException("Not implemented");
            }
        }

        record Request(Integer value, String value2) {
        }
    }
}
