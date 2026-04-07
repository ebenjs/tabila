package com.tabila.backend.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScalarController {

    @GetMapping(value = { "/scalar", "/scalar/" }, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String scalar() {
        return """
                <!doctype html>
                <html>
                  <head>
                    <meta charset=\"UTF-8\" />
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                    <title>Tabila API Reference</title>
                  </head>
                  <body>
                    <script id=\"api-reference\" data-url=\"/v3/api-docs\"></script>
                    <script src=\"https://cdn.jsdelivr.net/npm/@scalar/api-reference\"></script>
                  </body>
                </html>
                """;
    }
}
