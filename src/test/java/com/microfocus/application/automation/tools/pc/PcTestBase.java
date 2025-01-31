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

import com.microfocus.adm.performancecenter.plugins.common.pcentities.*;

import java.io.PrintStream;

public interface PcTestBase {

	public static final String        SERVER_AND_PORT                 = "jenkins.server:8082";
	public static final String        PC_SERVER_NAME                  = "pcServer.hp.com";
	public static final String        CREDENTIALSID                   = "123456789";
    public static final String        ALM_DOMAIN                      = "ALMDOM";
    public static final String        ALM_PROJECT                     = "ALMPROJ";
    public static final String        TEST_ID                         = "1";
    public static final String        TEST_INSTANCE_ID                = "2";
    public static final String        TIMESLOT_DURATION_HOURS         = "0";
    public static final String        TIMESLOT_DURATION_MINUTES       = "34";
    public static final String        TIMESLOT_ID                     = "56";
    public static final PostRunAction POST_RUN_ACTION                 = PostRunAction.COLLATE_AND_ANALYZE;
    public static final boolean       VUDS_MODE                       = false;
    public static final String        DESCRIPTION                     = "Testing HPE Performance Center Jenkins plugin";
    public static final String        RUN_ID                          = "7";
    public static final String        RUN_ID_WAIT                     = "8";
    public static final String        REPORT_ID                       = "9";
    public static final String        STOP_MODE                       = "stop";
    public static final String		  WEB_PROTOCOL					  = "http";
	public static final Boolean		  IS_HTTPS					  	  = false;
	public static final String 	  TESTINSTANCEID				= "MANUAL";
	public static final PrintStream LOGGER					  	  = null;
	public static final String 	  RETRY				= "NO_RETRY";
	public static final String 	  RETRYDELAY				= "5";
	public static final String 	  RETRYOCCURRENCES				= "3";
	public static final boolean       AUTHENTICATE_WITH_TOKEN                       = false;

    public static final MockPcModel   pcModel                         = new MockPcModel(SERVER_AND_PORT,PC_SERVER_NAME, CREDENTIALSID, ALM_DOMAIN, ALM_PROJECT,
                                                                          TEST_ID,TESTINSTANCEID, TEST_INSTANCE_ID,
                                                                          TIMESLOT_DURATION_HOURS,
                                                                          TIMESLOT_DURATION_MINUTES, POST_RUN_ACTION,
                                                                          VUDS_MODE, DESCRIPTION,IS_HTTPS, RETRY, RETRYDELAY, RETRYOCCURRENCES, AUTHENTICATE_WITH_TOKEN);
    
    public static final String          runResponseEntity  = "<Run xmlns=\"http://www.hp.com/PC/REST/API\">" +
    		                                                    "<TestID>" + TEST_ID + "</TestID>" +
	                                                    		"<TestInstanceID>" + TEST_INSTANCE_ID + "</TestInstanceID>" +
                                                				"<PostRunAction>" + POST_RUN_ACTION.getValue() + "</PostRunAction>" +
                                        						"<TimeslotID>1076</TimeslotID>" +
                                        						"<VudsMode>false</VudsMode>" +
                                        						"<ID>" + RUN_ID + "</ID>" +
                                								"<Duration>" + TIMESLOT_DURATION_MINUTES + "</Duration>" +
                        										"<RunState>*</RunState>" +
                        										"<RunSLAStatus />" + 
                                                             "</Run>";
    
    public static final String          emptyResultsEntity = "<RunResults xmlns=\"http://www.hp.com/PC/REST/API\" />";
    
    public static final String          runResultsEntity   = "<RunResults xmlns=\"http://www.hp.com/PC/REST/API\">" +
    		                                                    "<RunResult>" +
    		                                                        "<ID>1302</ID>" +
        		                                                    "<Name>output.mdb.zip</Name>" +
        		                                                    "<Type>Output Log</Type>" +
        		                                                    "<RunID>" + RUN_ID + "</RunID>" +
    		                                                    "</RunResult>" +
    		                                                    "<RunResult>" +
    		                                                        "<ID>1303</ID>" +
    		                                                        "<Name>RawResults.zip</Name>" +
    		                                                        "<Type>Raw Results</Type>" +
    		                                                        "<RunID>" + RUN_ID + "</RunID>" +
		                                                        "</RunResult>" +
		                                                        "<RunResult>" +
		                                                            "<ID>1304</ID>" +
		                                                            "<Name>Results.zip</Name>" +
		                                                            "<Type>Analyzed Result</Type>" +
		                                                            "<RunID>" + RUN_ID + "</RunID>" +
	                                                            "</RunResult>" +
	                                                            "<RunResult>" +
	                                                                "<ID>" + REPORT_ID + "</ID>" +
	                                                                "<Name>Reports.zip</Name>" +
	                                                                "<Type>HTML Report</Type>" +
	                                                                "<RunID>" + RUN_ID + "</RunID>" +
                                                                "</RunResult>" +
                                                                "<RunResult>" +
                                                                    "<ID>1306</ID>" +
                                                                    "<Name>HighLevelReport_7.xls</Name>" +
                                                                    "<Type>Rich Report</Type>" +
                                                                    "<RunID>" + RUN_ID + "</RunID>" +
                                                                "</RunResult>" +
                                                             "</RunResults>";

    public static final String        pcAuthenticationFailureMessage    = "Exception of type 'HPE.PC.API.Model.Exceptions.InvalidAuthenticationDataException' was thrown. Error code: 1100";

    public static final String        pcNoTimeslotExceptionMessage      = "Failed to retrieve reservation information for reservation " + TIMESLOT_ID + ". Error code: 1202";

    public static final String        pcStopNonExistRunFailureMessage   = "Failed to retrieve run " + RUN_ID + " information from domain " + ALM_DOMAIN + ", project " + ALM_PROJECT + ". Error code: 1300";

    public static final String 		  testResponseEntity =			 "<Test>" +
																	"<ID>2</ID>" +
																	"<Name>test1</Name>" +
																	"</Test>";
    
}
