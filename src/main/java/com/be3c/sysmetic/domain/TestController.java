package com.be3c.sysmetic.domain;


import com.be3c.sysmetic.global.common.response.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    public ResponseEntity<APIResponse<String >> testMethod(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.success("Backend Server Communication Success!"));
    }
}
