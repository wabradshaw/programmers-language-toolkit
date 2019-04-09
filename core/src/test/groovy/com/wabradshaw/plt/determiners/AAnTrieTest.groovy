package com.wabradshaw.plt.determiners

import spock.lang.Specification

import java.text.StringCharacterIterator

/**
 * A set of tests for the AAnTrie.
 */
class AAnTrieTest extends Specification {

    def "test getLetter"(){
        given:
            def trie = new AAnTrie(x as Character, "an", [])

        expect:
            trie.getLetter() == r as Character

        where:
            x    || r
            'a'  || 'a'
            'd'  || 'd'
            'A'  || 'A'
            '4'  || '4'
            'ú'  || 'ú'
            null || null
    }

    def "test getDeterminer"(){
        given:
            def trie = new AAnTrie('z' as Character, x, [])

        expect:
            trie.getDeterminer() == r

        where:
        x    || r
        "a"  || "a"
        "an" || "an"
        "le" || "le"
    }

    def "test null determiners are unacceptable"(){
        when:
            new AAnTrie('z' as Character, null, [])

        then:
            thrown(IllegalArgumentException)
    }

    def "test an empty AAnTrie is final"(){
        given:
            def trie = new AAnTrie(null, "a", [])

        expect:
            trie.isFinal()
    }

    def "test an AAnTrie with a different child isn't final"(){
        given:
            def child = new AAnTrie('z' as Character, "an", [])
            def trie = new AAnTrie(null, "a", [child])

        expect:
            !trie.isFinal()
    }

    def "test an AAnTrie with the same child is final"(){
        given:
            def child = new AAnTrie('z' as Character, "an", [])
            def trie = new AAnTrie(null, "an", [child])

        expect:
            trie.isFinal()
    }

    def "test that null children are ignored for AAnTries"(){
        given:
            def trie = new AAnTrie(null, "a", [null])

        expect:
            trie.isFinal()
    }

    def "test an AAnTrie with a mix of different child isn't final"(){
        given:
            def child1 = new AAnTrie('x' as Character, "a", [])
            def child2 = new AAnTrie('y' as Character, "an", [])
            def child3 = new AAnTrie('z' as Character, "a", [])
            def trie = new AAnTrie(null, "a", [child1, child2, child3])

        expect:
            !trie.isFinal()
    }

    def "test an AAnTrie where all children are redundant is final"(){
        given:
            def child1 = new AAnTrie('x' as Character, "a", [])
            def child2 = new AAnTrie('y' as Character, "a", [])
            def child3 = new AAnTrie('z' as Character, "a", [])
            def trie = new AAnTrie(null, "a", [child1, child2, child3])

        expect:
            trie.isFinal()
    }

    def "test an AAnTrie with a matching child but differing descendants isn't final"(){
        given:
            def grandchild = new AAnTrie('z' as Character, "an", [])
            def child = new AAnTrie('z' as Character, "a", [grandchild])
            def trie = new AAnTrie(null, "a", [child])

        expect:
            !trie.isFinal()
    }

    def "test choosing a determiner from a final trie returns that determiner"(){
        given:
            def trie = new AAnTrie(null, "a", [])
            def iterator = new StringCharacterIterator("cat")

        when:
            def result = trie.chooseDeterminer(iterator)

        then:
            result == "a"
    }

    def "test choosing a determiner for an empty iterator returns the current determiner"(){
        given:
            def child1 = new AAnTrie('a' as Character, "an", [])
            def child2 = new AAnTrie('e' as Character, "an", [])
            def child3 = new AAnTrie('i' as Character, "an", [])
            def trie = new AAnTrie(null, "a", [child1, child2, child3])
            def iterator = new StringCharacterIterator("")

        when:
            def result = trie.chooseDeterminer(iterator)

        then:
            result == "a"
    }

    def "test choosing a determiner can match a child node"(){
        given:
            def child1 = new AAnTrie('h' as Character, "a", [])
            def child2 = new AAnTrie('i' as Character, "an", [])
            def child3 = new AAnTrie('j' as Character, "a", [])
            def trie = new AAnTrie(null, "a", [child1, child2, child3])
            def iterator = new StringCharacterIterator("igloo")

        when:
            def result = trie.chooseDeterminer(iterator)

        then:
            result == "an"
    }

    def "test choosing a determiner can match a descendant node"(){
        given:
            def greatGrandchild = new AAnTrie('i' as Character, "a", [])
            def grandchild = new AAnTrie('n' as Character, "an", [greatGrandchild])
            def child = new AAnTrie('u' as Character, "an", [grandchild])
            def trie = new AAnTrie(null, "an", [child])
            def iterator = new StringCharacterIterator("university")

        when:
            def result = trie.chooseDeterminer(iterator)

        then:
            result == "a"
    }

    def "test choosing a determiner which doesn't match a child will use the current node"(){
        given:
            def child1 = new AAnTrie('a' as Character, "an", [])
            def child2 = new AAnTrie('e' as Character, "an", [])
            def child3 = new AAnTrie('i' as Character, "an", [])
            def trie = new AAnTrie(null, "a", [child1, child2, child3])
            def iterator = new StringCharacterIterator("box")

        when:
            def result = trie.chooseDeterminer(iterator)

        then:
            result == "a"
    }

}
