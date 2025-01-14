/*
 * Certain versions of software and/or documents ("Material") accessible here may contain branding from
 * Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 * the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 * and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 * marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * (c) Copyright 2012-2023 Micro Focus or one of its affiliates.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * ___________________________________________________________________
 */

package com.microfocus.application.automation.tools.sse.common;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.microfocus.application.automation.tools.common.SSEException;
import com.microfocus.application.automation.tools.sse.sdk.Logger;
import hudson.FilePath;
import hudson.model.Node;
import hudson.util.IOUtils;
import jenkins.model.Jenkins;
import net.minidev.json.JSONArray;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by barush on 06/11/2014.
 */
public class JsonHandler {

    private static Logger logger;

    public JsonHandler(Logger logger) {
        this.logger = logger;
    }

    public Object load(String selectedNode, String path) {

        logger.log(String.format("Loading JSON file from: [%s]", path));
        Object parsedJson;
        try {
            String jsonTxt = "";
            if (selectedNode.equals("master")) {
                jsonTxt = getStream(new File(path));
            } else {
                Node node = Jenkins.getInstance().getNode(selectedNode);
                FilePath filePath = new FilePath(node.getChannel(), path);
                JsonHandlerMasterToSlave uftMasterToSlave = new JsonHandlerMasterToSlave();
                try {
                    jsonTxt = filePath.act(uftMasterToSlave);
                } catch (IOException e) {
                    logger.log(String.format("File path not found %s", e.getMessage()));
                } catch (InterruptedException e) {
                    logger.log(String.format("Remote operation failed %s", e.getMessage()));
                }
            }
            parsedJson =
                    Configuration.defaultConfiguration().addOptions(Option.ALWAYS_RETURN_LIST).jsonProvider().parse(
                            jsonTxt);
        } catch (Throwable e) {
            throw new SSEException(String.format("Failed to load JSON from: [%s]", path), e);
        }

        return parsedJson;
    }

    public static String getStream(File path) {
        InputStream is = null;
        try {
            is = new FileInputStream(String.valueOf(path));

        } catch (FileNotFoundException e) {
            logger.log(String.format("File path not found %s", e.getMessage()));
        }

        String jsonText = "";
        try {
            jsonText = IOUtils.toString(is, String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.log(String.format("Failed to create the json object %s", e.getMessage()));
        }

        return jsonText;
    }

    public String getValueFromJsonAsString(
            Object jsonObject,
            String pathToRead,
            boolean shouldGetSingleValueOnly) {

        String value = "";
        try {
            Object extractedObject = JsonPath.read(jsonObject, pathToRead);
            while (extractedObject instanceof JSONArray && shouldGetSingleValueOnly) {
                extractedObject = ((JSONArray) extractedObject).get(0);
            }
            value = extractedObject.toString();
        } catch (Throwable e) {
            logger.log(String.format(
                    "Failed to get the value of [%s] from the JSON file.\n\tError was: %s",
                    pathToRead,
                    e.getMessage()));
        }
        return value;

    }
}
