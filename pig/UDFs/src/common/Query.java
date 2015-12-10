
package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "text", "words", "headWord", "answerType" })
public class Query {

	@JsonProperty("text")
	private String text;
	@JsonProperty("words")
	private List<Word> words = new ArrayList<Word>();
	@JsonProperty("headWord")
	private String headWord;
	@JsonProperty("answerType")
	private String answerType;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The text
	 */
	@JsonProperty("text")
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @param text
	 *            The text
	 */
	@JsonProperty("text")
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 
	 * @return The words
	 */
	@JsonProperty("words")
	public List<Word> getWords() {
		return words;
	}

	/**
	 * 
	 * @param words
	 *            The words
	 */
	@JsonProperty("words")
	public void setWords(List<Word> words) {
		this.words = words;
	}

	/**
	 * 
	 * @return The headWord
	 */
	@JsonProperty("headWord")
	public String getHeadWord() {
		return headWord;
	}

	/**
	 * 
	 * @param headWord
	 *            The headWord
	 */
	@JsonProperty("headWord")
	public void setHeadWord(String headWord) {
		this.headWord = headWord;
	}

	/**
	 * 
	 * @return The answerType
	 */
	@JsonProperty("answerType")
	public String getAnswerType() {
		return answerType;
	}

	/**
	 * 
	 * @param answerType
	 *            The answerType
	 */
	@JsonProperty("answerType")
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
