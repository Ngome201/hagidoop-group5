package hdfs;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationFileReader {
    Map<String,String> parameter;
    private transient LineNumberReader lnr;

    private transient BufferedWriter bw;

    public ConfigurationFileReader(String fname) throws FileNotFoundException {
        lnr= new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname)));

    }
    public Map<String,String> getValues() throws IOException {
        parameter = new HashMap<>();
        String [] res;
        String line ;
        line = lnr.readLine();
        while (line !=null){
            res = line.split("\t");
            parameter.put(res[0],res[1]);
            line = lnr.readLine();
        }
        return parameter;
    }

}