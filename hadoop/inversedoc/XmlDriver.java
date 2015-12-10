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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class XmlDriver
{

public static void main(String[] args) throws Exception
{
    Configuration conf = new Configuration();

    conf.set("xmlinput.start", "<page>");
    conf.set("xmlinput.end", "</page>");
   // conf.set("mapreduce.map.java.opts","-Xmx1843M -Dfile.encoding=UTF-8");
    Job job = new Job(conf);
    job.setJarByClass(XmlDriver.class);
    job.setOutputKeyClass(NullWritable.class);
    job.setOutputValueClass(Text.class);
   // job.setCombinerClass(WikiCombiner.class);
    job.setMapperClass(IDFMapper.class);
    job.setReducerClass(IDFReducer.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(IntWritable.class);
    
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