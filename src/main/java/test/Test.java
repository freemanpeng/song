package test;

import jmidi.*;

/**
 * 测试
 * @author peng
 */
public class Test {

	private static int TUNE = 4;
	
	public static void main(String[] args) throws Exception {
		// 设置音色
		Play play = new Play(2);

		// 当前第几小节
		int section = 1;
		// 上一个音符，初始化设置为9
		int prev = 9;
		// 随机生成的音符范围
		byte range = 15;
		// 随机选择节奏型
		byte[] rhythm = Rhythm.get(Note.rand(Rhythm.size()));
		// 随机选择走向
		byte[] path = Path.get(Note.rand(Path.size()));

		while (true) {
			for (int i = 0, chd = 0; i < rhythm.length; i++) {
				// 旋律区
				{
					int root = path[section - 1];
					// 每次走向的最后一小节
					if(section == path.length) {
						if(i > 0 && Note.melody(prev) != root) {
							// 直到生成和弦根音为止
							prev = must(play, range, prev, root);
						}
					} else {
						prev = must(play, range, prev, root, 3);
					}
				}
				// 和弦区
				{
					if(rhythm[i] == 1) {
						byte[][] chords = Chord.get(path[section - 1]);
						byte[] chord = chords[chd++ % chords.length];
						// 播放和弦
						play.chord(Note.key(chord[0], chord[1]) + TUNE);
					}
				}
				Thread.sleep(240);
			}
			section = (section == path.length) ? 1 : section + 1;
		}
	}

	public static boolean chk(int key, int prev, int path) {
		if(key - prev > 3) {
			return false;
		}
		if(prev - key > 3) {
			return false;
		}
		if(key == prev) {
			return false;
		}

		return Melody.get(path, Note.melody(key));
	}

	/**
	 * 生成音符
	 * @param play
	 * @param range
	 * @param prev
	 * @param root
	 * @param count 重新生成次数，如果不传，一直生成，直到合法为止
	 * @return
	 * @throws Exception
	 */
	private static int must(Play play, byte range, int prev, int root, int... count) {
		int i = 0;
		int max = (count.length > 0) ? count[0] : 0;
		
		do {
			int key = Note.rand(range);
			if(chk(key, prev, root)) {
				// 转换成区域
				int area = key / 6;
				// 转换成音符
				int melody = Note.melody(key);

				play.melody(Note.key(area, melody) + TUNE);
				return key;
			}
		} while (++i < max || count.length == 0);
		
		return prev;
	}
}