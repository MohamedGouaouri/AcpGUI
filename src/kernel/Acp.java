package kernel;
//la classe acp

import weka.core.matrix.Matrix;
import weka.core.matrix.SingularValueDecomposition;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Acp implements Serializable {

    private int totalTrainImagesNumber = 200;
    private int trainImagesNumber = 5;
    final int height = 112;
    final int width = 92;
    private String path = "src/sample/orl/";
    private final int personImages=10;

    private double threshold;
    private Matrix dataSet;
    private Matrix centers;
    private Matrix projectedCenters;
    private Matrix mean;
    private EigenSpace eigenSpace;
    public HashMap<String, Number> distancesMap = new HashMap<>();


    public Acp(double threshold){
        this.threshold = threshold;
    }


    // TODO: 13/03/2020 method tested
    public Matrix importerImages(String path){

        File directory = new File(path);
        Matrix total = new Matrix(height * width, directory.listFiles().length * trainImagesNumber);
        int i ;
        int j = 0;
        File[] images;

        for (File fd : directory.listFiles()) {
            distancesMap.put(fd.getName(), 0);
            images = fd.listFiles();
            for (i = 0; i < trainImagesNumber; i++) {
                assert images != null;
                Matrix mat = ImageMat.imageToVector(images[i].getPath());

                Util.replaceColumn(total,mat,i+j);
            }
            j = j + trainImagesNumber;
        }


        return total;
    }

    // TODO: 16/03/2020 method tested 
    public Matrix calculerVisageMoyen(Matrix dataSet){
        Matrix mean = new Matrix(dataSet.getRowDimension(), 1);
        int columnDim = dataSet.getColumnDimension();
        double s;
        for (int i = 0; i < dataSet.getRowDimension(); i++) {
            s = 0;
            for (int j = 0; j < dataSet.getColumnDimension(); j++) {
                s += dataSet.get(i, j);
            }
            mean.set(i, 0, Math.floor(s/columnDim));
        }
        return mean;
    }


    // used to calculate the reduced dimension of the new eigenspace
    public Matrix reduireDimensions(Matrix eigenvectors, Matrix eigenvalues, double percentage){

        double perc = percentage;
        double trace = eigenvalues.trace();
        double s = 0;
        int i = 0;
        int cols = eigenvalues.getColumnDimension();
        int rows = eigenvalues.getRowDimension();
        while ((s <= perc * trace) && (i < cols) && (i < rows)){
            s += eigenvalues.get(i, i);
            i++;
        }

        Matrix out = new Matrix(eigenvectors.getRowDimension(), i);
        for (int j = 0; j < i; j++) {
            Util.replaceColumn(out, Util.getColumnVector(eigenvectors, j), j);
        }
        return out;
    }


    // create the eigenspace from dataSet
    public EigenSpace creerEigenSpace(Matrix eigenvectors, int dim){
        eigenSpace = new EigenSpace(eigenvectors, dim);
        return eigenSpace;
    }

    // TODO: 16/03/2020 test this method 
    // project data onto the new eigenspace
    public Matrix projectData(EigenSpace eigenSpace, Matrix dataSet){

        // creating empty matrix that will hold the projected data on the new face space
        Matrix projectedData = new Matrix(eigenSpace.getDimension(), dataSet.getColumnDimension());

        for (int i = 0; i < dataSet.getColumnDimension() ; i++) {
                // coordinates matrix is a p x 1 matrix
                Matrix coordinates = eigenSpace.getCoordinates(Util.getColumnVector(dataSet, i));
                // insert coordinates matrix in the projected dataMatrix
                Util.replaceColumn(projectedData, coordinates, i);
        }

        return projectedData;
    }


    // TODO: 14/03/2020 check this
    public void calculateCenters(Matrix dataSet){
        centers = new Matrix(dataSet.getRowDimension(), Math.floorDiv(dataSet.getColumnDimension(), trainImagesNumber));
        ArrayList<Matrix> arrayList = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < totalTrainImagesNumber - trainImagesNumber + 1; i+=trainImagesNumber) {
            for (int j = i; j < i + trainImagesNumber; j++) {
                arrayList.add(Util.getColumnVector(dataSet, j));
            }

            Util.replaceColumn(centers, Util.mean(arrayList), k);
            k++;
            arrayList.clear();
        }
    }


    // TODO: 16/03/2020 test this 
    // our main method used to train the model
    public Matrix trainModel(double percentage){

        // import faces from database
        dataSet = importerImages(path);

        // calculate mean
        mean = calculerVisageMoyen(dataSet);

        // subtract mean from data set
        dataSet.minusEquals(Util.fillToDuplicatedMatrix(mean, dataSet.getColumnDimension()));

        // calculate the eigenvectors of the covariance matrix
        SingularValueDecomposition singularValueDecomposition = dataSet.svd();
        Matrix eigenvectors = singularValueDecomposition.getU();
        Matrix singularValues = singularValueDecomposition.getS();
        Matrix eigenvalues = Util.squareDiagonal(singularValues);

        // the reduced eigenspace dimension
        Matrix newBase = reduireDimensions(eigenvectors, eigenvalues, percentage);

        // create the eigenspace
        eigenSpace = creerEigenSpace(newBase, newBase.getColumnDimension());


        calculateCenters(dataSet);
        // project data classes onto the new eigenspace
        Matrix projectedDataClasses = projectData(eigenSpace, centers);
        projectedCenters = projectedDataClasses;

        return projectedDataClasses;
    }


    // TODO: 16/03/2020 test this 
    // recognize the new image
    public Result recognize(String path){

        // convert input face to vector
        Matrix inputFaceMatrix = ImageMat.imageToVector(path);

        // subtract mean from inputFaceMatrix
        inputFaceMatrix.minusEquals(mean);

        // project the inputFaceMatrix onto the eigen space
        Matrix projectedInputFaceMatrix = projectData(eigenSpace, inputFaceMatrix);
        

        // calculate distances
        ArrayList<Double> distances = new ArrayList<>();
        for (int i = 0; i < centers.getColumnDimension() ; i++) {

            distances.add(eigenSpace.getDistance(Util.getColumnVector(projectedCenters, i), projectedInputFaceMatrix));
        }



        Iterator<Double> iterator1 = distances.iterator();
        int i = 0;
        File directory = new File(this.path);
        while (iterator1.hasNext()){
            double value = iterator1.next();
            String[] dirList = directory.list();
            distancesMap.put(dirList[i], value);
            i++;

        }

        int foundFaces = 0;
        Iterator<Double> iterator2 = distances.iterator();
        while (iterator2.hasNext()){
            if (iterator2.next() <= threshold){
                foundFaces++;
            }
        }
        if (foundFaces == 0){
            return Result.REJETE;
        }
        if (foundFaces == 1){
            return Result.RECONNUE;
        }
        return Result.CONFUSION;

    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public EigenSpace getEigenSpace() {
        return eigenSpace;
    }


}
