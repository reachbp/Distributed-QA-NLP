package inversedoc;
/**
 * 
 * 
 * @author bharathi
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.jsoup.Jsoup;

public class IDFReducer
extends Reducer<Text, IntWritable, NullWritable, Text> {
@Override
public void reduce(Text key, Iterable<IntWritable> values,
 Context context)
 throws IOException, InterruptedException {
	int total_num_documents=1;
	int wordCount=0;
	
	 for (IntWritable value : values) {
		 wordCount+= value.get();
	 }
 context.write(NullWritable.get(), new Text(key.toString().trim()+","+wordCount));
	}
}