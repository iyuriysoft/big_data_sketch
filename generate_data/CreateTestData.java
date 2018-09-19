package a.testdata;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CreateTestData {

    public static void main(String[] args) throws Exception {
        int cnt = 3_000;
        int products = 20;
        int categories = 20;
        if (args.length < 3) {
            System.out.println("generation of a set of random sales\r\n"
                    + "(1) lines count, (2) products count, (3) categories count\r\n"
                    + "example: program.jar 3000 10 10");
            return;
        }
        cnt = Integer.valueOf(args[0]);
        products = Integer.valueOf(args[1]);
        categories = Integer.valueOf(args[2]);
        create("input.txt", cnt, products, categories);
        System.out.print("ok!");
    }

    private static void create(String name, long cnt, int products, int categories)
            throws FileNotFoundException, IOException, ParseException {
        String[] arP = new String[products];
        for (int i = 0; i < arP.length; i++) {
            arP[i] = String.format("product%d", i);
        }
        String[] arC = new String[categories];
        for (int i = 0; i < arC.length; i++) {
            arC[i] = String.format("category%d", i);
        }
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(name, false))) {
            for (int i = 0; i < cnt; i++) {
                out.append(generatingRandomString(arP, arC) + System.lineSeparator());
            }
        }
    }

    private static String generatingRandomString(String[] arP, String[] arC) throws ParseException {
        Random r = new Random();
        SimpleDateFormat dfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");

        // random time + random day
        String sdt = "";
        {
            long istartDay = dfDate.parse("2010-10-15").getTime();
            long iendDay = dfDate.parse("2010-10-22").getTime();
            long randomDay = istartDay + (long) (r.nextFloat() * (iendDay - istartDay + 1));
            String sday = dfDate.format(new Date(randomDay));
            
            istartDay = dfDate.parse("2010-10-15").getTime();
            iendDay = dfDate.parse("2010-10-16").getTime();
            long randomTime = (long) (r.nextGaussian() * (iendDay - istartDay));
            sdt = dfDateTime.format(dfDate.parse(sday).getTime() + randomTime);
        }

        String sprice = String.format("%.1f", r.nextGaussian() * 10 + 500);

        String sname = "";
        {
            List<String> list = Arrays.asList(arP);
            Collections.shuffle(list);
            sname = list.get(0);
        }

        String scat = "";
        {
            List<String> list = Arrays.asList(arC);
            Collections.shuffle(list);
            scat = list.get(0);
        }

        String sip = "";
        {
            int istart = 0;
            int iend = 255;
            StringBuilder sb = new StringBuilder();
            for (int u = 0; u < 4; u++) {
                sb.append(istart + (int) (r.nextFloat() * (iend - istart + 1)));
                if (u < 3)
                    sb.append(".");
            }
            sip = sb.toString();
        }

        String res = String.format("%s,%s,%s,%s,%s", sname, sprice, sdt, scat, sip);

        return res;
    }

}
