package formats;

import java.io.Serializable;

public interface Format extends FormatReader, FormatWriter, Serializable {
	public static final int FMT_TXT = 0;
	public static final int FMT_KV = 1;
	//public KV read();
	//public void write(KV record);
	public void open(String mode);
	public void close();
	public long getIndex();
	public String getFname();
	public void setFname(String fname);

}
