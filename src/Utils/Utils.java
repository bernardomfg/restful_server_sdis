package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
    public static boolean fileIsValid(String file) {
        return new File(file).exists();
    }

    public static String getFileID(String file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[1024];
        int bytesRead;
        FileInputStream fis = new FileInputStream(file);
        while ((bytesRead = fis.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
        }
        fis.close();
        byte[] hash = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aHash : hash) {
            sb.append(Integer.toString((aHash & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static boolean fileExists(ArrayList<String[]> list, File file) {
        for (String[] f : list) {
            if (f[0].equals(file.getName()))
                return true;
        }
        return false;
    }

    public static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static void wait(int n) {
        long t0, t1;
        t0 = System.currentTimeMillis();
        do {
            t1 = System.currentTimeMillis();
        } while (t1 - t0 < n);
    }

    public static int getRandomInt(int range) {
        Random r = new Random();
        return r.nextInt(range);
    }

    public static Boolean portIsAvailable(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }
            if (ss != null) try {
                ss.close();
            } catch (IOException e) {
            }
        }
        return false;
    }
}
