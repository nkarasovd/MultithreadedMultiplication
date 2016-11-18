package edu.spbu.matrix;


import java.io.*;
import java.util.Scanner;


public class DenseMatrix implements Matrix {
    public int size;              // Размер матрицы
    public double[][] matrixD;    // Плотная матрица


    public DenseMatrix(int size) {
        this.matrixD = new double[size][size];
        this.size = size;
    }

    public DenseMatrix(String fileName) {
        readDense(fileName);
    }

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

    public void readDense(String file) {
        this.size = getSize(file);
        Scanner in = null;
        try {
            in = new Scanner(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.matrixD = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixD[i][j] = in.nextDouble();
            }
        }
        in.close();
    }

    @Override
    public Matrix mul(Matrix o) {
        if (o instanceof DenseMatrix) try {
            return mulDenseDense((DenseMatrix) o);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        else return mulDenseSparse((MatrixSparseNewClass) o);
    }

    public DenseMatrix mulDenseDense(DenseMatrix o) throws InterruptedException {
        o = transpositionD(o);
        DenseMatrix res = new DenseMatrix(size);
        MulDD t = new MulDD(res, this, o);
        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        //res.denseOut();
        return res;

    }

    public class MulDD implements Runnable {
        DenseMatrix A = new DenseMatrix(size);
        DenseMatrix B = new DenseMatrix(size);
        DenseMatrix res = new DenseMatrix(size);
        int num = 0;

        public MulDD(DenseMatrix res, DenseMatrix A, DenseMatrix B) {
            this.A = A;
            this.B = B;
            this.res = res;
        }

        public void run() {
            for (int i = getNumberOfString(); i < size; i = getNumberOfString()) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        res.matrixD[i][j] = res.matrixD[i][j] + A.matrixD[i][k] * B.matrixD[j][k];
                    }
                }
            }
        }

        public int getNumberOfString() {
            synchronized (this) {
                return num++;
            }
        }
    }

    public MatrixSparseNewClass mulDenseSparse(MatrixSparseNewClass other) {
        return null;
    }

    public DenseMatrix transpositionD(DenseMatrix other) {

        DenseMatrix res = new DenseMatrix(size);

        for (int i = 0; i < other.size; i++) {
            for (int j = 0; j < other.size; j++) {
                res.matrixD[j][i] = other.matrixD[i][j];
            }
        }
        return res;
    }

    public void denseOut() {
        for (int i = 0; i < matrixD.length; i++) {
            for (int j = 0; j < matrixD[0].length; j++) {
                System.out.print(matrixD[i][j] + " ");
            }
            System.out.println();
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

}