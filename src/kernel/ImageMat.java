package kernel;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import weka.core.matrix.Matrix;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.io.*;

/*
* this class is used for image processing in face detection in PCA
* It contains several methods helping to convert image to matrices, resize images and greyscale them
*/
public class ImageMat implements Serializable {

    
    // construct an opencv matrix from an image
    public static Mat imageToMatcv(String path){

        return Imgcodecs.imread(path);
    }

    // construct javafx image from an opencv matrix
    public static Image matcvToImage(Mat matrix){
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".bmp", matrix, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        Image image = new Image(in);
        return image;
    }

    // construct weka one dimensional vector from an image
    public static Matrix imageToVector(String path){

        Matrix matrix = Util.matcvToMatrix(imageToMatcv(path));
        double[] packedArray = matrix.getRowPackedCopy();
        return new Matrix(packedArray, packedArray.length);

    }


    public static Image vectorToImage(Matrix matrix) {
        int h = 112;
        int w = 92;
        Matrix out = new Matrix(h, w);
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < h * w){
            out.set(k, j, matrix.get(i, 0));
            j++;
            i++;
            if (j == w){
                k++;
                j = 0;
            }
        }
        return matcvToImage(Util.matrixToMatcv(out));
    }

    // greyscale image in place
    public static void grayscaleImage(String path){
        Mat source = imageToMatcv(path);
        Mat destination = new Mat();
        Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
        Imgcodecs.imwrite(path, destination);
    }


    // resize image in place
    public static void resizeImage(String path){
        Mat source = imageToMatcv(path);
        Mat resized = new Mat();
        Size newSize = new Size(92, 112);
        Imgproc.resize(source, resized, newSize, Imgproc.INTER_AREA);
        Imgcodecs.imwrite(path, resized);
    }

    public static void convertToBMP(String path) throws IOException {
        ImageIO.write(ImageIO.read(new File(path)), "BMP",  new FileOutputStream(path));
    }
}
