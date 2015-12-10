package xmlparser;

/**
 * 
 * 
 * @author bharathi
 *
 */
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class CommonDataWritable implements Writable {

	private Text id;

	private Text text, title;

	public CommonDataWritable() {
		this.text = new Text();
		this.title = new Text();
		this.id = new Text();
	}

	public CommonDataWritable(Text textTitle, Text text, Text id) {
		this.text = text;
		this.title = textTitle;
		this.id = id;

	}

	public Text getId() {
		return id;
	}

	public Text getText() {
		return text;
	}

	public Text getTitle() {
		return title;
	}

	public void print() {
		System.out.println("Text" + this.text.toString());
		System.out.println("Title" + this.title.toString());
		System.out.println("Id" + this.id.toString());
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		text.readFields(in);
		title.readFields(in);
		id.readFields(in);
	}

	public void setId(Text id) {
		this.id = id;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public void setTitle(Text title) {
		this.title = title;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		if (text != null && title != null && id != null) {
			text.write(out);
			title.write(out);
			id.write(out);
		}

	}

}