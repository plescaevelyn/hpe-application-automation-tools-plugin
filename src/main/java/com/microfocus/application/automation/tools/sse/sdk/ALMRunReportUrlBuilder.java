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

package com.microfocus.application.automation.tools.sse.sdk;

import com.microfocus.application.automation.tools.common.ALMRESTVersionUtils;
import com.microfocus.application.automation.tools.common.SSEException;
import com.microfocus.application.automation.tools.model.ALMVersion;
import com.microfocus.application.automation.tools.sse.sdk.request.GetALMVersionRequest;

/**
 * @author Effi Bar-She'an
 */
public class ALMRunReportUrlBuilder {

    public String build(Client client, String serverUrl, String domain, String project, String runId) {
        String ret = "NA";
        try {
            ALMVersion version = getALMVersion(client);
            int majorVersion = toInt(version.getMajorVersion());
            int minorVersion = toInt(version.getMinorVersion());
            if (majorVersion < 12 || (majorVersion == 12 && minorVersion < 2)) {
                ret = client.buildWebUIRequest(String.format("lab/index.jsp?processRunId=%s", runId));
            } else if (majorVersion >= 16) {
                // Url change due to angular js upgrade from ALM16
                ret = String.format("%sui/?redirected&p=%s/%s&execution-report#!/test-set-report/%s",
                        serverUrl,
                        domain,
                        project,
                        runId);
            } else {
                ret = String.format("%sui/?redirected&p=%s/%s&execution-report#/test-set-report/%s",
                        serverUrl,
                        domain,
                        project,
                        runId);
            }
        } catch (Exception e) {
            // result url will be NA (in case of failure like getting ALM version, convert ALM version to number)
        }
        return ret;
    }

    public boolean isNewReport(Client client) {
        ALMVersion version = getALMVersion(client);
        // Newer than 12.2x, including 12.5x, 15.x and later
        return (toInt(version.getMajorVersion()) == 12 && toInt(version.getMinorVersion()) >= 2)
                || toInt(version.getMajorVersion()) > 12;
    }

    private int toInt(String str) {

        return Integer.parseInt(str);
    }

    private ALMVersion getALMVersion(Client client) {

        ALMVersion ret = null;
        Response response = new GetALMVersionRequest(client).execute();
        if(response.isOk()) {
            ret = ALMRESTVersionUtils.toModel(response.getData());
        } else {
            throw new SSEException(
                    String.format("Failed to get ALM version. HTTP status code: %d", response.getStatusCode()),
                    response.getFailure());
        }

        return ret;
    }
}
