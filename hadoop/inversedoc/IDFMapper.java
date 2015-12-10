package inversedoc;
/**
 * 
 * 
 * @author bharathi
 *
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.jsoup.Jsoup;

public class IDFMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
	@Override
	protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
		String document = value.toString();
		try {
			XMLStreamReader reader = XMLInputFactory.newInstance()
					.createXMLStreamReader(new ByteArrayInputStream(document.getBytes()));
			String pageText = "";// new Text();
			String pageTitle = "";// new Text();
			String pageId = "";// new Text();
			String currentElement = "";
			while (reader.hasNext()) {
				int code = reader.next();

				switch (code) {
				case XMLStreamConstants.START_ELEMENT: // START_ELEMENT:
					currentElement = reader.getLocalName();
					
					break;
				case XMLStreamConstants.CHARACTERS: // CHARACTERS:
					if (currentElement.equalsIgnoreCase("title")) {
						pageTitle += reader.getText();
					} else if (currentElement.equalsIgnoreCase("id") && pageId == "") {
						pageId = reader.getText();

					} else if (currentElement.equalsIgnoreCase("text")) {
						pageText += reader.getText();
					}
					break;
				case XMLStreamConstants.END_DOCUMENT:
					if (allElementsSet(pageText, pageTitle, pageId)) {
						sendcountWords(pageText.trim(), context);
						pageText = "";
						pageTitle = "";
						pageId = "";
					}

					break;
				}
			}
			reader.close();

		} catch (Exception e) {
			throw new IOException(e);

		}

	}

	public void sendcountWords(String text, Mapper.Context context) throws IOException, InterruptedException {
		String textOnly = Jsoup.parse(text.toString()).text();
		textOnly.replaceAll("  ", "");
		String newText=textOnly.replaceAll("[^a-zA-Z\\s-]", " ");
		String[] tokens = newText.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			if(!tokens[i].trim().isEmpty()) {
			String newToken=tokens[i].replaceAll("-", " ");
			context.write(new Text(newToken.toLowerCase()), new IntWritable(1));
			}
		}
	}

	public boolean allElementsSet(String pageText, String pageTitle, String pageId) {
		if ((pageText.length() == 0) || (pageTitle.length() == 0) || (pageId.length() == 0))
			return false;
		return true;
	}

}
