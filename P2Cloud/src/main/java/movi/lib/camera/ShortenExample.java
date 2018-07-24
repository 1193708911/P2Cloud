package movi.lib.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

public class ShortenExample {

	public static void main(String args[]) {
		try {
			// startTrim(new
			// File("D:\\Mp4\\Wonders_of_Nature.mp4"),new
			// File("D:\\Mp4\\new.mp4"),17000,35000);
			// startTrim(new File("D:\\Mp4\\test.mp4"),new
			// File("D:\\Mp4\\test1.mp4"),3000,7000);
			// appendVideo(new
			// String[]{"D:\\Mp4\\begin.mp4","D:\\Mp4\\end.mp4","D:\\Mp4\\append1.mp4"});
			appendVideo(new String[] { "D:\\Mp4\\test.mp4", "D:\\Mp4\\test1.mp4" });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void startTrim(File src, File dst, int startMs, int endMs) throws IOException {

//		ReadableByteChannel in = Channels.newChannel((new FileInputStream(src)));
		
		Movie movie = MovieCreator.build("D:\\Mp4\\test.mp4");
		// RandomAccessFile randomAccessFile = new RandomAccessFile(src,
		// "r");
		//randomAccessFile.getChannel()
		
		// DataSource ds = DataSource.
		// Movie movie =
		// MovieCreator.build("D:\\Mp4\\test.mp4");

		// 删除所有跟踪我们将创建新的跟踪从旧
		List<Track> tracks = movie.getTracks();
		movie.setTracks(new LinkedList<Track>());
		for (Track track : tracks) {
			printTime(track);
		}

		double startTime = startMs / 1000;
		double endTime = endMs / 1000;

		boolean timeCorrected = false;

		// 我们试图找到一个样品同步跟踪。因为我们只能开始解码在这样一个样品我们应该确保新的片段的开始就是这样的一个框架
		for (Track track : tracks) {
			if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
				if (timeCorrected) {
					// 这个异常可能是假阳性,以防我们有多个与同步跟踪样品在相同的位置。比如一部电影包含多个品质相同的视频(微软平滑流媒体文件)

					throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
				}
				startTime = correctTimeToSyncSample(track, startTime, false);// true
				endTime = correctTimeToSyncSample(track, endTime, true);// false
				timeCorrected = true;
			}
		}
		for (Track track : tracks) {
			long currentSample = 0;
			double currentTime = 0;
			long startSample = -1;
			long endSample = -1;
			for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
				TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
				for (int j = 0; j < entry.getCount(); j++) {
					// entry.getDelta()的数量当前样本覆盖。
					if (currentTime <= startTime) {
						// 目前的样品仍然在新的开始时间之前
						startSample = currentSample;
					}
					if (currentTime <= endTime) {
						// 当前样本后,新的开始时间和仍在新endtime前
						endSample = currentSample;
					} else {
						// 目前样品结束后出现的视频
						break;
					}
					currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
					currentSample++;
				}
			}

			movie.addTrack(new CroppedTrack(track, startSample, endSample));
			break;
		}
		// movie.addTrack(new CroppedTrack(track,
		// startSample,endSample));

		// IsoFile out = (IsoFile) new DefaultMp4Builder().build(movie);
		Container container = new DefaultMp4Builder().build(movie);

		if (!dst.exists()) {
			dst.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(dst);
		FileChannel fc = fos.getChannel();
		// out.getBox(fc); // 这一个增强的内存。
		container.writeContainer(fc);

		fc.close();
		fos.close();
		// randomAccessFile.close();
	}

	public void startAppend() {

	}

	public static void appendVideo(String[] videos) throws IOException {
		Movie[] inMovies = new Movie[videos.length];
		int index = 0;
		for (String video : videos) {
			inMovies[index] = MovieCreator.build(video);
			index++;
		}
		List<Track> videoTracks = new LinkedList<Track>();
		List<Track> audioTracks = new LinkedList<Track>();
		for (Movie m : inMovies) {
			for (Track t : m.getTracks()) {
				if (t.getHandler().equals("soun")) {
					audioTracks.add(t);
				}
				if (t.getHandler().equals("vide")) {
					videoTracks.add(t);
				}
			}
		}

		Movie result = new Movie();

		if (audioTracks.size() > 0) {
			result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
		}
		if (videoTracks.size() > 0) {
			result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
		}
		Container out = new DefaultMp4Builder().build(result);
		FileChannel fc = new RandomAccessFile(String.format("D:\\Mp4\\append2.mp4"), "rw").getChannel();
		out.writeContainer(fc);
		fc.close();
	}

	protected static long getDuration(Track track) {
		long duration = 0;
		for (TimeToSampleBox.Entry entry : track.getDecodingTimeEntries()) {
			duration += entry.getCount() * entry.getDelta();
		}
		return duration;
	}

	private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
		double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
		long currentSample = 0;
		double currentTime = 0;
		for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
			TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
			for (int j = 0; j < entry.getCount(); j++) {
				if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
					// 样品总是从1开始,但我们从零因此+ 1开始
					timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
				}
				currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
				currentSample++;
			}
		}
		double previous = 0;
		for (double timeOfSyncSample : timeOfSyncSamples) {
			if (timeOfSyncSample > cutHere) {
				if (next) {
					return timeOfSyncSample;
				} else {
					return previous;
				}
			}
			previous = timeOfSyncSample;
		}
		return timeOfSyncSamples[timeOfSyncSamples.length - 1];
	}

	private static void printTime(Track track) {
		double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
		long currentSample = 0;
		double currentTime = 0;
		for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
			TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
			for (int j = 0; j < entry.getCount(); j++) {
				if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
					// 样品总是从1开始,但我们从零因此+ 1开始
					timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
					System.out.println("currentTime-->" + currentTime);
				}
				currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
				currentSample++;
			}
		}

		// System.out.println("size-->"+currentSample);
		/*
		 * for(int i=0;i<timeOfSyncSamples.length;i++){
		 * System.out.println("data-->"+timeOfSyncSamples[i]); }
		 */

		/*
		 * double previous = 0; for (double timeOfSyncSample :
		 * timeOfSyncSamples) { if (timeOfSyncSample > cutHere) { if
		 * (next) { return timeOfSyncSample; } else { return previous; }
		 * } previous = timeOfSyncSample; } return
		 * timeOfSyncSamples[timeOfSyncSamples.length - 1];
		 */
	}

}
