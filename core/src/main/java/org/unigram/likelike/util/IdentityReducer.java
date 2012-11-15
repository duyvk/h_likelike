package org.unigram.likelike.util;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

// TODO: Auto-generated Javadoc
/**
 * The Class IdentityReducer.
 */
public class IdentityReducer extends Reducer<LongWritable, Text, 
    LongWritable, Text> {
    
    /**
     * Reduce method.
     * @param key -
     * @param values -
     * @param context -
     * @throws IOException -
     * @throws InterruptedException -
     */
    @Override
    public void reduce(final LongWritable key,
            final Iterable<Text> values,
            final Context context)
            throws IOException, InterruptedException {
        for (Text v : values) {
            context.write(key, v);
        }
    }   
}
