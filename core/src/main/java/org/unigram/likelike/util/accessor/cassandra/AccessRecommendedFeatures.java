package org.unigram.likelike.util.accessor.cassandra;

import java.util.Map;

import me.prettyprint.cassandra.service.PoolExhaustedException;

import org.apache.cassandra.thrift.NotFoundException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.unigram.likelike.common.LikelikeConstants;
import org.unigram.likelike.util.accessor.CassandraWriter;
import org.unigram.likelike.util.accessor.IReader;
import org.unigram.likelike.util.accessor.IWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class AccessRecommendedFeatures.
 */
public class AccessRecommendedFeatures implements IReader, IWriter {
	
	/**
	 * Instantiates a new access recommended features.
	 *
	 * @param conf the conf
	 */
	public AccessRecommendedFeatures(Configuration conf) {
        conf.set(LikelikeConstants.CASSANDRA_COLUMNFAMILY_NAME, 
        		LikelikeConstants.LIKELIKE_CASSANDRA_FEATURE_EXTRACTION_COLUMNFAMILY_NAME);        
		try {
			this.writer = new CassandraWriter(conf);
		} catch (PoolExhaustedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.unigram.likelike.util.accessor.IReader#read(java.lang.Long)
	 */
	@Override
	public Map<String, byte[]> read(Long key) throws Exception,
			InterruptedException {
		return this.writer.read(key);
	}

	/* (non-Javadoc)
	 * @see org.unigram.likelike.util.accessor.IWriter#write(java.lang.Long, java.lang.Long, org.apache.hadoop.mapreduce.Reducer.Context)
	 */
	@Override
	public boolean write(Long key, Long value, Context context)
			throws Exception, InterruptedException {
		return this.writer.write(key, value, context);
	}

	/**
	 * Instantiates a new access recommended features.
	 */
	private AccessRecommendedFeatures() {
		// for safe
	}
	
	/** The writer. */
	private CassandraWriter writer;
}
