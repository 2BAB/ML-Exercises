import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by 2bab on 2017/6/26.
 */
public class KNN4NumberIdentify {

    public static final int K = 3;

    public static void main(String[] args) {
        // src load
        File srcFolder = new File("./data/trainingDigits");
        String srcFileNames[] = srcFolder.list();
        int src[][] = new int[srcFileNames.length][1024];
        int srcNumber[] = new int[srcFileNames.length];
        for (int i = 0; i < srcFileNames.length; i++) {
            src[i] = img2Vector("./data/trainingDigits/" + srcFileNames[i]);
            srcNumber[i] = Integer.parseInt(srcFileNames[i].split("_")[0]);
        }

        // test
        File testFolder = new File("./data/testDigits");
        String testFileNames[] = testFolder.list();
        for (int i = 0; i < testFileNames.length; i++) {
            int realNumber = Integer.parseInt(testFileNames[i].split("_")[0]);
            int testMatrix[] = img2Vector("./data/testDigits/" + testFileNames[i]);
            int classifier = doTest(src, srcNumber, testMatrix);
            System.out.println(classifier + "  " + realNumber);
        }
    }

    private static int doTest(int src[][], int srcNumber[], int[] testMatrix) {
        PriorityQueue<Bean> queue = new PriorityQueue<>(K + 1, comparator);

        for (int i = 0; i < src.length; i++) {
            int innerLength = src[i].length; // actually 1024
            int total = 0;
            for (int j = 0; j < innerLength; j++) {
                total += doublePow(src[i][j] - testMatrix[j]);
            }
            double matchPercent = Math.sqrt(total);
            //System.out.println(matchPercent);
            int number = srcNumber[i];
            Bean bean = new Bean(number, matchPercent);
            queue.add(bean);

            if (queue.size() > K) {
                queue.poll();
            }
        }

        HashMap<Integer, Integer> hashMap = new HashMap<>();

        for (Bean bean : queue) {
            //System.out.println(bean.number);
            hashMap.merge(bean.number, 1, (a, b) -> a + b);
        }

        int similarityNumber = 0;
        int max = 0;
        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                similarityNumber = entry.getKey();
            }
        }

        return similarityNumber;
    }

    private static int doublePow(int a) {
        return a * a;
    }

    private static int[] img2Vector(String filePath) {
        int result[] = new int[1024];
        File file = new File(filePath);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (int i = 0; i < 32; i++) {
                String lineStr = br.readLine();
                for (int j = 0; j < 32; j++) {
                    result[32 * i + j] = lineStr.charAt(j);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private static String readFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                result.append(s).append(System.lineSeparator());
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    static class Bean {

        int number;
        double similarity;

        public Bean(int number, double similarity) {
            this.number = number;
            this.similarity = similarity;
        }
    }

    static Comparator<Bean> comparator = (Comparator<Bean>) (o1, o2) -> {
        if (o1.similarity > o2.similarity) {
            return -1;
        } else if (o1.similarity - o2.similarity < 0.0000000001) {
            return 0;
        }
        return 1;

    };
}
