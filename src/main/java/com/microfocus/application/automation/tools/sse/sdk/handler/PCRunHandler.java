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

package com.microfocus.application.automation.tools.sse.sdk.handler;

import java.net.MalformedURLException;
import java.net.URL;

import com.microfocus.application.automation.tools.common.SSEException;
import com.microfocus.application.automation.tools.sse.sdk.Args;
import com.microfocus.application.automation.tools.sse.sdk.Client;
import com.microfocus.application.automation.tools.sse.sdk.PCRunResponse;
import com.microfocus.application.automation.tools.sse.sdk.Response;
import com.microfocus.application.automation.tools.sse.sdk.RunResponse;

/***
 * 
 * @author Effi Bar-She'an
 * @author Dani Schreiber
 * 
 */
public class PCRunHandler extends RunHandler {
    
    public PCRunHandler(Client client, String entityId) {
        
        super(client, entityId);
    }
    
    @Override
    protected String getStartSuffix() {
        
        return String.format("test-instances/%s/startrun", _entityId);
    }
    
    @Override
    public String getNameSuffix() {
        
        return String.format("runs/%s", _runId);
    }
    
    @Override
    public String getReportUrl(Args args) {
        
        String ret = "No report URL available";
        try {
            ret =
                    String.format(
                            "td://%s.%s.%s:8080/qcbin/[TestRuns]?EntityLogicalName=run&EntityID=%s",
                            args.getProject(),
                            args.getDomain(),
                            new URL(args.getUrl()).getHost(),
                            _runId);
        } catch (MalformedURLException ex) {
            throw new SSEException(ex);
        }
        return ret;
    }
    
    @Override
    public RunResponse getRunResponse(Response response) {
        
        RunResponse ret = new PCRunResponse();
        ret.initialize(response);
        
        return ret;
        
    }
}
