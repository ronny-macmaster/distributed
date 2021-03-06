package edu.utexas.ronny.driver;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.utexas.ronny.map.WordCountMapper;
import edu.utexas.ronny.reduce.WordCountReducer;

/**
 * @author Ronald Macmaster
 * Driver for the word count job. 
 * Uses a map, combine, reduce to provide a word count histogram of the input.
 */
public class WordCountDriver extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		System.exit(ToolRunner.run(new WordCountDriver(), args));
	}
	
	/* (non-Javadoc)
	 * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
	 */
	@Override
	public int run(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.format("usage: %s {input} {output} %n", WordCountDriver.class.getSimpleName());
			System.exit(1);
		}
		
		// configure job
		Configuration config = getConf();
		String reduceKey = "mapred.reduce.tasks";
		System.err.format("%s : %s %n", reduceKey, config.get(reduceKey));
		Job job = Job.getInstance(config, "WordCount");
		job.setJarByClass(WordCountDriver.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		FileSystem.get(config).delete(new Path(args[1]), true);
		
		//	System.out.println(System.getenv("HADOOP_CONF_DIR"));
		//	System.out.println(System.getenv("HADOOP_HOME"));
		//	System.out.println(System.getenv("HADOOP_CLASSPATH"));
		
		job.setMapperClass(WordCountMapper.class);
		job.setCombinerClass(WordCountReducer.class);
		job.setReducerClass(WordCountReducer.class);
		job.setNumReduceTasks(3);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
}
