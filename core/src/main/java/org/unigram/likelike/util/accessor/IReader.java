package org.unigram.likelike.util.accessor;

import java.util.Map;

import org.apache.hadoop.mapreduce.Reducer.Context;

// TODO: Auto-generated Javadoc
/**
 * The Interface IReader.
 */
public interface IReader {

	/**
	 * Read.
	 *
	 * @param key the key
	 * @return the map
	 * @throws Exception the exception
	 * @throws InterruptedException the interrupted exception
	 */
	Map<String, byte[]> read(Long key) 
	throws Exception, InterruptedException;	
	
}
