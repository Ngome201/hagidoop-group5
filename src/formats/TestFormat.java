package formats;

import config.Project;

public class TestFormat {

	public static void main(String[] args) {
		KV kv;
		try {
			TxtFormat lf = new TxtFormat(Project.PATH+"data/filesample.txt");
			lf.open("R");
			KVFormat kvf = new KVFormat(Project.PATH+"data/filesample-kv.txt");
			kvf.open("W");
			while ((kv = lf.read()) != null) {
//				System.out.println(kv);
				kvf.write(kv);
			}
			lf.close();
			kvf.close();
			
			kvf = new KVFormat(Project.PATH+"data/filesample-kv.txt");
			kvf.open("R");
			lf = new TxtFormat(Project.PATH+"data/filesample2.txt");
			lf.open("W");
			while ((kv = kvf.read()) != null) {
//				System.out.println(kv);
				lf.write(kv);
			}
			kvf.close();
			lf.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
