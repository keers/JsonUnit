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
package net.javacrumbs.jsonunit.assertj;

import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.internal.Diff;
import net.javacrumbs.jsonunit.core.internal.Node;
import net.javacrumbs.jsonunit.core.internal.Path;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.BigDecimalAssert;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.StringAssert;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.javacrumbs.jsonunit.core.internal.Diff.quoteTextValue;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.getNode;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.nodeAbsent;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.ARRAY;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.BOOLEAN;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.NULL;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.NUMBER;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.OBJECT;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.STRING;

public class JsonAssert extends AbstractAssert<JsonAssert, Object> {
    private final Path path;
    private final Configuration configuration;

    JsonAssert(Path path, Configuration configuration, Object o) {
        super(o, JsonAssert.class);
        this.path = path;
        this.configuration = configuration;
        usingComparator(new JsonComparator(configuration));
    }

    public JsonAssert node(String node) {
        return new JsonAssert(path.to(node), configuration, getNode(actual, node));
    }

    public MapAssert<String, Object> isObject() {
        Node node = assertType(OBJECT);
        return new MapAssert<>((Map<String, Object>) node.getValue())
            .as("Different value found in node \"%s\"", path)
            .usingComparator(new JsonComparator(configuration));
    }

    public BigDecimalAssert isNumber() {
        Node node = assertType(NUMBER);
        return new BigDecimalAssert(node.decimalValue()).as("Different value found in node \"%s\"", path);
    }

    public ListAssert<Object> isArray() {
        Node node = assertType(ARRAY);
        return new ListAssert<Object>((List<?>)node.getValue())
            .as("Different value found in node \"%s\"", path)
            .usingElementComparator(new JsonComparator(configuration));
    }

    public BooleanAssert isBoolean() {
        Node node = assertType(BOOLEAN);
        return new BooleanAssert((Boolean) node.getValue()).as("Different value found in node \"%s\"", path);
    }

    public StringAssert isString() {
        Node node = assertType(STRING);
        return new StringAssert((String) node.getValue()).as("Different value found in node \"%s\"", path);
    }

    @Override
    public void isNull() {
        assertType(NULL);
    }

    @Override
    public JsonAssert isNotNull() {
        isPresent("not null");
        Node node = getNode(actual, "");
        if (node.getNodeType() == NULL) {
            failOnType(node, "not null");
        }
        return this;
    }

    private Node assertType(Node.NodeType type) {
        isPresent(type.getDescription());
        Node node = getNode(actual, "");
        if (node.getNodeType() != type) {
            failOnType(node, type.getDescription());
        }
        return node;
    }

    private void isPresent(String expectedValue) {
        if (nodeAbsent(actual, "", configuration)) {
            failOnDifference(expectedValue, "missing");
        }
    }

    private void failOnDifference(Object expected, Object actual) {
        failWithMessage(String.format("Different value found in node \"%s\", expected: <%s> but was: <%s>.", path, expected, actual));
    }

    private void failOnType(Node node, String expectedTypeDescription) {
        failWithMessage("Node \"" + path + "\" has invalid type, expected: <" + expectedTypeDescription + "> but was: <" + quoteTextValue(node.getValue()) + ">.");
    }


    private static class JsonComparator implements Comparator<Object> {
        private final Configuration configuration;

        private JsonComparator(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public int compare(Object o1, Object o2) {
            Diff diff = Diff.create(o2, o1, "", Path.create(""), configuration);
            if (diff.similar()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
