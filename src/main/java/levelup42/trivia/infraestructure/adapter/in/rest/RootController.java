package levelup42.trivia.infraestructure.adapter.in.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    
    @GetMapping("/")
    public String healthCheck() {
        return "Trivia API is UP and Running!";
    }
}
