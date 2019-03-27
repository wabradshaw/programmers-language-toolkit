package com.wabradshaw.plt.learning.determiners.network;

import com.wabradshaw.plt.learning.determiners.AOrAnTokeniser;
import com.wabradshaw.plt.learning.determiners.data.AnDataSetBuilder;
import com.wabradshaw.plt.learning.determiners.data.AnDatasetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * A main class to generate a dataset using the AnDataSetBuilder.
 */
public class AnNetworkTrainerMain {

    static List<String> testWords = Arrays.asList(new String[]{"apple", "cat", "potato", "university", "history", "eagle", "inventory", "yam", "umpire", "NLG", "MBA", "Scottish", "x-ray", "X-Ray", "1234"});
    public static void main(String[] args){
        try {
            AOrAnTokeniser tokeniser = new AOrAnTokeniser(4);
            AnDatasetIterator datasetIterator = new AnDatasetIterator(new File("C:\\Code\\programmers-language-toolkit\\learning\\src\\main\\resources\\determiners\\data"),
                                                                      4096,
                    tokeniser);
            AnNetworkTrainer trainer = new AnNetworkTrainer(datasetIterator);
            System.out.println("Training started");
            MultiLayerNetwork network = trainer.train(1, 1);
            System.out.println("Training complete");

            for(int i = 0; i < 50; i++) {
                System.out.println("Starting epoch " + i);
                File locationToSave = new File("C:\\Code\\programmers-language-toolkit\\learning\\src\\main\\resources\\determiners\\model.zip");
                ModelSerializer.writeModel(network, locationToSave, true);

//                System.out.println(network.summary());
                INDArray trained = network.output(tokeniser.tokeniseWords(testWords));

               // System.out.println(trained);

                for(int x = 0; x < trained.rows(); x++){
                    double[] row = trained.getRow(x).toDoubleVector();

                    System.out.println(testWords.get(x) + " " + row[0] + " " + row[1] + " " +((row[0] > row[1]) ? "an" : "a"));
                }

                network = trainer.train(1024, 256, network);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not complete dataset generation.", e);
        }
    }
}
