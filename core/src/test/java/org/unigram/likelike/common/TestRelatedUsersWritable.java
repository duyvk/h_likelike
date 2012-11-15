package org.unigram.likelike.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRelatedUsersWritable.
 */
public class TestRelatedUsersWritable extends TestCase {

    /**
     * Instantiates a new test related users writable.
     *
     * @param name the name
     */
    public TestRelatedUsersWritable(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test write and read.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void testWriteAndRead() throws IOException {
        
        List<LongWritable> eid = 
            new ArrayList<LongWritable>();
        eid.add(new LongWritable(327));
        eid.add(new LongWritable(227));
        eid.add(new LongWritable(4389));
        eid.add(new LongWritable(94));
        
        RelatedUsersWritable relatedUsers = new RelatedUsersWritable(eid);
        List<LongWritable> sourceElements = relatedUsers.getRelatedUsers();
        
        // write and read
        DataOutputBuffer out = new DataOutputBuffer();
        DataInputBuffer in = new DataInputBuffer();
        relatedUsers.write(out);

        RelatedUsersWritable destUsers = new RelatedUsersWritable();
        in.reset(out.getData(), out.getLength());
        destUsers.readFields(in);
        List<LongWritable> destElements = destUsers.getRelatedUsers();
        
        // check loaded
        assertTrue(destElements.size() == sourceElements.size());
        for (int i = 0; i < destElements.size(); i++) {
          assertEquals(destElements.get(i), sourceElements.get(i));
        }
        
    }

}
