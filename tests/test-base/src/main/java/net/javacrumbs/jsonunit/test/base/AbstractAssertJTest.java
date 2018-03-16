/**
 * Copyright 2009-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.javacrumbs.jsonunit.test.base;

import org.junit.Test;

import static java.math.BigDecimal.valueOf;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractAssertJTest {

    @Test
    public void exceptionTest() {
        try {
            assertThatJson("{\"b\":1}").isEqualTo(json("{'a':'${json-unit.ignore}'}"));
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldAssertObject() {
        assertThatJson("{\"a\":1}").isObject().containsEntry("a", json(1));
    }

    @Test
    public void shouldAssertDirectEqual() {
        assertThatJson("{\"a\":1}").isEqualTo(json("{'a':'${json-unit.ignore}'}"));
    }

    @Test
    public void shouldAssertObjectJson() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": 1}}").node("a").isObject().isEqualTo(json("{\"b\": 2}")))
            .hasMessage("[Different value found in node \"a\"] \n" +
                "Expecting:\n" +
                " <{\"b\":1}>\n" +
                "to be equal to:\n" +
                " <{\"b\":2}>\n" +
                "when comparing values using JsonComparator\n" +
                "but was not.");
    }

    @Test
    public void shouldAssertObjectJsonWithPlaceholder() {
         assertThatJson("{\"a\":{\"b\": \"ignored\"}}").node("a").isObject().isEqualTo(json("{'b':'${json-unit.any-string}'}"));
    }

    @Test
    public void shouldAssertObjectJsonWithPlaceholderFailure() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": 1}}").node("a").isObject().isEqualTo(json("{'b':'${json-unit.any-string}'}")))
            .hasMessage("[Different value found in node \"a\"] \n" +
                "Expecting:\n" +
                " <{\"b\":1}>\n" +
                "to be equal to:\n" +
                " <{\"b\":${json-unit.any-string}}>\n" +
                "when comparing values using JsonComparator\n" +
                "but was not.");
    }

    @Test
    public void shouldAssertString() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": \"foo\"}}").node("a.b").isString().startsWith("bar"))
            .hasMessage("[Different value found in node \"a.b\"] \n" +
                "Expecting:\n" +
                " <\"foo\">\n" +
                "to start with:\n" +
                " <\"bar\">\n");
    }

    @Test
    public void shouldAssertArray() {
        assertThatJson("{\"a\":[1, 2, 3]}").node("a").isArray().contains(valueOf(3));
    }

    @Test
    public void shouldFindObjectInArray() {
        assertThatJson("{\"a\":[{\"b\": 1}, {\"c\": 1}, {\"d\": 1}]}").node("a").isArray().contains(json("{\"c\": 1}"));
    }

    @Test
    public void shouldFindObjectInArrayWithPlaceholder() {
        assertThatJson("{\"a\":[{\"b\": 1}, {\"c\": 1}, {\"d\": 1}]}").node("a").isArray().contains(json("{\"c\": \"${json-unit.any-number}\"}"));
    }

    @Test
    public void arrayIgnoringOrderComparison() {
        assertThatJson("{\"a\":[{\"b\": 1}, {\"c\": 1}, {\"d\": 1}]}").node("a").isArray()
            .containsExactlyInAnyOrder(json("{\"c\": 1}"), json("{\"b\": 1}"), json("{\"d\": 1}"));
    }

    @Test
    public void shouldAssertBoolean() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": true}}").node("a.b").isBoolean().isFalse())
            .hasMessage("[Different value found in node \"a.b\"] expected:<[fals]e> but was:<[tru]e>");
    }

    @Test
    public void shouldAssertNull() {
        assertThatJson("{\"a\":{\"b\": null}}").node("a.b").isNull();
    }

    @Test
    public void shouldAssertNullFail() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": 1}}").node("a.b").isNull())
            .hasMessage("Node \"a.b\" has invalid type, expected: <null> but was: <1>.");
    }

    @Test
    public void shouldAssertNotNull() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": null}}").node("a.b").isNotNull())
            .hasMessage("Node \"a.b\" has invalid type, expected: <not null> but was: <null>.");
    }

    @Test
    public void shouldAssertNotNullChaining() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": 1}}").node("a").isNotNull().node("b").isNumber().isEqualByComparingTo("2"))
            .hasMessage("[Different value found in node \"a.b\"] expected:<[2]> but was:<[1]>");
    }

    @Test
    public void shouldAssertNotNullMissing() {
        assertThatThrownBy(() -> assertThatJson("{\"a\":{\"b\": null}}").node("a.c").isNotNull())
            .hasMessage("Different value found in node \"a.c\", expected: <not null> but was: <missing>.");
    }

    @Test
    public void shouldAssertObjectFailure() {
        assertThatThrownBy(() -> assertThatJson("true").isObject())
            .hasMessage("Node \"\" has invalid type, expected: <object> but was: <true>.");
    }

    @Test
    public void shouldAssertNumber() {
        assertThatJson("{\"a\":1}").node("a").isNumber().isEqualByComparingTo("1");
    }

    @Test
    public void shouldAssertNumberFailure() {
        assertThatThrownBy(() ->  assertThatJson("{\"a\":1}").node("a").isNumber().isEqualByComparingTo("2"))
            .hasMessage("[Different value found in node \"a\"] expected:<[2]> but was:<[1]>");
    }
}
