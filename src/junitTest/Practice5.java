package junitTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.IntStream;
import java.util.stream.IntStream.Builder;

public class Practice5 {
	private final static int SIZE = 5;

	public static void main(String[] args) throws  NumberFormatException, IOException {
		Builder builder = IntStream.builder();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("科目１～５の点数を入力してください");

		for (int i = 0; i < SIZE; i++) {
			builder.add(Integer.parseInt(br.readLine()));
		}

		int sum = builder.build().sum();
		double average = (double) sum / SIZE;

		System.out.println("５科目の合計点は" + sum + "です。");
		System.out.println("５科目の平均点は" + average + "です。");
	}
}
