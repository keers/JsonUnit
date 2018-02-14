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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class AbstractAssertJTest {

    @Test
    public void shouldAssertObject() {
        assertThatJson("{\"a\":1}").isObject().containsEntry("a", valueOf(1));
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
