package a.b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
/**
 * Custom Source for Apache Flume
 * There thee extra parameters:
 *   myfile = path to input file (input.txt by default)
 *   mypause = delay between events (0 by default)
 *   mycheckdate = 
 *
 */
public class CustomSourceTail extends AbstractSource implements Configurable, PollableSource {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomSourceTail.class);
  private static final String INPUT_FILE = "myfile";
  private static final String PAUSE = "mypause";
  private static final String INDEX_DATE = "mydateindex";
  private SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private int pause;
  private String inputFile;
  private int indexDate;
  private BufferedReader reader;
  private static int curLine;

  public long getBackOffSleepIncrement() {
      return 0;
  }

  public long getMaxBackOffSleepInterval() {
      return 0;
  }
  /**
   *
   * @param context
   */
  @Override
  public void configure(Context context) {
    logger.warn("CONFIGURE");
    inputFile = context.getString(INPUT_FILE, "input.txt");
    pause = context.getInteger(PAUSE, 0);
    indexDate = context.getInteger(INDEX_DATE, -1);
    logger.warn(String.format("FILE: %s", inputFile));
    
    ImmutableMap<String, String> parameters = context.getParameters();
    logger.info("parameters: " + parameters); 
  }

  /**
   * @return void
   */
  @Override
  public void start() {
    logger.warn("START custom flume source");
    Path file = Paths.get(inputFile);
    try {
      reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file)));
    } catch (IOException e) {
      logger.error("ERROR", e);
    }
    super.start();
  }

  /**
   * @return void
   */
  @Override
  public void stop() {
    // Disconnect from external client and do any additional cleanup
    // (e.g. releasing resources or nulling-out field values) ..
    logger.warn("STOP custom flume source");
    try {
      reader.close();
    } catch (IOException e) {
      logger.error("ERROR", e);
    }
    super.stop();
  }

  /**
   * @return Status , process source configured from context
   * @throws org.apache.flume.EventDeliveryException
   */
  @Override
  public Status process() throws EventDeliveryException {
    Status status = Status.READY;
    try {
      // This try clause includes whatever Channel/Event operations you want to do
      // Receive new data
      Map<String, String> headers = new HashMap<>();
      headers.put("type", "data");

      Path file = Paths.get(inputFile);
      try (InputStream in = Files.newInputStream(file);
          BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
          Thread.sleep(pause);
          if (i++ >= curLine) {
            String[] ar = line.split(",");
            if (ar.length < 3) {
              break;
            }
            logger.debug(String.format("DATA %d >= %d: %s", i + 1, curLine, line));
            
            if (indexDate >= 0)
              processLine(line.getBytes(), dfDateTime.parse(ar[indexDate]).getTime());
            else
              processLine(line.getBytes(), new Date().getTime());
            curLine++;
          }
        }
      } catch (IOException e) {
        logger.error("ERROR: ", e);
      }
      status = Status.READY;
    } catch (Throwable t) {
      // Log exception, handle individual exceptions as needed
      logger.error("ERROR: ", t);
      status = Status.BACKOFF;
    }
    return status;
  }

  /**
   * @void process file lines.
   * @param line byte[]
   */
  public void processLine(byte[] line, long time) {
    byte[] message = line;
    Event event = new SimpleEvent();
    Map<String, String> headers = new HashMap<>();
    headers.put("timestamp", String.valueOf(time));
    event.setBody(message);
    event.setHeaders(headers);
    try {
      getChannelProcessor().processEvent(event);
    } catch (ChannelException e) {
      logger.error("ERROR: ", e);
    }

  }
}