package xmlparser.wikinews;

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

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import xmlparser.CommonDataWritable;

public class WikiNewsMapper extends
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
			String pageId = "";// new Text();
			String currentElement = "";
			while (reader.hasNext()) {
				int code = reader.next();

				switch (code) {
				case XMLStreamConstants.START_ELEMENT: // START_ELEMENT:
					currentElement = reader.getLocalName();
					System.out.println();
					break;
				case XMLStreamConstants.CHARACTERS: // CHARACTERS:
					if (currentElement.equalsIgnoreCase("title")) {

						pageTitle += reader.getText();
					} else if (currentElement.equalsIgnoreCase("id")
							&& pageId == "") {

						pageId = reader.getText();

					} else if (currentElement.equalsIgnoreCase("text")) {

						pageText += reader.getText();

					}
					break;
				case XMLStreamConstants.END_DOCUMENT:
					if (allElementsSet(pageText, pageTitle, pageId)
							&& !pageTitle.contains(":")) {
						System.out.println("text" + pageText + " title"
								+ pageTitle + " id" + pageId);
						
						pageId = "wikinews_" + pageId;
						
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
