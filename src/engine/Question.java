package engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "question")
@JsonPropertyOrder({"id", "title", "text", "options"})
public class Question {
	//	todo: should be include the user
	@JsonIgnore
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ElementCollection
//	@CollectionTable(name = "answer_table", joinColumns = @JoinColumn(name = "id"))
//	@Column(name = "answer")
	public List<Integer> answer = new ArrayList<>();
	@JsonIgnore
	public String email;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(nullable = false)
	public int id;
	@NotBlank
	@Column
	public String title;
	@NotBlank
	@Column
	public String text;
	@Size(min = 2, max = 4)
//	@Column(name = "options")
	@ElementCollection
//	@CollectionTable(name = "options_table", joinColumns = @JoinColumn(name = "id"))
	public List<String> options = new ArrayList<>();
	@JsonCreator
	public Question(@JsonProperty("answer") List<Integer> answer,
	                @JsonProperty("title") String title,
	                @JsonProperty("text") String text,
	                @JsonProperty("options") List<String> options) {
		if (answer != null && answer.size() > 4) {
			System.out.println("bad request for answer");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		if (options == null || options.size() < 2 || options.size() > 4) {
			System.out.println("bad request for options");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		this.answer = answer;
		this.title = title;
		this.text = text;
		this.options = options;
	}

	public Question() {

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

//	public List<Integer> getAnswer() {
//		return answer;
//	}

	//	public void setId(int id) {
//		this.id = id;
//	}

	@Override
	public String toString() {
		return "Question{" +
				"answer=" + answer +
				", title='" + title + '\'' +
				", text='" + text + '\'' +
				", options=" + options +
				'}';
	}

	public String answer(List<Integer> attempt) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		@JsonPropertyOrder({"success", "feedback"})
		class Answer {
			public final boolean success;
			public final String feedback;

			public Answer(boolean success, String feedback) {
				this.success = success;
				this.feedback = feedback;
			}
		}
		if ((answer == null && attempt == null) || (attempt != null && attempt.size() == 0 && answer == null) || attempt.equals(answer)) {
			System.out.println("Right answer = " + answer);
			System.out.println("Right attempt = " + attempt);
			Answer correctAnswer = new Answer(true, "Congratulations, you are right!");
			return objectMapper.writeValueAsString(correctAnswer);
		} else {
			System.out.println("Wrong answer = " + answer);
			System.out.println("Wrong attempt = " + attempt);
			Answer wrongAnswer = new Answer(false, "Wrong answer!, Please try again.");
			return objectMapper.writeValueAsString(wrongAnswer);
		}
	}
}
