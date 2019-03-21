package com.wabradshaw.plt.learning.determiners.data;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * A class designed to load in huge ordered files containing usage counts of "a whatever" or "an whatever" and use them
 * to create datasets suitable for machine learning. Dataset partitioning is random, so datasets may be slightly
 * different sizes.
 *
 * Input data is based on Google N-Grams:
 *   a/an word TAB year TAB match_count TAB volume_count NEWLINE
 *
 * Output entries are of the format:
 *   word TAB ratio NEWLINE
 * Where ratio is the percentage of ngrams using "an" rather than "a".
 */
public class AnDataSetBuilder {

    private Random rng = new Random();

    /**
     * Creates a number of "a or an" dataset files that can be used to train a machine learning model.
     *
     * Input data is based on Google N-Grams:
     *   a/an word TAB year TAB match_count TAB volume_count NEWLINE
     *
     * Output entries are of the format:
     *   word TAB ratio NEWLINE
     * Where ratio is the percentage of ngrams using "an" rather than "a".
     *
     * Output files are created by index (starting at 0.txt)
     *
     * @param aFile The file containing counts of ngrams that start "a ".
     * @param anFile The file containing counts of ngrams that start "an ".
     * @param outputDirectory The directory the dataset should be written into.
     * @param datasets The number of datasets to be created. If this approaches or exceeds the number of entries,
     *                 some datasets may be empty.
     */
    public void build(File aFile, File anFile, File outputDirectory, int datasets) throws IOException {
        if(!outputDirectory.exists()){
            outputDirectory.mkdir();
        }

        BufferedWriter[] fileWriters = new BufferedWriter[datasets];
        for(int i = 0; i < datasets; i++){
            fileWriters[i] = Files.newWriter(new File(outputDirectory.getPath() + "/" + i +".txt"),
                                             Charset.defaultCharset());
        }

        LineIterator as = FileUtils.lineIterator(aFile);
        LineIterator ans = FileUtils.lineIterator(anFile);
        UsageCount nextA = as.hasNext() ? new UsageCount(as.next(), false) : null;
        UsageCount nextAn = ans.hasNext() ? new UsageCount(ans.next(), true) : null;
        UsageCount current = null;

        if(nextA != null && nextA.before(nextAn)){
            current = nextA;
            nextA = as.hasNext() ? new UsageCount(as.next(), false) : null;
        } else if (nextAn != null){
            current = nextAn;
            nextAn = ans.hasNext() ? new UsageCount(ans.next(), true) : null;
        }

        while(nextA != null || nextAn != null){
            if(nextA != null && nextA.before(nextAn)){
                if(nextA.matches(current)) {
                    current.add(nextA);
                    nextA = as.hasNext() ? new UsageCount(as.next(), false) : null;
                } else {
                    fileWriters[rng.nextInt(datasets)].write(current.toString());
                    current = nextA;
                }
            } else {
                if(nextAn.matches(current)) {
                    current.add(nextAn);
                    nextAn = ans.hasNext() ? new UsageCount(ans.next(), true) : null;
                } else {
                    fileWriters[rng.nextInt(datasets)].write(current.toString());
                    current = nextAn;
                }
            }
        }

        if(current != null){
            fileWriters[rng.nextInt(datasets)].write(current.toString());
        }

        for (BufferedWriter fileWriter : fileWriters) {
            fileWriter.close();
        }
    }

    /**
     * Helper class to store information about how many times a word was prefaced with a or an.
     */
    private class UsageCount{
        private final String word;
        private int aCount = 0;
        private int anCount = 0;

        /**
         * Creates a UsageCount by parsing the input string.
         *
         * @param contents The string containing the determiner-word ngram and its count
         * @param an Whether or not it's from the an file
         */
        private UsageCount(String contents, boolean an) {
            String[] split = contents.split("\t");
            if(an) {
                this.word = split[0].substring(3);
                anCount = Integer.parseInt(split[2]);
            } else {
                this.word = split[0].substring(2);
                aCount = Integer.parseInt(split[2]);
            }
        }

        boolean matches(UsageCount candidate){
            return this.word.equals(candidate.word);
        }

        void add(UsageCount count){
            this.aCount += count.aCount;
            this.anCount += count.anCount;
        }

        boolean before(UsageCount candidate) {
            return candidate == null || this.word.compareTo(candidate.word) < 0;
        }

        @Override
        public String toString(){
            return word + "\t" + (anCount * 1.0 / (aCount + anCount)) + "\n";
        }
    }
}