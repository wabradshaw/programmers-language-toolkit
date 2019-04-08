package com.wabradshaw.plt.determiners

import spock.lang.Specification

/**
 * A set of tests for the DeterminerServiceBuilder
 */
class DeterminerServiceBuilderTest extends Specification {

    def "a default build creates a DeterminerService"() {
        when:
            DeterminerService result = new DeterminerServiceBuilder().build()

        then:
            result != null
    }

}
