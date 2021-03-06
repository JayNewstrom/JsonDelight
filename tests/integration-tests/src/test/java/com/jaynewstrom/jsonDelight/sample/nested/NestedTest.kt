package com.jaynewstrom.jsonDelight.sample.nested

import com.jaynewstrom.jsonDelight.sample.JsonTestHelper
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Test

class NestedTest {
    @Test fun testAllTheThings() {
        val outer = JsonTestHelper().deserializeFile(Outer::class.java, "NestedTest.json", this)
        assertThat(outer.one).isEqualTo("a")
        assertThat(outer.two.foo).isEqualTo("b")
        val json = JsonTestHelper().serialize(outer, Outer::class.java)
        assertThat(json).isEqualTo("{\"one\":\"a\",\"two\":{\"foo\":\"b\"}}")
    }

    @Test fun testOptionalInner() {
        val outer = JsonTestHelper().deserializeFile(OuterWithOptionalInner::class.java, "NestedOptionalTest.json", this)
        assertThat(outer.one).isEqualTo("a")
        assertThat(outer.two).isNull()
        val json = JsonTestHelper().serialize(outer, OuterWithOptionalInner::class.java)
        assertThat(json).isEqualTo("{\"one\":\"a\",\"two\":null}")
    }
}
