package xmlparser.wikinews;
/**
 * 
 * 
 * @author bharathi
 *
 */
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.jsoup.Jsoup;

import xmlparser.CommonDataWritable;


public  class WikiNewsReducer
extends Reducer<Text, CommonDataWritable, Text, Text> {

    @Override
    protected void setup(
            Context context)
    throws IOException, InterruptedException {
      //  context.write(new Text("<?xml version="1.0" encoding="UTF-8"?>"),null);
    	context.write(new Text("<mediawiki>"), null);
    }

    @Override
    protected void cleanup(
            Context context)
    throws IOException, InterruptedException {
        context.write(new Text("</mediawiki>"), null);
    }

    private Text outputKey = new Text();
    public void reduce(Text key, Iterable<CommonDataWritable> values,
            Context context)
    throws IOException, InterruptedException {
        for (CommonDataWritable value : values) {
            outputKey.set(constructPropertyXml(value.getText(),value.getTitle(),value.getId()));
            context.write(outputKey, null);
        }
    }

    public static String constructPropertyXml(Text text, Text title,Text id) {
    	String textOnly = Jsoup.parse(title.toString()).text();
    	StringBuilder sb1=new StringBuilder();
    	boolean start=false;
    	boolean end=false;
    	for(int i =0;i<textOnly.length()-1;i++) {
    		if(!start)
    			sb1.append(textOnly.charAt(i));
    		if(textOnly.charAt(i)=='{' && textOnly.charAt(i+1)=='{')
    			start=true;
    		if(textOnly.charAt(i)=='}' && textOnly.charAt(i+1)=='}') {
    			start=false;	sb1.append(".");
    		}
    		
    	}
    	System.out.println(sb1.toString());
    	textOnly=sb1.toString();
    	String removedRedirects=textOnly.replaceAll("#REDIRECT", " ");
    	String removedSpecials=removedRedirects.replaceAll("[^a-zA-Z\\d\\s:\\-\\.]", " . ");
    	String removedExtraSpaces=removedSpecials.replaceAll(" ", " ");
    	
    	System.out.println(removedExtraSpaces);
        StringBuilder sb = new StringBuilder();
        sb.append("<page>").append("<title>").append(text.toString()).append("</title>")
        .append("<text>").append(removedExtraSpaces.toString()).append("</text>")
        .append("<id>").append(id.toString()).append("</id>").append("</page>");
        
        return sb.toString();
    }
    
    
}

