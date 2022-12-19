import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Trainer training=new Trainer();
        training.skinDetectTrain();
        Tester tester=new Tester();
        tester.detectSkin("src/inputs/input1.jpg");

    }
}
