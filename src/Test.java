import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        Path path = Paths.get("C:\\Users\\bxczp\\Desktop\\test.txt");

        List<ModelC> listM = new ArrayList<ModelC>();

        ModelC m = null;

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = null;
            String source = null;
            while ((line = reader.readLine()) != null) {
                if (line != null && !"".equals(line)) {

                    if (line.trim().startsWith("dependencies")) {
                        source = line;
                    }
                    if (line.trim().startsWith("Line")) {
                        m = new ModelC();
                        m.setSource(source);
                        m.setCode(line);
                        listM.add(m);
                    }
                }
            }
        }
        
        for (ModelC c : listM) {
            System.out.println(c);
        }

    }

}
