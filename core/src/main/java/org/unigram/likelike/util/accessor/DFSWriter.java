package org.unigram.likelike.util.accessor;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Reducer.Context;

// TODO: Auto-generated Javadoc
/**
 * The Class DFSWriter.
 */
public class DFSWriter implements IWriter {

    /* (non-Javadoc)
     * @see org.unigram.likelike.util.accessor.IWriter#write(java.lang.Long, java.lang.Long, org.apache.hadoop.mapreduce.Reducer.Context)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean write(Long key, Long value, Context context) 
    throws Exception, InterruptedException, IOException {
        context.write(new LongWritable(key), new LongWritable(value));
        return true;
    }
    
    /**
     * Instantiates a new dFS writer.
     *
     * @param conf the conf
     */
    public DFSWriter(Configuration conf) {
            // nothing to do
    }

}
