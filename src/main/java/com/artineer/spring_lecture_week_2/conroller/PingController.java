package com.artineer.spring_lecture_week_2.conroller;

import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.vo.ApiCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PingController {
    @GetMapping
    public ResponseEntity<Response<String>> ping() {
        Response<String> response = Response.<String>builder()
                .code(ApiCode.SUCCESS).data("pong").build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}