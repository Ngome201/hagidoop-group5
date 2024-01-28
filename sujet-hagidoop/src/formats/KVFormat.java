package formats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class KVFormat implements Format {
	private static final long serialVersionUID = 1L;

	private String fname;
	private KV kv;
	
	private transient BufferedReader br;
	private transient BufferedWriter bw;
	private transient long index = 0;
	private transient String mode;

		
	public KVFormat(String fname) {
		this.fname = fname;
	}
	
	public void open(String mode) {
		try {
			this.mode = mode;
			this.kv = new KV();
			if (mode.equals("R"))
				br= new BufferedReader(new InputStreamReader(new FileInputStream(fname))); 
			if (mode.equals("W"))
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (mode.equals("R")) br.close();
			if (mode.equals("W")) bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public KV read() {
		try {
			while (true) {
//				String l = br.readLine();
//				if (l == null) return null;
//				index += l.length();
//				StringTokenizer st = new StringTokenizer(l, KV.SEPARATOR);
//				if (st.countTokens() != 2) continue;
//				String k = st.nextToken();
//				String v = st.nextToken();
//				return new KV(k,v);
				
				String l = br.readLine();
				if (l == null) return null;
				index += l.length();
				StringTokenizer st = new StringTokenizer(l, KV.SEPARATOR);
				if (st.countTokens() != 2) continue;
				kv.k = st.nextToken();
				kv.v = st.nextToken();
				return kv;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void write(KV record) {
		try {
			String s = record.k+KV.SEPARATOR+record.v;
			bw.write(s, 0, s.length());
			bw.newLine();
			bw.flush();
			index += s.length();
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
