package com.wabradshaw.plt.determiners;

import java.text.CharacterIterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * An AAnTrie is a data structure representing whether the start of a word typically gets described using "a" or "an".
 * AAnTries are arranged into a tree structure where each node represents a particular character of the string, and
 * whether most words (up to that character) use "a" or "an". Each node contains further AAnTrie nodes for future
 * letters.
 * </p>
 */
class AAnTrie {

    private final Character letter;
    private final String determiner;
    private final Map<Character, AAnTrie> children;

    /**
     * Standard constructor.
     *
     * Children are expected to be for unique letters. Behaviour is undefined if two child nodes have the same letter.
     *
     * @throws IllegalArgumentException if the determiner is null. Use the empty string instead.
     *
     * @param letter The character the trie represents. Null for the start of the string.
     * @param determiner The determiner ("a" or "an") the string up to this node represents. Not null.
     * @param children A list of child nodes representing future letters. These should be unique for each letter.
     */
    AAnTrie(Character letter, String determiner, List<AAnTrie> children){
        if(determiner == null){
            throw new IllegalArgumentException("Could not create an AAnTrie for '" + letter + "' with a null determiner.");
        }
        this.letter = letter;
        this.determiner = determiner;
        this.children = children.stream()
                                .filter(i -> i != null)
                                .filter(i -> !i.isFinal() || !i.getDeterminer().equals(this.determiner))
                                .collect(Collectors.toMap(i -> i.getLetter(),
                                                          i -> i));
    }

    /**
     * Finds the best determiner for the remainder of a word. Done by checking to see if there is a child node better
     * suited to making the decision. If so, the decision is delegated, otherwise this node's determiner is used.
     *
     * @param iterator A CharacterIterator that has just passed the letter this trie represents. I.e. getting the
     *                 calling CharacterIterator.current() will return the letter after the one this trie represents.
     * @return A determiner suited to the input word.
     */
    String chooseDeterminer(CharacterIterator iterator) {
        if(this.isFinal()){
            return this.determiner;
        } else {
            AAnTrie matchedChild = this.children.get(iterator.current());
            if(matchedChild == null){
                return this.determiner;
            } else {
                iterator.next();
                return matchedChild.chooseDeterminer(iterator);
            }
        }
    }

    /**
     * Gets the letter in the word this trie represents. Null for the start of the string.
     *
     * @return The letter in the word this trie represents.
     */
    Character getLetter(){
        return this.letter;
    }

    /**
     * Gets the determiner most words which have reached this Trie use. Note that child nodes may have different
     * determiners.
     *
     * @return The determiner most words which have reached this Trie use.
     */
    String getDeterminer(){
        return this.determiner;
    }

    /**
     * Checks whether or not this is a leaf node of the AAnTrie tree.
     *
     * @return Whether or not this is a leaf node of the AAnTrie tree.
     */
    Boolean isFinal(){
        return this.children.isEmpty();
    }
}
