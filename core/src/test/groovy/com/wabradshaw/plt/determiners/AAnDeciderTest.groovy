package com.wabradshaw.plt.determiners

import spock.lang.Specification

import java.text.CharacterIterator

/**
 * A set of tests for the AAnDecider.
 */
class AAnDeciderTest extends Specification {

    def "chooseAAn without user words will delegate the decision to the model"() {
        given:
            Map<String, String> userWords = [:]
            AAnTrie model = Stub(AAnTrie.class)
            model.chooseDeterminer(_) >> "a"
            AAnDecider decider = new AAnDecider(userWords, model)

        expect:
            decider.chooseAAn(w) == r

        where:
            w     || r
            "cat" || "a"
            "owl" || "a"
    }

    def "chooseAAn with user words will use them when appropriate but delegate other decisions to the model"() {
        given:
            Map<String, String> userWords = ["cat":"a", "owl":"an", "mo":"a", "MO":"an"]
            AAnTrie model = Stub(AAnTrie.class)
            model.chooseDeterminer(_) >> "a"
            AAnDecider decider = new AAnDecider(userWords, model)

        expect:
            decider.chooseAAn(w) == r

        where:
            w     || r
            "cat" || "a"
            "owl" || "an"
            "dog" || "a"
            "mo"  || "a"
            "MO"  || "an"
            "Owl" || "a"
    }

    def "chooseAAn defaults to 'a' without a word"(){
        given:
            Map<String, String> userWords = [:]
            AAnTrie model = Stub(AAnTrie.class)
            model.chooseDeterminer(_) >> "an"
            AAnDecider decider = new AAnDecider(userWords, model)

        expect:
            decider.chooseAAn(w) == r

        where:
            w    || r
            null || "a"
            ""   || "a"
    }

    def "chooseAAn when there isn't a word can use userWords (instead of defaulting)"(){
        given:
            Map<String, String> userWords = ["":"an"]
            userWords.put(null, "an")
            AAnTrie model = Stub(AAnTrie.class)
            model.chooseDeterminer(_) >> "a"
            AAnDecider decider = new AAnDecider(userWords, model)

        expect:
          decider.chooseAAn(w) == r

        where:
            w    || r
            null || "an"
            ""   || "an"
    }
}
