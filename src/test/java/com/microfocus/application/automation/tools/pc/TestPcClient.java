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

package com.microfocus.application.automation.tools.pc;

import com.microfocus.application.automation.tools.run.PcBuilder;
import hudson.FilePath;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.microfocus.adm.performancecenter.plugins.common.rest.PcRestProxy;

import com.microfocus.adm.performancecenter.plugins.common.pcentities.*;

@SuppressWarnings({"squid:S2699","squid:S3658"})
public class TestPcClient {
         
    private static PcClient pcClient;
    public final String RESOURCES_DIR = getClass().getResource("").getPath();

    @BeforeClass
    public static void setUp() {
        try {
            PcRestProxy resetProxy = new MockPcRestProxy(PcTestBase.WEB_PROTOCOL, PcTestBase.PC_SERVER_NAME, PcTestBase.AUTHENTICATE_WITH_TOKEN, PcTestBase.ALM_DOMAIN,
                PcTestBase.ALM_PROJECT,PcTestBase.LOGGER);
            pcClient = new PcClient(PcTestBase.pcModel, System.out, resetProxy);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Test
    public void testStartRun(){
        System.out.println("Testing Start Run with PC client");
        try {
            Assert.assertTrue("Failed to start run with pcClient", pcClient.startRun() > 0);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }       
    }

    @Test (timeout=5000)
    public void testWaitForRunCompletion(){
        
        System.out.println("Testing Wait for Run Completion with PC client");
        try {
            PcRunResponse response = pcClient.waitForRunCompletion(Integer.parseInt(PcTestBase.RUN_ID_WAIT), 200);
            Assert.assertEquals(response.getRunState(), RunState.FINISHED.value());
        } catch (InterruptedException e) {            
            Assert.fail("pcClient did not return from waitForRunCompletion (test run has timed out)");
        }catch (Exception e) {
            Assert.fail(e.toString());
        }       
    }
    
    @Test
    public void testPublishRunReport(){
        
        System.out.println("Testing Publish PC Run Report to Jenkins server with PC client");
        try {
            
            FilePath reportHtml = pcClient.publishRunReport(Integer.parseInt(PcTestBase.RUN_ID),
                String.format(PcBuilder.getRunReportStructure(), RESOURCES_DIR, PcBuilder.getArtifactsDirectoryName(), PcTestBase.RUN_ID));
            Assert.assertTrue("Failed to publish PC run report", reportHtml.exists());
            try {
                // Test cleanup
                reportHtml.getParent().getParent().getParent().deleteRecursive();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            Assert.fail(e.toString());
        }       
    }    
    
    @Test
    public void testLogout() {        
        System.out.println("Testing Logout from PC server");
        Assert.assertTrue("Failed to logout with pcClient", pcClient.logout());
    }

    @Test
    public void testStopRun() {        
        System.out.println("Testing Stop Run with PC client");
        Assert.assertTrue("Failed to stop run with pcClient", pcClient.stopRun(Integer.parseInt(PcTestBase.RUN_ID)));
    }
    
    
}
  
