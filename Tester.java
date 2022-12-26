import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Tester {

    public void detectSkin(String inputPath) throws IOException {
        File file=new File(inputPath);
        BufferedImage inputImg= ImageIO.read(file);
        for(int i=0;i<inputImg.getWidth();i++){
            for(int j=0;j<inputImg.getHeight();j++){
                Color color= new Color(inputImg.getRGB(i,j),true);
                if(Trainer.probabilityLearnt[color.getRed()][color.getGreen()][color.getBlue()]<=0.5){
                    inputImg.setRGB(i,j,Color.white.getRGB());
                }
            }
        }
        ImageIO.write(inputImg,"jpg",new File("src/outputs/"+file.getName()));
    }
}
