package com.wabradshaw.plt.learning.determiners.network;

import com.wabradshaw.plt.learning.determiners.data.AnDatasetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.dataset.DataSet;

import java.io.IOException;
import java.util.Date;

public class AnNetworkTrainer {

    private final AnDatasetIterator datasetIterator;

    private static final int L1 = 64;
    private static final int L2 = 64;
    private static final int L3 = 64;
    private static final int L4 = 64;

    /**
     * Creates the network trainer.
     *
     * @param datasetIterator The iterator that provides datasets for training
     */
    public AnNetworkTrainer(AnDatasetIterator datasetIterator) {
        this.datasetIterator = datasetIterator;
    }

    public MultiLayerNetwork train(int batches, int sampleFrequency) throws IOException {
        MultiLayerNetwork network = createNetwork();
        return this.train(batches, sampleFrequency, network);
    }

    public MultiLayerNetwork train(int batches, int sampleFrequency, MultiLayerNetwork network) throws IOException {

        for(int batch = 0; batch < batches; batch++){
            DataSet dataSet = this.datasetIterator.next();
            dataSet.shuffle();
            network.fit(dataSet);

            if((batch % sampleFrequency) == 0 || (batch == batches - 1)){
                System.out.println("Completed batch " + (batch+1) + " at " + new Date());
            }
        }
        return network;
    }

    private MultiLayerNetwork createNetwork(){
        MultiLayerConfiguration networkConfiguration = new NeuralNetConfiguration.Builder()
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam())
                .l2(1e-4)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(datasetIterator.getTokenLength()) // Number of input datapoints.
                        .nOut(L1) // Number of output datapoints.
                        .activation(Activation.RELU) // Activation function.
                        .weightInit(WeightInit.XAVIER) // Weight initialization.
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(L1) // Number of input datapoints.
                        .nOut(L2) // Number of output datapoints.
                        .activation(Activation.RELU) // Activation function.
                        .weightInit(WeightInit.XAVIER) // Weight initialization.
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(L2) // Number of input datapoints.
                        .nOut(L3) // Number of output datapoints.
                        .activation(Activation.RELU) // Activation function.
                        .weightInit(WeightInit.XAVIER) // Weight initialization.
                        .build())
                .layer(new DenseLayer.Builder()
                        .nIn(L3) // Number of input datapoints.
                        .nOut(L4) // Number of output datapoints.
                        .activation(Activation.RELU) // Activation function.
                        .weightInit(WeightInit.XAVIER) // Weight initialization.
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(L4)
                        .nOut(2)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true)
                .build();

        MultiLayerNetwork network = new MultiLayerNetwork(networkConfiguration);
        network.init();
        return network;
    }
}
