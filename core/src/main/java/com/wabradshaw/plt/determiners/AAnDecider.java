package com.wabradshaw.plt.determiners;

/**
 * <p>
 * An AAnDecider chooses whether or not a string would be better represented with "a" or "an". This class is designed
 * for internal use only. You probably want the DeterminerService.
 * </p>
 * <p>
 * In general, decisions are made by comparing the start of the word with a trie based model of whether that start
 * commonly maps to "a" or "an". This should work in the vast majority of cases. However, there are some words where
 * the choice of "a" or "an" is based on dialect e.g. "a herb" vs "an herb". When an AAnDecider is created, the user
 * can supply a map of words and their preferred use of a/an.
 *</p>
 * <p>
 * All decisions are case sensitive. Capitalisation can change pronunciation, and as such changes which determiner
 * should be used. For example, "see you in a <i>mo</i>" vs "I have an <i>MO</i> of giving examples".
 * </p>
 */
class AAnDecider {

    /**
     * <p>
     * Chooses whether or not a string would be better represented with "a" or "an". Defaults to "a" for null or the
     * empty string.
     *</p>
     * <p>
     * All decisions are case sensitive. Capitalisation can change pronunciation, and as such changes which determiner
     * should be used. For example, "see you in a <i>mo</i>" vs "I have an <i>MO</i> of giving examples".
     * </p>
     * @param word The word (or series of words) being referenced. Case sensitive.
     * @return "a" or "an"
     */
    protected String chooseAAn(String word){
        return null;
    }
}
