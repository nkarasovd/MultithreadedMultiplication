package edu.spbu.matrix;


public class Main {
    public static void main(String[] args) {
        SparseMatrix m1 = new SparseMatrix("MatrixExample/dense1");
        SparseMatrix m2 = new SparseMatrix("MatrixExample/dense2");
        m1.mul(m2);
    }
}
