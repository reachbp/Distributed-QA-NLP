package xmlparser.wikinews;

/**
 * 
 * 
 * @author bharathi
 *
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import xmlparser.CommonDataWritable;
import xmlparser.XmlInputFormat;

public class WikiNewsDriver {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out
					.println("USAGE: <input wiki news dump xml> <output folder>");
			System.exit(-1);
		}
		Configuration conf = new Configuration();

		conf.set("xmlinput.start", "<page>");
		conf.set("xmlinput.end", "</page>");
		// conf.set("mapreduce.map.java.opts","-Xmx1843M -Dfile.encoding=UTF-8");
		Job job = new Job(conf);
		job.setJarByClass(WikiNewsDriver.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(WikiNewsMapper.class);
		// job.setMapperClass(WikiNewsMapper.class);
		job.setReducerClass(WikiNewsReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(CommonDataWritable.class);

		job.setInputFormatClass(XmlInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		Path outPath = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		FileSystem dfs = FileSystem.get(outPath.toUri(), conf);
		if (dfs.exists(outPath)) {
			dfs.delete(outPath, true);
		}
		job.waitForCompletion(true);
	}
}