package a.b;

import java.net.UnknownHostException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import a.udf.GetEndIP;
import a.udf.GetIP;
import a.udf.GetStartIP;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SimpleUDFTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SimpleUDFTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SimpleUDFTest.class);
    }

    /**
     * @throws UnknownHostException
     */
    public void testUDF() throws UnknownHostException {
        GetIP o = new GetIP();
        LongWritable val = o.evaluate(new Text("10.10.10.10"));
        System.out.println(val.get());
        assertTrue(val.get() == 168430090);

        val = o.evaluate(new Text(" 0.166.68.89"));
        System.out.println(val.get());
        assertTrue(val.get() == 10896473);

        GetStartIP oStartIP = new GetStartIP();
        val = oStartIP.evaluate(new Text("1.1.1.1/24"));
        System.out.println(val.get());
        assertTrue(val.get() == 16843008);

        GetEndIP oEndIP = new GetEndIP();
        val = oEndIP.evaluate(new Text("1.1.1.1/24"));
        System.out.println(val.get());
        assertTrue(val.get() == 16843263);
    }

}
