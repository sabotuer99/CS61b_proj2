package gitlet;

import java.util.ArrayList;
import java.util.List;

public class Staging {
	private List<String> filesToAdd;
	private List<String> filesToRm;
	
	public Staging(){
		filesToAdd = new ArrayList<String>();
		filesToRm = new ArrayList<String>();
	}
	
	public List<String> getFilesToAdd() {
		return filesToAdd;
	}
	public void setFilesToAdd(List<String> filesToAdd) {
		this.filesToAdd = filesToAdd;
	}
	
	public List<String> getFilesToRm() {
		return filesToRm;
	}
	public void setFilesToRm(List<String> filesToRm) {
		this.filesToRm = filesToRm;
	}
}
