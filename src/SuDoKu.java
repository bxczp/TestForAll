
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SuDoKu {

    // final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd
    // HH:mm:ss SS");

    public static void main(String[] args) {
        File f = new File("C:\\Users\\chengzp2018.jy\\Desktop\\Sudoku\\test");
        final List<File> filePathsList = new ArrayList<File>();
        File[] filePaths = f.listFiles();
        for (File s : filePaths) {
            filePathsList.add(s);
        }

        CountDownLatch latch = new CountDownLatch(filePathsList.size());
        ExecutorService pool = Executors.newFixedThreadPool(1024);

        BlockingQueue<Future<Map<String, FileInputStream>>> queue = new ArrayBlockingQueue<Future<Map<String, FileInputStream>>>(
                100000);

        for (int i = 0; i < filePathsList.size(); i++) {
            File temp = filePathsList.get(i);
            Future<Map<String, FileInputStream>> future = pool.submit(new MyCallableProducer(latch, temp));
            queue.add(future);

            pool.execute(new MyCallableConsumer(queue));
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdownNow();
    }

    // 文件读线程
    static class MyCallableProducer implements Callable<Map<String, FileInputStream>> {
        private CountDownLatch latch;
        private File file;
        private FileInputStream fis = null;
        private Map<String, FileInputStream> fileMap = new HashMap<String, FileInputStream>();

        public MyCallableProducer(CountDownLatch latch, File file) {
            this.latch = latch;
            this.file = file;
        }

        @Override
        public Map<String, FileInputStream> call() throws Exception {
            fis = new FileInputStream(file);
            fileMap.put(file.getName(), fis);
            latch.countDown();
            return fileMap;
        }
    }

    // 文件写线程
    static class MyCallableConsumer implements Runnable {
        private String fileName = "";
        private BlockingQueue<Future<Map<String, FileInputStream>>> queue;
        private FileInputStream fis = null;
        private File dirFile = null;

        private BufferedReader br = null;
        private InputStreamReader isr = null;
        private FileWriter fw = null;
        private BufferedWriter bw = null;
        private String result = "";
        private boolean done = false;

        public MyCallableConsumer(BlockingQueue<Future<Map<String, FileInputStream>>> queue2) {
            this.queue = queue2;
        }

        @Override
        public void run() {
            try {
                Future<Map<String, FileInputStream>> future = queue.take();
                Map<String, FileInputStream> map = future.get();

                Set<String> set = map.keySet();
                for (Iterator<String> iter = set.iterator(); iter.hasNext();) {

                    fileName = iter.next().toString();
                    fis = map.get(fileName);

                    try {
                        isr = new InputStreamReader(fis, "utf-8");
                        br = new BufferedReader(isr);

                        dirFile = new File("C:\\Users\\chengzp2018.jy\\Desktop\\Sudoku\\test2\\" + fileName);

                        fw = new FileWriter(dirFile);
                        bw = new BufferedWriter(fw);

                        String data = "";
                        while ((data = br.readLine()) != null) {

                            if (data.endsWith(",")) {
                                data = data + "#";
                            }
                            String[] strs = data.split(",");

                            for (int i = 0; i < strs.length; i++) {
                                if (strs[i].equals("") || strs[i] == null) {
                                    strs[i] = "0";
                                }
                            }

                            int[][] maps = new int[9][9];
                            for (int i = 0; i < 9; i++) {
                                for (int j = 0; j < 9; j++) {
                                    maps[i][j] = Integer.parseInt(strs[i * 9 + j]);
                                }
                            }

                            Map<CoordinateEnum, List<Coordinate>> map2 = new EnumMap<CoordinateEnum, List<Coordinate>>(
                                    CoordinateEnum.class);
                            SudoKuUtils.initCoordinateEnum(map2);
                            StackList stackList = new StackList();
                            Coordinate coordinate = SudoKuUtils.findFirst(maps, map2);
                            StringBuffer sb = new StringBuffer();
                            if (coordinate != null) {
                                SudoKuUtils.findBestValue(coordinate, maps, map2, stackList);
                                for (int i = 0; i < 9; i++) {
                                    for (int j = 0; j < 9; j++) {
                                        if (j % 3 == 0) {
                                            // sb.append(",");
                                        }
                                        sb.append(String.valueOf(maps[i][j]) + ",");
                                    }
                                    // sb.append("\r\n");
                                }
                            }

                            bw.write(sb.toString().substring(0, sb.toString().length() - 1) + "\r");
                        }
                    } catch (Exception e) {
                    } finally {
                        try {
                            bw.close();
                            br.close();
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        enum CoordinateEnum {
            _0_0(0, 0), _0_1(0, 1), _0_2(0, 2), _1_0(1, 0), _1_1(1, 1), _1_2(1, 2), _2_0(2, 0), _2_1(2, 1), _2_2(2, 2);
            private int x;
            private int y;

            CoordinateEnum(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }
        }

        static class StackList<E> {
            private LinkedList<E> ll = new LinkedList<E>();

            // 入栈
            public void push(E e) {
                ll.addFirst(e);
            }

            // 查看栈顶元素但不移除
            public E peek() {
                return ll.getFirst();
            }

            // 出栈
            public E pop() {
                return ll.removeFirst();
            }

            // 判空
            public boolean empty() {
                return ll.isEmpty();
            }

            // 打印栈元素
            public String toString() {
                return ll.toString();
            }
        }

        static class SudoKuUtils {
            public static int[][] getWrited(int a[][]) {
                int[][] b = new int[9][9];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (a[i][j] == 0) {
                            b[i][j] = 1; // 可以写入值，0代表可以写入
                        } else {
                            b[i][j] = 0;
                        }
                    }
                }
                return b;
            }

            // 初始化九个 小九宫格
            public static void initCoordinateEnum(Map<CoordinateEnum, List<Coordinate>> map) {
                for (CoordinateEnum e : CoordinateEnum.values()) {
                    map.put(e, getCoordinate(e));
                }
            }

            // 获取小九宫格的九个坐标
            private static List<Coordinate> getCoordinate(CoordinateEnum coordinateEnum) {
                List<Coordinate> list = new ArrayList<Coordinate>();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        Coordinate coordinate = new Coordinate();
                        coordinate.setX(3 * coordinateEnum.getX() + i);
                        coordinate.setY(3 * coordinateEnum.getY() + j);
                        list.add(coordinate);
                    }
                }
                return list;
            }

            public static List<Coordinate> getCoordinateEnumValue(int x, int y,
                    Map<CoordinateEnum, List<Coordinate>> map) {
                List<Coordinate> list = new ArrayList<Coordinate>();
                x = x / 3;
                y = y / 3;
                for (CoordinateEnum e : CoordinateEnum.values()) {
                    if (e.getX() == x && y == e.getY()) {
                        list = map.get(e);
                        break;
                    }
                }
                if (list == null) {
                    list = new ArrayList<Coordinate>();
                }
                return list;
            }

            // 横纵坐标校验规则
            public static boolean XYRule(int x, int y, int[][] a, int result) {
                if (x > 8 || x < 0) {
                    return false;
                }
                if (y < 0 || y > 8) {
                    return false;
                }
                for (int i = 0; i < 9; i++) { // 横坐标校验
                    if (a[x][i] == result) {
                        return false;
                    }
                }
                for (int i = 0; i < 9; i++) { // 纵坐标校验
                    if (a[i][y] == result) {
                        return false;
                    }
                }
                return true;
            }

            // 小九宫格校验
            public static boolean litterBoxRule(int x, int y, int[][] a, int result,
                    Map<CoordinateEnum, List<Coordinate>> map) {
                List<Coordinate> enumValues = getCoordinateEnumValue(x, y, map);
                for (Coordinate coordinate : enumValues) {
                    if (a[coordinate.getX()][coordinate.getY()] == result) {
                        return false;
                    }
                }
                return true;
            }

            // 判断是否完成
            public static boolean finish(int a[][]) {
                int sum = 0;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        sum += a[i][j];
                    }
                }
                if (sum == 405) {
                    return true;
                }
                return false;
            }

            // 得到一个有效的坐标点， 1.若果小九宫格已经有八个了，返回第九个坐标点，2找到横纵轴立面空白最少的返回
            public static Coordinate findFirst(int[][] a, Map<CoordinateEnum, List<Coordinate>> map) {
                for (Map.Entry entry : map.entrySet()) {
                    int count = 0;
                    Coordinate coordinate1 = new Coordinate();
                    for (Coordinate coordinate : (List<Coordinate>) entry.getValue()) {
                        if (a[coordinate.getX()][coordinate.getY()] != 0) {
                            count++;
                        } else {
                            coordinate1 = coordinate;
                        }
                    }
                    if (count == 8) {
                        return coordinate1;
                    }
                }
                int x[] = new int[9];
                int y[] = new int[9];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (a[i][j] != 0) {
                            x[i]++;
                        }
                        if (a[j][i] != 0) {
                            y[i]++;
                        }
                    }
                }
                Coordinate maxA = findMax(x);
                Coordinate maxB = findMax(y);
                if (maxA.getX() < maxB.getX()) {
                    int indexY = maxB.getY();
                    for (int i = 0; i < 9; i++) {
                        if (a[i][indexY] == 0) {
                            Coordinate coordinate1 = new Coordinate(i, indexY);
                            return coordinate1;
                        }
                    }
                } else {
                    int indexX = maxA.getY();
                    for (int i = 0; i < 9; i++) {
                        if (a[indexX][i] == 0) {
                            Coordinate coordinate1 = new Coordinate(indexX, i);
                            return coordinate1;
                        }
                    }
                }
                return null; // 执行到这说明数值已经填充完了
            }

            static Coordinate findIndex(int[] a) {
                Coordinate coordinate = new Coordinate();
                for (int i = 0; i < 9; i++) {
                    if (a[i] == 9) {
                        continue;
                    }
                    coordinate.setY(i);
                    coordinate.setX(a[i]);
                    return coordinate;
                }
                if (coordinate.getX() == 0 && coordinate.getY() == 0) {
                    coordinate.setX(0);
                    coordinate.setY(0);
                }
                return coordinate;
            }

            private static Coordinate findMax(int[] a) {
                Coordinate coordinate = new Coordinate(); // x 存最大值 y存最小值
                coordinate = findIndex(a);
                int max = coordinate.getX();
                for (int i = 0; i < a.length; i++) {
                    if (max < a[i] && a[i] != 9) {
                        max = a[i];
                        coordinate.setX(max);
                        coordinate.setY(i);
                    }
                }
                return coordinate;
            }

            // 递归调用，找到相应的值进行填充，用栈来保存每一步，就和迷宫问题（数据结构一种案例）一样
            public static void findBestValue(Coordinate coordinate, int[][] a,
                    Map<CoordinateEnum, List<Coordinate>> map, StackList stackList) {
                List<Integer> list = findRightValue(coordinate, a, map);
                for (Integer s : list) {
                    if (!stackList.empty()) {
                        if (finish(a)) {
                            return;
                        }
                        Coordinate peek = null;
                        peek = (Coordinate) stackList.peek();
                        if (peek.getX() != coordinate.getX() || peek.getY() != coordinate.getY()) { // 没有就填充进去
                            stackList.push(coordinate);
                        }
                    }
                    //
                    if (stackList.empty()) {
                        stackList.push(coordinate);
                    }
                    a[coordinate.getX()][coordinate.getY()] = s;
                    Coordinate coordinate1 = SudoKuUtils.findFirst(a, map);
                    if (coordinate1 == null) {
                        return;
                    }
                    List<Integer> list2 = findRightValue(coordinate1, a, map);
                    if (list2.size() == 0) {
                        if (!stackList.empty()) { // 值回退
                            Coordinate coordinate2 = (Coordinate) stackList.peek();
                            a[coordinate2.getX()][coordinate2.getY()] = 0;
                            coordinate = (Coordinate) stackList.pop();
                        }
                    } else {
                        findBestValue(coordinate1, a, map, stackList);
                    }
                }
                if (!stackList.empty()) {
                    Coordinate coordinate2 = (Coordinate) stackList.peek();
                    if (coordinate.getX() == coordinate2.getX() && coordinate.getY() == coordinate2.getY()) {
                        stackList.pop();
                        a[coordinate2.getX()][coordinate2.getY()] = 0;
                    }
                }
            }

            // 找到一个坐标点几种可能的值
            static List<Integer> findRightValue(Coordinate coordinate, int a[][],
                    Map<CoordinateEnum, List<Coordinate>> map) {
                List<Integer> list = new ArrayList<Integer>();
                for (int val = 1; val <= 9; val++) {
                    if (XYRule(coordinate.getX(), coordinate.getY(), a, val)
                            && litterBoxRule(coordinate.getX(), coordinate.getY(), a, val, map)) {
                        list.add(val);
                    }
                }
                return list;
            }

        }

        static class Coordinate {
            private int x;
            private int y;

            public Coordinate() {
            }

            public Coordinate(int x, int y) {
                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }
        }

    }

}
