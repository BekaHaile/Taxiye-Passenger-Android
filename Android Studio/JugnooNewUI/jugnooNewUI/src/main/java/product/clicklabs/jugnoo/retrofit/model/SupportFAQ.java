package product.clicklabs.jugnoo.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shankar on 1/20/16.
 */
public class SupportFAq {

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("questions_answers")
	@Expose
	private List<QuestionAnswer> questionAnswers;

	public SupportFAq(Integer id, String name, List<QuestionAnswer> questionAnswers) {
		this.id = id;
		this.name = name;
		this.questionAnswers = questionAnswers;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<QuestionAnswer> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}


	public class QuestionAnswer{
		@SerializedName("question")
		@Expose
		private String question;
		@SerializedName("answer")
		@Expose
		private String answer;

		public QuestionAnswer(String question, String answer) {
			this.question = question;
			this.answer = answer;
		}

		public String getQuestion() {
			return question;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public String getAnswer() {
			return answer;
		}

		public void setAnswer(String answer) {
			this.answer = answer;
		}
	}

}

