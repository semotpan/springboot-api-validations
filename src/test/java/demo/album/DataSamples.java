package demo.album;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class DataSamples {

    public static String newValidRequest() {
        return """
                {
                    "title": "Jeru",
                    "artist": "Gerry Mulligan",
                    "price": 17.99
                }
                """;
    }

    public static String newInvalidRequest() {
        return """
                {
                    "title": "",
                    "artist": null,
                    "price": -17.99
                }
                """;
    }

    public static String expectedValidationFailure() {
        return """
                {
                  "status": 422,
                  "errorCode": "UNPROCESSABLE_ENTITY",
                  "message": "Schema validation failure.",
                  "errors": [
                    {
                      "field": "title",
                      "message": "title cannot be blank."
                    },
                    {
                      "field": "artist",
                      "message": "artist cannot be blank."
                    },
                    {
                      "field": "price",
                      "message": "price must be greater than 0."
                    }
                    
                  ]
                }
                """;
    }
}
