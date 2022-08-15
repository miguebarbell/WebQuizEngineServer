package engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
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
		UserRepository userRepository;
		@Autowired
		PasswordEncoder encoder;
		@Autowired
		private QuestionRepository questionRepository;

		@GetMapping("/api/quizzes/{id}")
		public String getQuiz(@PathVariable int id) {
			System.out.println("GET /api/quizzes/{id} " + id);
			Optional<Question> questionFound = questionRepository.findById(id);
			System.out.println("questionFound = " + questionFound);
			if (questionFound.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			try {
				// fixme: @JsonIgnore not working for optional.
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
		public String response(@RequestBody Map<String, List<Integer>> response, @PathVariable int id,
		                       @RequestHeader Map<String, String> headers) throws JsonProcessingException {
			System.out.println("POST /api/quizzes/{id}/solve id " + id);
			System.out.println("headers.toString() = " + headers.toString());
			List<Integer> answer = response.get("answer");
			Optional<Question> questionFound = questionRepository.findById(id);
			if (questionFound.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			System.out.println("questionFound json post");
			return questionFound.get().answer(answer);
		}

		@PostMapping(value = "/api/quizzes/{id}/solve", consumes = "application/x-www-form-urlencoded")
		public String responseForm(List<Integer> answer, @PathVariable int id, @RequestHeader Map<String, String> headers) throws JsonProcessingException {
			System.out.println("POST /api/quizzes/{id}/solve x-www-form id " + id);
			System.out.println("headers.toString() = " + headers.toString());
			Optional<Question> questionFound = questionRepository.findById(id);
			if (questionFound.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			System.out.println("questionFound x-www-form-urlencoded");
			return questionFound.get().answer(answer);
		}

		@PostMapping(value = "/api/quizzes")
		public String newQuestion(@RequestBody Question newQuestion, Principal principal) {
			System.out.println("POST /api/quizzes " + newQuestion.toString());
//			System.out.println("saving new question = " + newQuestion);
//			System.out.println("saving new question with id = " + newQuestion.getId());
			try {
				newQuestion.setEmail(principal.getName());
				questionRepository.save(newQuestion);
				System.out.println("saved new question = " + newQuestion);
				return objectMapper.writeValueAsString(newQuestion);
//				return objectMapper.writeValueAsString(newQuestion);
			} catch (JsonProcessingException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		}

		@GetMapping(value = "/api/quizzes")
		public String getAllQuestions(@RequestHeader Map<String, String> header, Principal principal) {
			System.out.println("GET /api/quizzes from " + principal.getName());
			System.out.println("header = " + header.toString());
			System.out.println("principal.getName() = " + principal.getName());
			System.out.println("principal.toString() = " + principal);
			try {
				System.out.println("quizzes found " + objectMapper.writeValueAsString(questionRepository.findAll()));
				System.out.println("reached here");
				return objectMapper.writeValueAsString(questionRepository.findAll());
			} catch (JsonProcessingException e) {
				System.out.println("[Error] JsonProcessingException " + e);
				throw new RuntimeException(e);
			}
		}

		// testing reasons
		@PostMapping(value = "/actuator/shutdown")
		public String shutdown() {
			System.out.println("shutting down");
			return "Shutdown";
		}

		@PostMapping(value = "/api/register")
		public String register(@RequestBody User user) {
			System.out.println("registering user = " + user.toString());
			if (userRepository.findUserByUsername(user.getEmail()) == null) {
				user.setPassword(encoder.encode(user.getPassword()));
				try {
					userRepository.saveUser(user);
					return objectMapper.writeValueAsString(user);
				} catch (JsonProcessingException e) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		}

		@DeleteMapping(value = "/api/quizzes/{id}")
		@ResponseStatus(value = HttpStatus.NO_CONTENT)
		public void deleteQuiz(@PathVariable int id, Principal principal) {
			String email = principal.getName();
//			System.out.println("email = " + email);
//			System.out.println("deleting id = " + id);
			Optional<Question> questionFound = questionRepository.findById(id);
			if (questionFound.isPresent()) {
				if (email.equals(questionFound.get().getEmail())) {
					questionRepository.deleteById(id);
				} else {
					throw new ResponseStatusException(HttpStatus.FORBIDDEN);
				}
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
		}
	}

}
