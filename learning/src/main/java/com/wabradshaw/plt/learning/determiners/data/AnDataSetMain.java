package com.wabradshaw.plt.learning.determiners.data;

import java.io.File;
import java.io.IOException;

/**
 * A main class to generate a dataset using the AnDataSetBuilder.
 */
public class AnDataSetMain {

    /**
     * Builds a dataset using a/an data. See the {@link AnDataSetBuilder} for more details.
     *
     * Takes four arguments, in this order:
     * 1. The path to the file of "a" ngrams
     * 2. The path to the file of "an" ngrams
     * 3. The output directory
     * 4. The number of datasets to produce
     *
     * @param args See above
     */
    public static void main(String[] args){
        if(args.length != 4){
            throw new IllegalArgumentException("This method expects four args: the path to 'a' file, " +
                    "the path to 'an' file, the output directory, and the number of datasets to produce. " +
                    args.length + " arg(s) were given.");
        }
        File as = new File(args[0]);
        if(!as.exists()){
            throw new IllegalArgumentException("The a file '" + args[0] +"' does not exist.");
        }

        File ans = new File(args[1]);
        if(!ans.exists()){
            throw new IllegalArgumentException("The an file '" + args[1] +"' does not exist.");
        }

        File out = new File(args[2]);

        int datasets = Integer.parseInt(args[3]);

        System.out.println("Processing started");
        try {
            new AnDataSetBuilder().build(as, ans, out, datasets);
        } catch (IOException e) {
            throw new RuntimeException("Could not complete dataset generation.", e);
        }
        System.out.println("Processing complete");
    }
}
