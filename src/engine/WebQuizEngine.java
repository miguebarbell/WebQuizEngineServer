package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
public class WebQuizEngine {

    public static void main(String[] args) {
        SpringApplication.run(WebQuizEngine.class, args);
    }

    Question question = new Question(
        1,
        "Mass Capital",
        "Which of the next cities is the capital of MA?",
        Arrays.asList("Salem", "Boston", "Houston", "Manchester"));

    @RestController
    public class QuizController {
        @GetMapping("/api/quiz")
        public String getQuiz() {
            ObjectMapper objectMapper = new ObjectMapper();
//            String jsonResponse;
//            Question question = new Question(
//                1,
//                "Mass Capital",
//                "Which of the next cities is the capital of MA?",
//                Arrays.asList("Salem", "Boston", "Houston", "Manchester"));
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(question);
//                return jsonResponse;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

        @PostMapping("/api/quiz")
        public String response(@RequestBody Map<String, Integer> response) throws JsonProcessingException {
            Integer answer = response.get("answer");
            return question.answer(answer);
        }
    }

}
