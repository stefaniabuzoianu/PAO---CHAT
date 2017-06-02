
package serverchat;

public class SRM {
	private StringBuilder txt;

	SRM(){
		txt = new StringBuilder();
	}
	public StringBuilder get() {
		return txt;
	}

	public void set(String txt) {
		this.txt.append(txt);
	}
	
}