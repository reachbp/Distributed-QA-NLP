package pig;

import java.io.IOException;

import org.apache.pig.FilterFunc;
import org.apache.pig.data.Tuple;
import org.codehaus.jackson.map.ObjectMapper;

import common.Query;
import common.TextUtils;
import common.Word;

/**
 * USAGE: query, article_title, article_text
 * 
 * @author sethia
 *
 */
// TODO 2 pig files

public class SatisfiesQuery extends FilterFunc {

	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Boolean exec(Tuple input) throws IOException {
		String article_title = input.get(0).toString();
		String article_text = input.get(1).toString();

		String queryString = input.get(2).toString();
		System.out.format(
				"----------------------INPUTS-------------------\nquery:%s\ntitle: %s\n"
				// + "text: %s\n"
						+ "--------------------------------------------\n",
				queryString, article_title);// , article_text);

		Query query = mapper.readValue(queryString, Query.class);
		boolean result = containsQuery(query, article_title)
				|| containsQuery(query, article_text);
		return result;
	}

	private boolean containsQuery(Query query, String text) {
		String cleanedText = TextUtils.cleaned(text);

		for (Word word : query.getWords()) {
			if (cleanedText.matches(TextUtils.getRegex(TextUtils.cleaned(word
					.getWord())))) {
				return true;
			} else if (word.getAlternatives() != null) {
				for (String alternative : word.getAlternatives()) {
					if (cleanedText.contains(TextUtils.getRegex(TextUtils
							.cleaned(alternative)))) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
