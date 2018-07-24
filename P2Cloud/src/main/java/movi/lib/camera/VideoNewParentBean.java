package movi.lib.camera;

import java.io.Serializable;
import java.util.ArrayList;

public class VideoNewParentBean implements Serializable {
	private static final long serialVersionUID = 1968920508245094987L;

	private ArrayList<VideoNewBean> list;

	public ArrayList<VideoNewBean> getList() {
		return list;
	}

	public void setList(ArrayList<VideoNewBean> list) {
		this.list = list;
	}

}
