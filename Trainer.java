import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Trainer {
    private double[][][] skinPixCount=new double[256][256][256];
    private double[][][] nonSkinPixCount=new double[256][256][256];
    private double[][][] skinProbability=new double[256][256][256];
    private double[][][] nonSkinProbability=new double[256][256][256];
    public static double[][][] probabilityLearnt=new double[256][256][256];

    private double totalSkinPixel=0;
    private double totalNonSkinPixel=0;
    public void skinDetectTrain() throws IOException {
        List<File> masked= Arrays.asList(new File("src/ibtd/Mask").listFiles());
        List<File> nonMasked=Arrays.asList(new File("src/ibtd/NonMask").listFiles());
        for(int i=0;i<masked.size();i++){
            countSkinNonSkin(masked.get(i),nonMasked.get(i));
        }

        probabilityOfSkinNonSkin();
    }

    public void countSkinNonSkin(File masked,File nonMasked) throws IOException,NullPointerException {
        BufferedImage maskedImage = ImageIO.read(masked);

        BufferedImage nonMaskedImage=ImageIO.read(nonMasked);
        for(int i=0;i<maskedImage.getWidth();i++){
            for(int j=0;j<maskedImage.getHeight();j++){

                Color maskColor=new Color(maskedImage.getRGB(i,j),true);
                Color nonMaskColor=new Color(nonMaskedImage.getRGB(i,j),true);

                if(isSkin(maskColor.getRed(),maskColor.getGreen(),maskColor.getBlue())){
                    this.skinPixCount[maskColor.getRed()][maskColor.getGreen()][maskColor.getBlue()]+=1;
                    totalSkinPixel++;
                }
                else {
                    this.nonSkinPixCount[nonMaskColor.getRed()][nonMaskColor.getGreen()][nonMaskColor.getBlue()]++;
                    totalNonSkinPixel++;
                }

            }
        }
    }
    public void probabilityOfSkinNonSkin(){
        for(int i=0;i<256;i++){
            for(int j=0;j<256;j++){
                for(int k=0;k<256;k++){
                    skinProbability[i][j][k]=skinPixCount[i][j][k]/totalSkinPixel;
                    nonSkinProbability[i][j][k]=nonSkinPixCount[i][j][k]/totalNonSkinPixel;
                    if(skinProbability[i][j][k]==0.0&&nonSkinProbability[i][j][k]==0.0){
                        probabilityLearnt[i][j][k]=0.0;
                    }
                    else if(nonSkinProbability[i][j][k]==0.0){
                        probabilityLearnt[i][j][k]=100.0;
                    }
                    else{
                        probabilityLearnt[i][j][k]=skinProbability[i][j][k]/nonSkinProbability[i][j][k];
                    }
                }
            }
        }
    }
    public boolean isSkin(int r,int g, int b){
        if(r==255&&g==255&&b==255){
            return false;
        }
        return true;
    }

}
