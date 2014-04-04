/*
 * #%L
 * Wisdom-Framework
 * %%
 * Copyright (C) 2013 - 2014 Wisdom Framework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.wisdom.test.parents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.testing.helpers.Stability;
import org.wisdom.api.http.FileItem;
import org.wisdom.api.http.Status;
import org.wisdom.test.WisdomRunner;

import javax.inject.Inject;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class easing the implementation of tests.
 * It provides a couple of useful methods to retrieve the content of action's results§.
 */
@RunWith(WisdomRunner.class)
public class WisdomTest implements Status {

    /**
     * The bundle context.
     * The Wisdom Test Framework injects the bundle context of the bundle containing the test classes. Other bundle
     * contexts can be retrieve using {@code context.getBundle(id).getBundleContext()}.
     */
    @Inject
    public BundleContext context;

    /**
     * The object mapper used for JSON. Be aware that this mapper is not connected to the JSON engine of Wisdom,
     * so only the default mapping is supported.
     *
     * However, you can access it from your tests, avoiding you to recreate one.
     */
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String JSON_ERROR = "Cannot retrieve the json form of result `";

    /**
     * Helper method to build a {@link org.wisdom.api.http.FileItem} from a {@link java.io.File} object.
     *
     * @param file the file
     * @return a fake implementation of a file item.
     */
    public static FileItem from(File file) {
        return new FakeFileItem(file, null);
    }

    /**
     * Helper method to build a {@link org.wisdom.api.http.FileItem} from a {@link java.io.File} object.
     *
     * @param file  the file
     * @param field the field name of the form having sent the file
     * @return a fake implementation of a file item.
     */
    public static FileItem from(File file, String field) {
        return new FakeFileItem(file, field);
    }

    /**
     * A method executed before the execution of each test method checking that the bundle context is correctly
     * injected and that the framework is in a stable state. Stability means that all bundles are resolved (except
     * fragments), and there are no flipping states.
     */
    @Before
    public void ensureBundleContextInjection() {
        assertThat(context).isNotNull();
        Stability.waitForStability(context);
    }

    /**
     * Helper method retrieving the status code of the response to an action.
     *
     * @param result the result object from which the status code is extracted. Must not be {@literal null}.
     * @return the status code.
     */
    public int status(Action.ActionResult result) {
        return result.getResult().getStatusCode();
    }

    /**
     * Helper method retrieving the mime-type of the response to an action.
     *
     * @param result the result object from which the mime-type is extracted. Must not be {@literal null}.
     * @return the mime type, {@literal null} if the result does not set the mime-type.
     */
    public String contentType(Action.ActionResult result) {
        return result.getResult().getContentType();
    }

    /**
     * Helper method to get the content of the response as Object Node. This method is only usable for action
     * having returned JSON objects as content.
     *
     * @param result the result object from which the content is extracted. Must not be {@literal null}.
     * @return the object node
     * @throws java.lang.IllegalArgumentException if the JSON Node cannot be retrieved or built.
     */
    public ObjectNode json(Action.ActionResult result) {
        try {
            return MAPPER.valueToTree(result.getResult().getRenderable().content());
            // We catch Exception and not the declared IllegalArgumentException,
            // as Jackson often throws a ClassCastException.
        } catch (Exception e) {
            throw new IllegalArgumentException(JSON_ERROR + result + "`", e);
        }
    }

    /**
     * Helper method to get the content of the response as Array Node. This method is only usable for action
     * having returned JSON objects as content.
     *
     * @param result the result object from which the content is extracted. Must not be {@literal null}.
     * @return the array node
     * @throws java.lang.IllegalArgumentException if the JSON Node cannot be retrieved or built.
     */
    public ArrayNode jsonarray(Action.ActionResult result) {
        try {
            // Default rendering here (no extension support)
            return MAPPER.valueToTree(result.getResult().getRenderable().content());
            // We catch Exception and not the declared IllegalArgumentException,
            // as Jackson often throws a ClassCastException.
        } catch (Exception e) {
            throw new IllegalArgumentException(JSON_ERROR + result + "`", e);
        }
    }

    /**
     * Helper method to get the content of the response as String.
     *
     * @param result the result object from which the content is extracted. Must not be {@literal null}.
     * @return the String form of the result.
     * @throws java.lang.IllegalArgumentException if the String form cannot be retrieved.
     */
    public String toString(Action.ActionResult result) {
        try {
            return result.getResult().getRenderable().content().toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot retrieve the String form of result `" + result + "`", e);
        }
    }

    /**
     * Helper method to get the content of the response as byte array.
     *
     * @param result the result object from which the content is extracted. Must not be {@literal null}.
     * @return the String form of the result.
     * @throws java.lang.IllegalArgumentException if the byte array cannot be retrieved.
     */
    public byte[] toBytes(Action.ActionResult result) {
        try {
            return IOUtils.toByteArray(result.getResult().getRenderable().render(result.getContext(), result.getResult()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot retrieve the byte[] form of result `" + result + "`", e);
        }
    }


}