package pig;

import java.io.IOException;

import org.apache.pig.data.DefaultTuple;
import org.junit.Test;

public class ScoreGenTest {

	@Test
	public void testScoreGen() throws IOException {
		ScoreGen scorer = new ScoreGen();
		String queryString = "{ \"text\": \"Who is the President of Unites States?\", \"words\": [{\"word\": \"President\",\"priority\": 1.5}, {\"word\": \"United\",\"priority\": 1.5}, {\"word\": \"States\",\"priority\": 1.5}],\"headWord\": \"the head word\",\"answerType\": \"the answerType\"}";

		String[] texts = new String[] {
				"barack obama barack United States president",
				"President of India",
				"Barack Obama is the President of the United States",
				"The President of India visited United States" };
		for (String text : texts) {
			DefaultTuple tuple = new DefaultTuple();
			tuple.append("mock title");
			tuple.append(text);
			tuple.append(queryString);

			scorer.exec(tuple);
		}
	}

}
