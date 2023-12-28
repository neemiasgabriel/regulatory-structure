package com.picpay.entrypoint.api.config;

import com.picpay.entrypoint.api.dto.ErrorPresenter;
import com.picpay.entrypoint.api.dto.ResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Email enviado com sucesso",
        content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseDto.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request",
        content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorPresenter.class))),
    @ApiResponse(responseCode = "404", description = "Not Found",
        content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorPresenter.class))),
    @ApiResponse(responseCode = "408", description = "Request Timeout",
        content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorPresenter.class))),
    @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
        content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorPresenter.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
        content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorPresenter.class)))
})
public @interface ApiBasicResponses {
}