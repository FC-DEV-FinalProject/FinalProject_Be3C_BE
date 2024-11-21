package com.be3c.sysmetic.domain;


import com.be3c.sysmetic.global.common.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Netlify API", description = "")
public class TestController {

    @GetMapping("/test")
    @Operation(
            summary = "테스트",
            description = "테스트"
    )
    public ResponseEntity<APIResponse<String >> testMethod(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success("Backend Server Communication Success!"));
    }
}
