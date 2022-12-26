import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AccuracyMeasure {
    private List<File> maskImages=new ArrayList<>();
    private List<File> nonMaskImages=new ArrayList<>();
    private int dataToBeTrained=0;
    private int dataToBeTested=0;
    private int dataSize=0;
    private double accuracy;
    private int[] index=null;
    public AccuracyMeasure(String maskPath, String nonMaskPath){
        maskImages.addAll(Arrays.asList(new File(maskPath).listFiles()));
        nonMaskImages.addAll(Arrays.asList(new File(nonMaskPath).listFiles()));
        dataSize=maskImages.size();
        index=new int[dataSize];
        accuracy=0.0;
        dataToBeTrained= (int) Math.ceil(dataSize*0.9);
        dataToBeTested=dataSize-dataToBeTrained;
    }
    public void accuracyTraining(Trainer trainer) throws IOException {
        for(int i=0;i<dataToBeTrained;i++){
            trainer.countSkinNonSkin(maskImages.get(i),nonMaskImages.get(i));
        }
    }
    public void accuracyTesting(Trainer trainer) throws IOException {
        double trueNegative=0;
        double truePositive=0;
        double falseNegative=0;
        double falsePositive=0;
        for(int i=dataSize-dataToBeTested;i<dataSize;i++){
            BufferedImage nonMaskedImage= ImageIO.read(nonMaskImages.get(i));
            BufferedImage maskedImage=ImageIO.read(maskImages.get(i));

            for(int w=0;w<nonMaskedImage.getWidth();w++) {
                for (int h = 0; h < nonMaskedImage.getHeight(); h++) {
                    Color nonMaskedColor = new Color(nonMaskedImage.getRGB(w, h), true);
                    Color maskedColor = new Color(maskedImage.getRGB(w, h), true);

                    if (!isSkin(nonMaskedColor, trainer)) {
                        if (isTrulyNonSkin(maskedColor)) {
                            falseNegative++;
                        } else {
                            trueNegative++;
                        }
                    } else {
                        if (isTrulySkin(maskedColor)) {
                            truePositive++;
                        } else {
                            falsePositive++;
                        }
                    }
                }
            }
        }
        accuracy+=(truePositive+falseNegative)/(trueNegative+truePositive+falseNegative+falsePositive);
    }
    public boolean isTrulyNonSkin(Color maskedColor){
        int maskedRed=maskedColor.getRed();
        int maskedGreen=maskedColor.getGreen();
        int maskedBlue=maskedColor.getBlue();

        return maskedRed>250&&maskedGreen>250&&maskedBlue>250;
    }
    public boolean isTrulySkin(Color maskedColor){
        int maskedRed=maskedColor.getRed();
        int maskedGreen=maskedColor.getGreen();
        int maskedBlue=maskedColor.getBlue();

        return maskedRed<=250&&maskedGreen<=250&&maskedBlue<=250;
    }
    private boolean isSkin(Color nonMaskedColor, Trainer trainer){
        int nonMaskedRed=nonMaskedColor.getRed();
        int nonMaskedGreen=nonMaskedColor.getGreen();
        int nonMaskedBlue=nonMaskedColor.getBlue();
        return trainer.probabilityLearnt[nonMaskedRed][nonMaskedGreen][nonMaskedBlue]>0.4;
    }
    public static void main(String[] args) throws IOException {
        AccuracyMeasure accuracyMeasure=new AccuracyMeasure("src/ibtd/Mask", "src/ibtd/NonMask");
        for(int i=0;i<10;i++){
            Trainer trainer=new Trainer();
            Collections.shuffle(Arrays.asList(accuracyMeasure.index));
            accuracyMeasure.accuracyTraining(trainer);
            accuracyMeasure.accuracyTesting(trainer);
        }
        System.out.println("Accuracy: "+(accuracyMeasure.accuracy/10.0)*100.0+"%");
    }
}
