package com.wabradshaw.plt.learning.determiners;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;

public class AOrAnTokeniser {

    public static final int TOKENS = 38;

    private final int characters;
    private final int tokenLength;

    public AOrAnTokeniser(int characters) {
        if(characters < 1){
            throw new IllegalArgumentException("AnDatasetIterators must consider at least one character." +
                    "Attempted to create one with " + characters +".");
        }
        this.characters = characters;
        this.tokenLength = this.characters * TOKENS;
    }

    public INDArray tokeniseWord(String word){
        INDArray matrix = Nd4j.zeros(new int[]{1, this.tokenLength});
        tokeniseComponentWord(word, 0, matrix);
        return matrix;
    }

    public INDArray tokeniseWords(List<String> words){
        INDArray matrix = Nd4j.zeros(new int[]{words.size(), this.tokenLength});
        for(int i = 0; i < words.size(); i++) {
            tokeniseComponentWord(words.get(i), i, matrix);
        }
        return matrix;
    }

    public void tokeniseComponentWord(String word, int row, INDArray matrix){
        for(int charId = 0; charId < Math.min(word.length(), characters); charId++){
            int mod = charId*AOrAnTokeniser.TOKENS;
            char c = word.charAt(charId);
            if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
                if(Character.isUpperCase(c)){
                    matrix.putScalar(new int[]{row, 1 + mod}, 1);
                    c = Character.toLowerCase(c);
                }
                int pos = c - 'a';
                matrix.putScalar(new int[]{row, 12 + pos + mod}, 1);
            } else if(Character.isDigit(c)) {
                int pos = Character.getNumericValue(c);
                matrix.putScalar(new int[]{row, 2 + pos + mod}, 1);
            } else {
                matrix.putScalar(new int[]{row, mod}, 1);
            }
        }
    }

    public int getTokenLength(){
        return this.tokenLength;
    }
}
