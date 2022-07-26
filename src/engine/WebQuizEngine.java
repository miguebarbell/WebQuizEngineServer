package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class WebQuizEngine {

	//	Question question = new Question(
//			2,
//			"Mass Capital",
//			"Which of the next cities is the capital of MA?",
//			Arrays.asList("Salem", "Houston", "Boston", "Manchester"));
	List<Question> questions = new ArrayList<>();


	public static void main(String[] args) {
		SpringApplication.run(WebQuizEngine.class, args);
	}

	@RestController
	public class QuizController {

		ObjectMapper objectMapper = new ObjectMapper();

		@GetMapping("/api/quizzes/{id}")
		public String getQuiz(@PathVariable int id) {
			Question questionFound = null;
			for (Question question : questions) {
				if (question.id == id) {
					questionFound = question;
				}
			}

			if (questionFound == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			try {
				return objectMapper.writeValueAsString(questionFound);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

		}

		@PostMapping(value = "/api/quizzes/{id}/solve")
		public String response(@RequestBody Map<String, Integer> response, @PathVariable int id) throws JsonProcessingException {
			int answer = response.get("answer");
			Question questionFound = null;
			for (Question question : questions) {
				if (question.id == id) {
					questionFound = question;
				}
			}
			if (questionFound == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			return questionFound.answer(answer);
		}

		@PostMapping(value = "/api/quizzes/{id}/solve", consumes = "application/x-www-form-urlencoded")
		public String responseForm(String answer, @PathVariable int id) throws JsonProcessingException {
			// fixme: this is duplicated
			Question questionFound = null;
			for (Question question : questions) {
				if (question.id == id) {
					questionFound = question;
				}
			}
			if (questionFound == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			return questionFound.answer(Integer.parseInt(answer));
		}

		@PostMapping(value = "/api/quizzes")
		public String newQuestion(@RequestBody Question newQuestion) {
			newQuestion.setId(questions.size() + 1);
			questions.add(newQuestion);
			try {
				return objectMapper.writeValueAsString(newQuestion);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		@GetMapping(value = "/api/quizzes")
		public String getAllQuestions() {
			try {
				return objectMapper.writeValueAsString(questions);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
