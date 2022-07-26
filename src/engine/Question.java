package engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@JsonPropertyOrder({"id", "title", "text", "options"})
public class Question {
	@JsonIgnore
	public final int answer;
	public int id;
	public String title;
	public String text;
	public List<String> options;
	ObjectMapper objectMapper = new ObjectMapper();

	@JsonCreator
	public Question(@JsonProperty("answer") int answer,
	                @JsonProperty("title") String title,
	                @JsonProperty("text") String text,
	                @JsonProperty("options") List<String> options) {
		this.answer = answer;
		this.title = title;
		this.text = text;
		this.options = options;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Question{" +
				"answer=" + answer +
				", title='" + title + '\'' +
				", text='" + text + '\'' +
				", options=" + options +
				'}';
	}

	public String answer(int attempt) throws JsonProcessingException {
		@JsonPropertyOrder({"success", "feedback"})
		class Answer {
			public final boolean success;
			public final String feedback;

			public Answer(boolean success, String feedback) {
				this.success = success;
				this.feedback = feedback;
			}
		}
		if (attempt == answer) {
			Answer correctAnswer = new Answer(true, "Congratulations, you are right!");
			return objectMapper.writeValueAsString(correctAnswer);
		} else {
			Answer wrongAnswer = new Answer(false, "Wrong answer!, Please try again.");
			return objectMapper.writeValueAsString(wrongAnswer);
		}
	}
}
