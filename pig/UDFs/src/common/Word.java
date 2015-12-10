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
@JsonPropertyOrder({ "word", "priority" })
public class Word {

	@JsonProperty("word")
	private String word;
	@JsonProperty("priority")
	private Double priority;
	@JsonProperty("alternatives")
	private List<String> alternatives = new ArrayList<String>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The word
	 */
	@JsonProperty("word")
	public String getWord() {
		return word;
	}

	/**
	 * 
	 * @param word
	 *            The word
	 */
	@JsonProperty("word")
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * 
	 * @return The priority
	 */
	@JsonProperty("priority")
	public Double getPriority() {
		return priority;
	}

	/**
	 * 
	 * @param priority
	 *            The priority
	 */
	@JsonProperty("priority")
	public void setPriority(Double priority) {
		this.priority = priority;
	}

	/**
	 * 
	 * @return The alternatives
	 */
	@JsonProperty("alternatives")
	public List<String> getAlternatives() {
		return alternatives;
	}

	/**
	 * 
	 * @param alternatives
	 *            The alternatives
	 */
	@JsonProperty("alternatives")
	public void setAlternatives(List<String> alternatives) {
		this.alternatives = alternatives;
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