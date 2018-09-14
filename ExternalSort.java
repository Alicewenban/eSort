package uk.ac.cam.amw216.fjava.tick0;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.Math;

public class ExternalSort {

    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {

        //TODO: Complete this method
        //readin
        RandomAccessFile a = new RandomAccessFile(f1, "rw");
        RandomAccessFile a1 = new RandomAccessFile(f1, "rw");
        RandomAccessFile b = new RandomAccessFile(f2, "rw");
        RandomAccessFile b1 = new RandomAccessFile(f2, "rw");

        System.out.println(a.length()/4);
        boolean copyReq=false;
        for(int q=1;q<a.length()/4;q=q*2) {
           a.seek(0);
           a1.seek(q*4);
           b.seek(0);

           for (int p = 0; p <Math.ceil( a.length() / 8); p = p + q) {
               merge(a, a1, b, q);
               a.skipBytes(q * 4);
               a1.skipBytes(q * 4);
           }
           System.out.println("pass complete a - b for size : " + q);

           q = q * 2;

           a.seek(0);
           b.seek(0);
           b1.seek(q * 4);
           if(a.length()/4>q) {
                for (int r = 0; r <Math.ceil( a.length() / 8); r = r + q) {
                    merge(b, b1, a, q);
                    b.skipBytes(q * 4);
                    b1.skipBytes(q * 4);
                }
               System.out.println("pass complete b-a for size : " + q);
           }else{
               copyReq=true;
           }
       }
        a.seek(0);
        b.seek(0);
       if(copyReq){
           DataOutputStream d = new DataOutputStream(
                   new BufferedOutputStream(new FileOutputStream(a.getFD())));
           for (int i = 0; i < Math.ceil(a.length()/4); i++) {
               int v = b.readInt();
               if(v==0){
                   System.out.println("0 at copy");
               }
               d.writeInt(v);
           }
           d.flush();
       }
       // System.out.println("Read four bytes as an int value " + f.readInt());
        //System.out.println("The file is " + f.length() + " bytes long");

        a.seek(0);
        for(int i=0; i<a.length();i=i+4){
        //    System.out.println("Read four bytes as an int value " + a.readInt());
        }
    }
    private static RandomAccessFile merge(RandomAccessFile a1, RandomAccessFile a2, RandomAccessFile b1,int maxNumberToMerge) throws FileNotFoundException, IOException{
        DataOutputStream d = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(b1.getFD())));

        int sizeOfFirstBlock = Math.min(maxNumberToMerge,(Math.toIntExact(a1.length())-Math.toIntExact(a1.getFilePointer()))/4);
        int sizeOfSecondBlock = Math.min(maxNumberToMerge,(Math.toIntExact(a2.length())-Math.toIntExact(a2.getFilePointer()))/4);

        int blockAtoMerge[] = new int [sizeOfFirstBlock];
        int blockBtoMerge[] = new int [sizeOfSecondBlock];
        //System.out.println("first : "+ sizeOfFirstBlock + " second "+ sizeOfSecondBlock);


        for (int i = 0; i < sizeOfFirstBlock; i++) {
            blockAtoMerge[i] = a1.readInt();
        }
        for (int i = 0; i < sizeOfSecondBlock; i++) {
            blockBtoMerge[i] = a2.readInt();
        }
        if(sizeOfSecondBlock!=0L) {
            int arrayAIndex = 0;
            int arrayBIndex = 0;
            while (arrayAIndex < sizeOfFirstBlock && arrayBIndex < sizeOfSecondBlock) {
                if (blockAtoMerge[arrayAIndex] < blockBtoMerge[arrayBIndex]) {
                    if(blockAtoMerge[arrayAIndex]==0){
                        System.out.print("92: 0s");
                    }
                    d.writeInt(blockAtoMerge[arrayAIndex]);
                    arrayAIndex++;
                } else {
                    if(blockBtoMerge[arrayBIndex]==0){
                        System.out.print("98: 0s");
                    }
                    d.writeInt(blockBtoMerge[arrayBIndex]);
                    arrayBIndex++;
                }
            }
            while(arrayAIndex<sizeOfFirstBlock){
                if(blockAtoMerge[arrayAIndex]==0){
                    System.out.print("106: 0s");
                }
                d.writeInt(blockAtoMerge[arrayAIndex]);
                arrayAIndex++;
            }
            while(arrayBIndex<sizeOfSecondBlock){
                if(blockBtoMerge[arrayBIndex]==0){
                    System.out.print("113 :0s");
                }
                d.writeInt(blockBtoMerge[arrayBIndex]);
                arrayBIndex++;
            }
        }else{
            int arrayAIndex=0;
            while(arrayAIndex<sizeOfFirstBlock){
                if(blockAtoMerge[arrayAIndex]==0){
                    System.out.print(" cased by 0 0s");
                }
                d.writeInt(blockAtoMerge[arrayAIndex]);
                arrayAIndex++;
            }
        }
        d.flush();
        return b1;
    }

    private static String byteToHex(byte b) {
        String r = Integer.toHexString(b);
        if (r.length() == 8) {
            return r.substring(6);
        }
        return r;
    }

    public static String checkSum(String f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream ds = new DigestInputStream(
                    new FileInputStream(f), md);
            byte[] b = new byte[512];
            while (ds.read(b) != -1)
                ;

            String computed = "";
            for(byte v : md.digest())
                computed += byteToHex(v);

            return computed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<error computing checksum>";
    }

    public static void main(String[] args) throws Exception {
        String path = "src\\uk\\ac\\cam\\amw216\\fjava\\tick0\\";
        String f1 = path+args[0];
        String f2 = path+args[1];
        sort(f1, f2);

        System.out.println("The checksum is: "+checkSum(f1));

        RandomAccessFile f = new RandomAccessFile("datatest","rw");
        DataOutputStream d = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(f.getFD())));
        //d.writeInt(12);

      //  d.writeInt(1); //write calls now only store primitive ints in memory
        //d.writeInt(2);
        //d.writeInt(3);
        //d.flush(); //force the contents to be written to the disk. Important!
        //f.seek(4);
        //System.out.println("Read four bytes as an int value "+f.readInt());
        //System.out.println("The file is "+f.length()+" bytes long");
    }
}