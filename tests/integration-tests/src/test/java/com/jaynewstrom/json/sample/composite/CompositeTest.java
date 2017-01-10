package com.jaynewstrom.json.sample.composite;

import com.jaynewstrom.compositetest.Composite;
import com.jaynewstrom.json.sample.JsonTestHelper;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public final class CompositeTest {
    @Test public void testCustomDeserializer() {
        JsonTestHelper testHelper = new JsonTestHelper();
        Composite composite = testHelper.deserializeString(Composite.class, "{\"foo\":\"bar\"}");
        assertThat(composite.foo).isEqualTo("bar");
    }
}
