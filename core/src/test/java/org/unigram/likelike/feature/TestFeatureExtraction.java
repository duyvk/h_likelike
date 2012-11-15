package org.unigram.likelike.feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.PoolExhaustedException;
import me.prettyprint.cassandra.testutils.EmbeddedServerHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.OutputLogFilter;
import org.apache.thrift.transport.TTransportException;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.commons.collections.MultiHashMap;
import org.unigram.likelike.common.LikelikeConstants;
import org.unigram.likelike.util.accessor.CassandraWriter;
import org.unigram.likelike.util.accessor.IWriter;
import org.unigram.likelike.util.accessor.cassandra.AccessRecommendedFeatures;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class TestFeatureExtraction.
 */
public class TestFeatureExtraction extends TestCase {

    /**
     * Instantiates a new test feature extraction.
     *
     * @param name the name
     */
    public TestFeatureExtraction(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    /**
     * Test run.
     */
    public void testRun() {
    	// test with hadoop dfs
    	this.dfsRunWithCheck();
        
    	// test with cassandra
        try {
            embedded = new EmbeddedServerHelper();
            embedded.setup();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	
    	this.cassandraRunWithCheck();
        embedded.teardown();
    }
    
    /**
     * Cassandra run with check.
     */
    public void cassandraRunWithCheck() {
        // settings 
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "file:///");
        conf.set("mapred.job.tracker", "local");

        // run
        this.run("cassandra"
        		, conf);
        
        /* check result */
        assertTrue(this.checkCassandraResults(conf));
    }
    
	/**
	 * Check cassandra results.
	 *
	 * @param conf the conf
	 * @return true, if successful
	 */
	private boolean checkCassandraResults(Configuration conf) {

		AccessRecommendedFeatures accessor = new AccessRecommendedFeatures(conf);
        Long keys[] = {0L, 2L};
        MultiHashMap resultMap = new MultiHashMap();
        for (int i =0; i<keys.length; i++) {
            Long key = keys[i];
            try {
            	Map<String, byte[]> cols  = accessor.read(key);
                //System.out.println("key:" + key.toString() 
                //		+ "\tcols.size() = " + cols.size());
                
                Iterator itrHoge = cols.keySet().iterator();
                while(itrHoge.hasNext()){
                    String v = (String) itrHoge.next();
                    //System.out.println("\tvalue: " + v);
                    resultMap.put(key, // target  
                            Long.parseLong(v));                    
                   }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } 
        }
        
        return this.checkResults(resultMap);
	}

	/**
	 * Dfs run with check.
	 */
	public void dfsRunWithCheck() {
        // settings 
        Configuration conf = new Configuration();
        conf.set("fs.default.name", "file:///");
        conf.set("mapred.job.tracker", "local");

        // run
        this.run("dfs", conf);       
    
        /* extract result*/
        MultiHashMap resultMap = null;
        try {
            resultMap = this.getResultMap(conf, 
                    new Path(this.outputPath));
        } catch (IOException e){ 
                e.printStackTrace();
                return;
        }
        /* check result */
        assertTrue(this.checkResults(resultMap));
        
    }
    
    /**
     * Run.
     *
     * @param writer the writer
     * @param conf the conf
     * @return true, if successful
     */
    public boolean run(String writer, Configuration conf) {

         /* run feature extraction */
         String[] args = {
                 "-input",  this.recommendPath, 
                 "-feature", this.featurePath,
                 "-output", this.outputPath,
                 "-storage", writer
         };

         conf.set(LikelikeConstants.LIKELIKE_OUTPUT_WRITER, writer);         
         FeatureExtraction job = new FeatureExtraction();
     
         try {
             job.run(args, conf);
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }    	
         return true;
    }
    
    /**
     * Check results.
     *
     * @param resultMap the result map
     * @return true, if successful
     */
    public boolean checkResults(MultiHashMap resultMap) {
        Set keys = resultMap.keySet();
        assertTrue(keys.size() == 2);

        Collection coll = (Collection) resultMap.get(new Long(0));
        if (coll == null || coll.size() != 3) { return false; }
        assertTrue(coll.contains(new Long(5)));
        assertTrue(coll.contains(new Long(6)));
        assertTrue(coll.contains(new Long(7)));
        assertFalse(coll.contains(new Long(1)));
        assertFalse(coll.contains(new Long(2)));
        
        coll = (Collection) resultMap.get(new Long(2));
        if (coll == null || coll.size() != 2) { return false; }          
        assertTrue(coll.contains(new Long(10)));
        assertTrue(coll.contains(new Long(11)));
        assertFalse(coll.contains(new Long(1)));
        assertFalse(coll.contains(new Long(2)));        
        
        coll = (Collection) resultMap.get(new Long(5));
        if (coll != null) { return false; }

        return true;
    }
    
    /**
     * Gets the result map.
     *
     * @param conf the conf
     * @param outputPath the output path
     * @return the result map
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected MultiHashMap getResultMap(
            Configuration conf, 
            Path outputPath) throws IOException {
        FileSystem fs = FileSystem.getLocal(conf);
        Path[] outputFiles = FileUtil.stat2Paths(
            fs.listStatus(outputPath, new OutputLogFilter()));

        if (outputFiles != null) {
            TestCase.assertEquals(outputFiles.length, 1);
        } else {
            TestCase.fail();
        }

        BufferedReader reader = this.asBufferedReader(
                fs.open(outputFiles[0]));        
        
        String line;
        MultiHashMap resultMap = new MultiHashMap();
        while ((line = reader.readLine()) != null) {
            String[] lineArray = line.split("\t");
                resultMap.put(Long.parseLong(lineArray[0]),
                        Long.parseLong(lineArray[1]));
        }
        return resultMap;
    }    
    
    /**
     * As buffered reader.
     *
     * @param in the in
     * @return the buffered reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private BufferedReader asBufferedReader(final InputStream in)
    throws IOException {
        return new BufferedReader(new InputStreamReader(in));
    }
    
    /** The recommend path. */
    private String recommendPath  = "testSmallRecommend.txt";

    /** The feature path. */
    private String featurePath = "testSmallInput.txt";
    
    /** The output path. */
    private String outputPath = "outputFeatureExtraction";    

    /** The embedded. */
    private static EmbeddedServerHelper embedded;    
    
}
