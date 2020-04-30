package kernel;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import weka.core.matrix.Matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// Util class is used for general purpose operations
public class Util implements Serializable {

    // TODO: 05/03/2020 make it secure
    // get a specified column vector from weka matrix
    public static Matrix getColumnVector(Matrix data, int n){
        assert n < data.getColumnDimension();
        double[] v = new double[data.getRowDimension()];

        for (int i = 0; i < data.getRowDimension(); i++) {
            v[i] = data.get(i, n);
        }
        return new Matrix(v, data.getRowDimension());
    }

    // TODO: 05/03/2020 make it secure
    // fill matrix columns with a given vector
    public static Matrix fillToDuplicatedMatrix(Matrix vector, int n){
        // create new matrix filled with zero of dimension len(v) x n
        Matrix matrix = new Matrix(vector.getRowDimension(), n);
        for (int i = 0; i < matrix.getRowDimension(); i++) {

            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                matrix.set(i, j, vector.get(i, 0));
            }
        }
        return matrix;
    }

    // TODO: 05/03/2020  Make it secure: because matrix and vector must have the same row dimension
    // replace a specified column fof matrix with a vector
    public static void replaceColumn(Matrix matrix, Matrix column, int n){
        for (int i = 0; i < matrix.getRowDimension() ; i++) {
            matrix.set(i, n, column.get(i, 0));
        }

    }


    // TODO: 07/03/2020 make it secure because matrices must have the same dimensions
    // construct weka matrix from opencv matrix
    public static Matrix matcvToMatrix(Mat mat){
        Matrix matrix = new Matrix(mat.rows(), mat.cols());
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                matrix.set(i, j, mat.get(i, j)[0]);
            }
        }
        return matrix;
    }

    // TODO: 07/03/2020 make it secure because matrices must have the same dimensions
    // construct opencv matrix from weka weka matrix
    public static Mat matrixToMatcv(Matrix matrix){
        Mat matcv = new Mat(matrix.getRowDimension(), matrix.getColumnDimension(), CvType.CV_8UC1);
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                matcv.put(i, j, matrix.get(i, j));
            }
        }
        return matcv;
    }


    // used to diagonal elements
    public static Matrix squareDiagonal(Matrix matrix){
        Matrix out = new Matrix(matrix.getRowDimension(), matrix.getColumnDimension()) ;
        if (matrix.getRowDimension() < matrix.getColumnDimension()){
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                out.set(i, i, Math.pow(matrix.get(i, i ), 2));
            }
        }
        else {
            for (int i = 0; i < matrix.getColumnDimension(); i++) {
                out.set(i, i, Math.pow(matrix.get(i, i ), 2));
            }
        }
        return out;
    }

    // calculate mean of vectors
    public static Matrix mean(ArrayList<Matrix> vectors){
        double number_of_vectors = vectors.size();
        double div = 1.0 / number_of_vectors;
        Matrix out = new Matrix(vectors.get(0).getRowDimension(), 1);
        for (int i = 0; i < number_of_vectors; i++) {
            out.plusEquals(vectors.get(i));
        }
        out.timesEquals(div);
        return out;
    }

    public static HashMap detectFaces(String path){
        Map<Mat, Integer> result = new HashMap<>();
        CascadeClassifier faceDetector = new CascadeClassifier("libs/facedetector/haarcascade_frontalface_alt.xml");
        Mat image = Imgcodecs.imread(path); // enter the path for reading

        MatOfRect face_Detections = new MatOfRect();
        faceDetector.detectMultiScale(image, face_Detections);
        //System.out.println((face_Detections.toArray().length));
        Rect crop = null;
        int i = 1;
        for (Rect rect : face_Detections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            crop = new Rect(rect.x, rect.y, rect.width, rect.height);
            Mat image_roi = new Mat(image, crop);
            Imgcodecs.imwrite("src/userPics/face" + i + ".bmp", image_roi); //enter the path for saving

            // greyscale and resize
            ImageMat.resizeImage("src/userPics/face" + i + ".bmp");
            ImageMat.grayscaleImage("src/userPics/face" + i + ".bmp");
            i++;
        }
        result.put(image, face_Detections.toList().size());
        return (HashMap) result;
    }

}
