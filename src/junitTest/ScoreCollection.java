package junitTest;

import java.util.ArrayList;
import java.util.List;

public class ScoreCollection {
	private List<Scoreable> scores = new ArrayList<>();

	public void add(Scoreable scoreable) {
		// ガード節(gurd clause)を追加して入力値の許容範囲を示す
		if (scoreable == null) throw new IllegalArgumentException();
		scores.add(scoreable);
	}

	public int arithmeticMean() {
		// コレクションが空の場合のガード節
		if (scores.size() == 0) return 0;

		// int total = scores.stream().mapToInt(Scoreable::getScore).sum();

		// int値を超える結果に対応
		long total = scores.stream().mapToLong(Scoreable::getScore).sum();

		return (int)(total / scores.size());
	}
}
