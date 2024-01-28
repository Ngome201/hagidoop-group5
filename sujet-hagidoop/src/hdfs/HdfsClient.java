package hdfs;

import formats.Format;
import formats.KV;
import formats.KVFormat;
import formats.TxtFormat;

import java.io.*;

public class HdfsClient {
    public static void HdfsDelete(String fname) {

    }
    public static void HdfsWrite(int fmt, String fname) throws IOException {
        TxtFormat lf ;
        KVFormat kvf ;
        KV kv;

        if (fmt == Format.FMT_TXT) {
//            format = new KVFormat("data/" + "kv-" + fname);
            lf = new TxtFormat(fname);
            lf.open("R");
            kvf = new KVFormat("data/kv-copy.txt"); // we assume that the file comes in a text format
            kvf.open("W");
            while ((kv = lf.read()) != null) {
//        System.out.println(kv);
                kvf.write(kv);
            }
            lf.close();
            kvf.close();
        }
        else {
            TxtFormat kvf1 = new TxtFormat(fname); // we assume that the file comes in a text format
            kvf1.open("R");
            kvf = new KVFormat("data/kv-copy.txt"); // we assume that the file comes in a text format
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname)));
            long index = 0;
            while ((kv = kvf1.read()) != null) {
//        System.out.println(kv);
                bw.write(kv.v, 0, kv.v.length());
                bw.newLine();
                bw.flush();
                index += kv.v.length();
            }
            kvf1.close();
            kvf.close();
        }
    }
    public static void HdfsRead(String fname) {

    }
    public static void main(String[] args) {
        // appel des méthodes précédentes depuis la ligne de commande
    }
}
