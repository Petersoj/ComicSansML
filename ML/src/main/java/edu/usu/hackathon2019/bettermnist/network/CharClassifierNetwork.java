package edu.usu.hackathon2019.bettermnist.network;

import edu.usu.hackathon2019.bettermnist.CharDataSet;
import org.nd4j.linalg.api.ndarray.INDArray;

public class CharClassifierNetwork {

    public CharClassifierNetwork() {

    }

    /**
     * Creates a CharDataSet to interface with data more easily (Tensors to POJO)
     *
     * @param features the corresponding input features to the output labels
     * @param labels   the output labels tensor
     * @return
     */
    public CharDataSet match(INDArray features, INDArray labels) {
        int maxValueIndex = (int) labels.argMax(0).getDouble(0);
        CharDataSet charDataSet = new CharDataSet(CharDataSet.CHARACTERS[maxValueIndex]);
        charDataSet.setFeatures(features);
        charDataSet.setLabels(labels);
        return charDataSet;
    }

    /**
     * @see CharClassifierNetwork#match(INDArray, INDArray)
     */
    public CharDataSet match(INDArray labels) {
        return match(null, labels);
    }
}
