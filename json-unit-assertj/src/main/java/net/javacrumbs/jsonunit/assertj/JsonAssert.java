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
import net.javacrumbs.jsonunit.core.internal.Node;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.BigDecimalAssert;

import java.util.Map;

import static net.javacrumbs.jsonunit.core.internal.Diff.quoteTextValue;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.getNode;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.nodeAbsent;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.NUMBER;
import static net.javacrumbs.jsonunit.core.internal.Node.NodeType.OBJECT;

public class JsonAssert extends AbstractAssert<JsonAssert, Object> {
    private final String path;
    private final Configuration configuration;

    JsonAssert(String path, Configuration configuration, Object o) {
        super(o, JsonAssert.class);
        this.path = path;
        this.configuration = configuration;
    }

    public JsonAssert node(String path) {
        return new JsonAssert(path, configuration, actual);
    }

    public JsonObjectAssert isObject() {
        Node node = assertType(OBJECT);
        return new JsonObjectAssert((Map<String, Object>) node.getValue());
    }

    public BigDecimalAssert isNumber() {
        Node node = assertType(NUMBER);
        return new BigDecimalAssert(node.decimalValue()).as("Different value found in node \"%s\"", path);
    }

    private Node assertType(Node.NodeType type) {
        isPresent(type.getDescription());
        Node node = getNode(actual, path);
        if (node.getNodeType() != type) {
            failOnType(node, type);
        }
        return node;
    }

    private void isPresent(String expectedValue) {
        if (nodeAbsent(actual, path, configuration)) {
            failOnDifference(expectedValue, "missing");
        }
    }

    private void failOnDifference(Object expected, Object actual) {
        failWithMessage(String.format("Different value found in node \"%s\", expected: <%s> but was: <%s>.", path, expected, actual));
    }

    private void failOnType(Node node, final Node.NodeType expectedType) {
        failWithMessage("Node \"" + path + "\" has invalid type, expected: <" + expectedType.getDescription() + "> but was: <" + quoteTextValue(node.getValue()) + ">.");
    }
}
