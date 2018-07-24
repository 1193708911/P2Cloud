package movi.ui.bean;

public class VideoMsgBean {
	//记录开始位置
	private long  start;
	//记录结束位置
	private long end;
	//记录时间区间
	private long duration;
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
}
