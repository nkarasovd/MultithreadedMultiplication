package edu.spbu.matrix;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Double.parseDouble;

public class SparseMatrix implements Matrix {
    public SparseMatrix(String file) {
        readSparse(file);
    }

    public SparseMatrix(int size) {
        this.MatrixS = new HashMap<>();
        this.size = size;
    }

    HashMap<Point, Double> MatrixS = new HashMap<Point, Double>();

    Point P = new Point();

    int size;

    public int getSize(String file) {
        int size = 0;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String str = in.readLine();
            String[] arr = str.split("\\s+");
            size = arr.length;
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    public void readSparse(String file) {
        this.size = getSize(file);
        this.P = new Point();
        Scanner in = null;
        try {
            in = new Scanner(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = 0;
        while (in.hasNextLine()) {

            String[] s = in.nextLine().split(" ");
            size = s.length;
            for (int j = 0; j < s.length; j++) {
                if (parseDouble(s[j]) != 0.0) {
                    P.x = i;
                    P.y = j;
                    this.MatrixS.put(this.P, parseDouble(s[j]));

                }
            }
            i++;
        }
    }

    @Override
    public Matrix mul(Matrix o) {
        if (o instanceof SparseMatrix) try {
            return mulSparseSparse((SparseMatrix) o);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        else return mulSparseDense((DenseMatrix) o);
    }

    public SparseMatrix mulSparseSparse(SparseMatrix other) throws InterruptedException {
        SparseMatrix result = new SparseMatrix(size);
        MulSS t = new MulSS(result.MatrixS, this.MatrixS, other.MatrixS);
        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        result.SparseOut();
        return result;
    }

    public class MulSS implements Runnable {
        HashMap<Point, Double> A;
        HashMap<Point, Double> B;
        HashMap<Point, Double> res;
        int num = 0;

        public MulSS(HashMap<Point, Double> res, HashMap<Point, Double> A, HashMap<Point, Double> B) {
            this.A = A;
            this.B = B;
            this.res = res;
        }


        public void run() {
            Double s = 0.0;
            SparseMatrix res = new SparseMatrix(size);
            for (int i = 0; i <= size; i++) {
                for (int j = 0; j <= size; j++) {
                    for (int k = 0; k <= size; k++) {
                        Point Coord1 = new Point();
                        Coord1.x = j;
                        Coord1.y = i;
                        Point Coord2 = new Point();
                        Coord2.x = i;
                        Coord2.y = k;
                        Point Coord3 = new Point();
                        Coord3.x = j;
                        Coord3.y = k;
                        s = res.MatrixS.get(Coord1) + (this.A.get(Coord2) * B.get(Coord3));
                        res.MatrixS.put(Coord1, s);
                    }
                }
            }
        }

        public int getNumberOfString() {
            synchronized (this) {
                return num++;
            }
        }

        /** public HashMap.Entry<Point, Double> getValue(Iterator<ConcurrentHashMap.Entry<Point, Double>> E) {
         synchronized (E) {
         HashMap.Entry<Point, Double> value1 = null;
         if (E.hasNext()) {
         value1 = E.next();
         }
         return value1;
         }
         }

         public int getCoordinate(Iterator<ConcurrentHashMap.Entry<Point, Double>> E) {
         int i=-2;
         synchronized (E) {
         HashMap.Entry<Point, Double> value1 = null;
         if (E.hasNext()) {

         value1 = E.next();
         Point c = value1.getKey();
         i = c.x;

         } else {
         i = -1;
         }
         }
         return i;
         }

         */

    }

    public MatrixSparseNewClass mulSparseDense(DenseMatrix other) {
        return null;
    }

    public void SparseOut() {
        this.P = new Point();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.P.x = i;
                this.P.y = j;
                if (this.MatrixS.get(this.P) != null)
                    System.out.print(MatrixS.get(this.P) + " ");


                else System.out.print(0.0 + " ");

            }
            System.out.println();
        }

    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}