package dev.tujger.ddc;

import org.junit.Test;

@SuppressWarnings("FieldCanBeLocal")
public class MainTest {

    private String inputFileName = "./src/main/resources/input-test1.txt";
    private String outputFileName = "./src/main/resources/output-test.txt";

    @Test
    public void main() {
        Main.main(new String[]{});
        Main.main(new String[]{inputFileName});
        Main.main(new String[]{inputFileName, outputFileName});
        Main.main(new String[]{inputFileName + "-not-exists", outputFileName});
    }
}