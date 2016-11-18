package edu.spbu.matrix;

/**
 * Created by 1111111 on 17.11.2016.
 */
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class MatrixSparseNewClass implements Matrix{
    public int size;
    public ConcurrentHashMap<Integer,Sparse> map;

    public MatrixSparseNewClass(String file){
        readSparse(file);
    }

    public MatrixSparseNewClass(int size){
        this.map = new ConcurrentHashMap<Integer,Sparse>();
        this.size = size;
    }

    public Matrix mul(Matrix x){
        if (x instanceof MatrixSparseNewClass) try {
            return mulSparseSparse((MatrixSparseNewClass) x);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        else return mulSparseDense((DenseMatrix) x);
    }

    public MatrixSparseNewClass mulSparseSparse(MatrixSparseNewClass other) throws InterruptedException {
        transSparce(other.map);
        MatrixSparseNewClass result = new MatrixSparseNewClass(size);

        Iterator<ConcurrentHashMap.Entry<Integer, Sparse>> iterMap1 = this.map.entrySet().iterator();

        MulSS t = new MulSS(result.map,this.map,other.map,iterMap1);

        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return result;
    }

    public class MulSS implements Runnable {
        ConcurrentHashMap<Integer, Sparse> A;
        ConcurrentHashMap<Integer, Sparse> B;
        ConcurrentHashMap<Integer, Sparse> res;
        Iterator<ConcurrentHashMap.Entry<Integer, Sparse>> E;

        public MulSS(ConcurrentHashMap<Integer, Sparse> res,ConcurrentHashMap<Integer, Sparse> A, ConcurrentHashMap<Integer, Sparse> B, Iterator<ConcurrentHashMap.Entry<Integer, Sparse>> E) {
            this.A = A;
            this.B = B;
            this.E = E;
            this.res = res;
        }

        public void run() {
            for  (Sparse sparse1 = getFreeRow(E);sparse1!=null;sparse1 = getFreeRow(E)) {
                Sparse resSparse = new Sparse();
                Iterator<HashMap.Entry<Integer, Sparse>> iterMap2 = B.entrySet().iterator();
                while (iterMap2.hasNext()) {
                    HashMap.Entry<Integer, Sparse> entry2 = iterMap2.next();
                    Sparse sparse2 = entry2.getValue();
                    if (!sparse1.mapSparseString.isEmpty() && !sparse2.mapSparseString.isEmpty()) {
                        Iterator<HashMap.Entry<Integer, Double>> iterRow1 = sparse1.mapSparseString.entrySet().iterator();
                        double r = 0;
                        while (iterRow1.hasNext()) {
                            HashMap.Entry<Integer, Double> entry3 = iterRow1.next();
                            int j = entry3.getKey();
                            if (sparse2.mapSparseString.get(j) != null) {
                                r = r + entry3.getValue() * sparse2.mapSparseString.get(j);
                            }
                        }
                        resSparse.mapSparseString.put(entry2.getKey(), r);
                    }
                }
                res.put(sparse1.index, resSparse);
            }
        }
        public Sparse getFreeRow(Iterator<ConcurrentHashMap.Entry<Integer, Sparse>> E) {
            synchronized (E) {
                if (E.hasNext()) {
                    ConcurrentHashMap.Entry<Integer, Sparse> entry1 = E.next();
                    Sparse sparse1 = entry1.getValue();
                    sparse1.index = entry1.getKey();
                    return sparse1;
                } else {
                    return null;
                }
            }
        }
    }

    public void transSparce(ConcurrentHashMap<Integer, Sparse> m){
        HashMap<Integer,Double> g = new HashMap<>();
        for (int i=1;i<size;i++){
            for (int j=i+1;j<=size;j++) {
                g.put(0,m.get(i).mapSparseString.get(j));
                m.get(i).mapSparseString.put(j, m.get(j).mapSparseString.get(i));
                m.get(j).mapSparseString.put(i, g.get(0));
            }
        }
    }

    public MatrixSparseNewClass mulSparseDense(DenseMatrix other){
        return null;
    }

    public static int getSize(String file){
        int size=0;
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

    public void readSparse(String file){
        this.size=getSize(file);
        Scanner in = null;
        try {
            in = new Scanner(new File(file));
            in.useLocale(Locale.US);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.map = new ConcurrentHashMap<>();
        for (int i = 1; i <= size; i++) {
            Sparse sparse = new Sparse();
            for (int j = 1; j <= size; j++) {
                double a = in.nextDouble();
                if (a != 0.0) {
                    sparse.mapSparseString.put(j,a);
                }
            }
            map.put(i,sparse);
        }
        in.close();
    }
}