package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class WebQuizEngine {
	public static void main(String[] args) {
		SpringApplication.run(WebQuizEngine.class, args);
	}

	@RestController
	public static class QuizController {
		ObjectMapper objectMapper = new ObjectMapper();
		@Autowired
		private QuestionRepository questionRepository;

		@GetMapping("/api/quizzes/{id}")
		public String getQuiz(@PathVariable int id) {
			Optional<Question> questionFound = questionRepository.findById(id);
			System.out.println("questionFound = " + questionFound);
			if (questionFound.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			try {
				// fixme: @JsonIgnore not working for optinal.
				Map<String, Object> map = new HashMap<>();
				map.put("id", questionFound.get().getId());
				map.put("text", questionFound.get().text);
				map.put("options", questionFound.get().options);
				map.put("title", questionFound.get().title);
				return objectMapper.writeValueAsString(map);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

		}

		@PostMapping(value = "/api/quizzes/{id}/solve")
		public String response(@RequestBody Map<String, List<Integer>> response, @PathVariable int id) throws JsonProcessingException {
			List<Integer> answer = response.get("answer");
			Optional<Question> questionFound = questionRepository.findById(id);
			if (questionFound.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			System.out.println("questionFound json post");
			return questionFound.get().answer(answer);
		}

		@PostMapping(value = "/api/quizzes/{id}/solve", consumes = "application/x-www-form-urlencoded")
		public String responseForm(List<Integer> answer, @PathVariable int id) throws JsonProcessingException {
			Optional<Question> questionFound = questionRepository.findById(id);
			if (questionFound.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			System.out.println("questionFound x-www-form-urlencoded");
			return questionFound.get().answer(answer);
		}

		@PostMapping(value = "/api/quizzes")
		public String newQuestion(@RequestBody Question newQuestion) {
			System.out.println("saving new question = " + newQuestion);
			System.out.println("saving new question with id = " + newQuestion.getId());
			try {
				questionRepository.save(newQuestion);
				System.out.println("saved new question = " + newQuestion);
				return objectMapper.writeValueAsString(newQuestion);
//				return objectMapper.writeValueAsString(newQuestion);
			} catch (JsonProcessingException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		}

		@GetMapping(value = "/api/quizzes")
		public String getAllQuestions() {
			try {
				return objectMapper.writeValueAsString(questionRepository.findAll());
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
