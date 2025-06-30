import java.io.File;

public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Missing arguments :(");
            return;
        }

        File alienFloraFile = new File(args[0]);
        AlienFlora alienFlora = new AlienFlora(alienFloraFile);

        alienFlora.readGenomes();
        alienFlora.evaluateEvolutions();
        alienFlora.evaluateAdaptations();
    }
}
