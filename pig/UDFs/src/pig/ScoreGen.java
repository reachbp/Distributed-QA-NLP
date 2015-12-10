package pig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.codehaus.jackson.map.ObjectMapper;

import common.Query;
import common.TextUtils;
import common.Word;

public class ScoreGen extends EvalFunc<Float> {
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Float exec(Tuple input) throws IOException {
		String title = input.get(0).toString();
		String text = input.get(1).toString();
		String queryString = input.get(2).toString();

		Query query = mapper.readValue(queryString, Query.class);

		float result = getProximityScore(query, title) * 2
				+ getProximityScore(query, text);
		return result;
	}

	private float getProximityScore(Query query, String text) {
		text = TextUtils.cleaned(text);
		String[] textWords = text.split("\\s+");

		List<String> wordsSatisfyingQuery = getWordsInText(query, text);
		String[] matchedQueryWords = new String[0];

		matchedQueryWords = wordsSatisfyingQuery.toArray(matchedQueryWords);

		float result = 0;
		if (matchedQueryWords.length > 1) {
			float slopDistance = (minWindow(textWords, matchedQueryWords) - matchedQueryWords.length);
			if (slopDistance == 0) {
				result = matchedQueryWords.length + 1.05f;
			} else {
				// function graph at:
				// https://www.desmos.com/calculator/mqos6tvh52 (slide "a" to
				// see changes)
				final float a = matchedQueryWords.length;
				final float slope = -1 / ((2 * a) - 1);
				final float intercept = -1 * (2 * (float) Math.pow(a, 2) + a)
						* slope;
				System.out.printf(
						"slop: %f, a: %f, slope: %f, intercept: %f\n",
						slopDistance, a, slope, intercept);

				result = slope * slopDistance + intercept;
			}
		} else if (matchedQueryWords.length == 1) {
			return 2;
		}

		return result;
	}

	private double logBase(float base, float term) {
		return Math.log(term) / Math.log(base);
	}

	private List<String> getWordsInText(Query query, String cleanedText) {
		List<String> results = new ArrayList<String>();

		for (Word word : query.getWords()) {
			String cleanedWord = TextUtils.cleaned(word.getWord());
			if (cleanedText.matches(TextUtils.getRegex(cleanedWord))) {
				results.add(cleanedWord);
			}
			if (word.getAlternatives() != null) {
				for (String alternative : word.getAlternatives()) {
					String cleanedAlternative = TextUtils.cleaned(alternative);
					if (cleanedText.matches(TextUtils
							.getRegex(cleanedAlternative))) {
						results.add(cleanedAlternative);
					}
				}
			}
		}

		return results;
	}

	/**
	 * Solution to https://leetcode.com/problems/minimum-window-substring/
	 */
	private static int minWindow(String[] textWords, String[] queryWords) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < queryWords.length; i++) {
			if (map.containsKey(queryWords[i]))
				map.put(queryWords[i], map.get(queryWords[i]) + 1);
			else
				map.put(queryWords[i], 1);
		}

		int start = -1, end = textWords.length;

		LinkedList<Integer> queue = new LinkedList<Integer>();
		int left = queryWords.length;
		for (int i = 0; i < textWords.length; i++) {
			String c = textWords[i];
			if (!map.containsKey(c))
				continue;
			int n = map.get(c);
			map.put(c, n - 1);
			queue.add(i);
			if (n > 0)
				left--;
			String head = textWords[queue.peek()];
			while (map.get(head) < 0) {
				queue.poll();
				map.put(head, map.get(head) + 1);
				head = textWords[queue.peek()];
			}
			if (left == 0) {
				int new_length = queue.peekLast() - queue.peek() + 1;
				if (new_length < end - start) {
					start = queue.peek();
					end = queue.peekLast() + 1;
				}
			}
		}
		if (left == 0)
			return end - start;

		return Integer.MAX_VALUE;
	}

	/*
	 * private HashMap<String,Float> getIDFWeights(DataBag idf_values) throws
	 * ExecException, NumberFormatException { int totalNumberOfDocuments=100;
	 * HashMap<String,Float> idfWeights=new HashMap<String,Float>(); for
	 * (Iterator<Tuple> iterator = idf_values.iterator(); iterator.hasNext();) {
	 * Tuple tuple = iterator.next(); idfWeights.put((String) tuple.get(0),
	 * (float) Math.log(Integer.parseInt((String)
	 * tuple.get(1))/totalNumberOfDocuments)); } return idfWeights; } private
	 * float getOccuranceScore(Query query, String text,HashMap<String,Float>
	 * idfWeights) {
	 * 
	 * text = TextUtils.cleaned(text); String[] textWords = text.split("\\s+");
	 * List<String> wordsSatisfyingQuery = getWordsInText(query, text);
	 * HashMap<String,Integer> termfrequencies=new HashMap<String,Integer>();
	 * for(String eachSatisfiedWord:wordsSatisfyingQuery)
	 * termfrequencies.put(eachSatisfiedWord, 0); int
	 * total_number=textWords.length;
	 * 
	 * for(int i=0;i<textWords.length;i++) {
	 * if(termfrequencies.containsKey(textWords[i]))
	 * termfrequencies.put(textWords[i], termfrequencies.get(textWords[i])+1); }
	 * float occurenceScore=0F; for(String
	 * eachSatisfiedWord:wordsSatisfyingQuery) { String term=eachSatisfiedWord;
	 * int tfVal=termfrequencies.get(term); float idfVal=idfWeights.get(term);
	 * occurenceScore=tfVal*idfVal; } return occurenceScore; }
	 */
	
}
