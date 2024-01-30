package formats;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;


public class TxtFormat implements Format {
	private static final long serialVersionUID = 1L;

	private String fname;
	private KV kv;

	private transient LineNumberReader lnr;
	private transient BufferedWriter bw;
	private transient long index = 0;
	private transient String mode;

	public TxtFormat(String fname) {
		this.fname = fname;
	}
	
	public void open(String mode) {
		try {
			this.mode = mode;
			this.kv = new KV();
			if (mode.equals("R"))
				lnr= new LineNumberReader(new InputStreamReader(new FileInputStream(fname)));
			if (mode.equals("W"))
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (mode.equals("R")) lnr.close();
			if (mode.equals("W")) {bw.flush();bw.close();}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	
	public KV read() {
		try {
			kv.k = Integer.toString(lnr.getLineNumber());
			kv.v = lnr.readLine();
			if (kv.v == null) return null;
			index += kv.v.length();
			return kv;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void write(KV record) {
		try {
			bw.write(record.v, 0, record.v.length());
			bw.newLine();
			index += record.v.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public long getIndex() {
		return index;
	}

	public String getFname() {return fname;}
	
	public void setFname(String fname) {this.fname = fname;}
}
