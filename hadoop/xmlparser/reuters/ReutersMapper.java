package xmlparser.reuters;

/**
 * 
 * 
 * @author bharathi
 *
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import xmlparser.CommonDataWritable;

public class ReutersMapper extends
		Mapper<LongWritable, Text, Text, CommonDataWritable> {
	@Override
	protected void map(LongWritable key, Text value, Mapper.Context context)
			throws IOException, InterruptedException {
		String document = value.toString();
		// System.out.println("‘" + document + "‘");
		try {
			XMLStreamReader reader = XMLInputFactory
					.newInstance()
					.createXMLStreamReader(
							new ByteArrayInputStream(document.getBytes("UTF-8")));
			String pageText = "";// new Text();
			String pageTitle = "";// new Text();
			//CHANGE 1
			String pageId = UUID.randomUUID().toString();// "";// new Text();
			String currentElement = "";
			while (reader.hasNext()) {
				int code = reader.next();

				switch (code) {
				case XMLStreamConstants.START_ELEMENT: // START_ELEMENT:
					currentElement = reader.getLocalName();
					System.out.println();
					break;
				case XMLStreamConstants.CHARACTERS: // CHARACTERS:
					//CHANGE 2
					if (currentElement.equalsIgnoreCase("text/title")) {

						pageTitle += reader.getText();
					}			//CHANGE 3
					/*
					 * else if (currentElement.equalsIgnoreCase("id") && pageId
					 * == "") {
					 * 
					 * pageId = reader.getText();
					 * 
					 * }
					 */
					//CHANGE 4
					else if (currentElement.equalsIgnoreCase("text/body")) {

						pageText += reader.getText();

					}
					break;
				case XMLStreamConstants.END_DOCUMENT:
					if (allElementsSet(pageText, pageTitle, pageId)) {
						/*
						 * System.out.println("text" + pageText + " title" +
						 * pageTitle + " id" + pageId);
						 */
						pageId = "reuters_" + pageId;
						CommonDataWritable next = new CommonDataWritable(
								new Text(pageText.trim()), new Text(
										pageTitle.trim()), new Text(
										pageId.trim()));
						context.write(new Text(), next);
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

	public boolean allElementsSet(String pageText, String pageTitle,
			String pageId) {
		if ((pageText.length() == 0) || (pageTitle.length() == 0)
				|| (pageId.length() == 0))
			return false;
		return true;
	}

}
