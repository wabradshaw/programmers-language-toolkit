package com.wabradshaw.plt.learning.determiners.data;

import com.wabradshaw.plt.learning.determiners.AOrAnTokeniser;
import org.apache.commons.io.FileUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnDatasetIterator {
    private final File dataDirectory;
    private final AOrAnTokeniser tokeniser;
    private List<Integer> candidateBatches;
    private List<Integer> usedBatches = new ArrayList<>();


    /**
     *
     *
     * @param dataDirectory The directory containing the training datasets
     * @param datasets The total number of datasets
     * @param tokeniser The tokeniser that converts a word into a set of features
     */
    public AnDatasetIterator(File dataDirectory, int datasets, AOrAnTokeniser tokeniser) {
        this.dataDirectory = dataDirectory;
        this.tokeniser = tokeniser;

        if(datasets < 1){
            throw new IllegalArgumentException("AnDatasetIterators must have at least one dataset. " +
                    "Attempted to create one with " + datasets +".");
        }

        this.candidateBatches = IntStream.range(0, datasets).boxed().collect(Collectors.toList());
        Collections.shuffle(candidateBatches);
    }

    public int getTokenLength(){
        return this.tokeniser.getTokenLength();
    }

    public DataSet next() throws IOException {
        if(this.candidateBatches.isEmpty()){
            this.candidateBatches = this.usedBatches;
            this.usedBatches = new ArrayList<Integer>();
            Collections.shuffle(this.candidateBatches);
        }

        int target = this.candidateBatches.remove(this.candidateBatches.size() - 1);
        usedBatches.add(target);
        File file = new File(this.dataDirectory.getPath() + "/" + target + ".txt");
        List<String> entries = FileUtils.readLines(file, Charset.defaultCharset());

        INDArray input = Nd4j.zeros(new int[]{entries.size(), this.getTokenLength()});
        INDArray labels = Nd4j.zeros(new int[]{entries.size(), 2});

        for(int entryId = 0; entryId < entries.size(); entryId++){
            String entry = entries.get(entryId);
            String[] split = entry.split("\t");
            String word = split[0].replace("_*","");
            Double anActivation = Double.parseDouble(split[1]);

            this.tokeniser.tokeniseComponentWord(word, entryId, input);
            labels.putScalar(new int[]{entryId, 0}, anActivation);
            labels.putScalar(new int[]{entryId, 1}, 1-anActivation);
        }

        return new DataSet(input, labels);
    }
}
