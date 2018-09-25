package a.udf;

import java.net.UnknownHostException;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

@Description(name = "getEndIP", value = "_FUNC_(str) - get IP as long from range", extended = "Example:\n"
    + "  > SELECT getEndIP(range) FROM a;\n" + "  16843263")
public class GetEndIP extends UDF {

  public LongWritable evaluate(Text iprange) throws UnknownHostException {
    CIDRUtils cidrUtils = new CIDRUtils(iprange.toString());
    LongWritable to_value = new LongWritable(0);
    if (iprange != null) {
       to_value.set(cidrUtils.getBroadcastAddress2());
    }
    return to_value;
  }

}
