package S12345Smith;

import java.io.*;
import java.util.List;

public class FileManager {
    public static void saveToFile(List<PhotoCollection> collections, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(collections);
        }
    }

    public static List<PhotoCollection> readFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<PhotoCollection>) in.readObject();
        }
    }
}
