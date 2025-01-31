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

package com.microfocus.application.automation.tools.results;

import hudson.model.DirectoryBrowserSupport;
import hudson.model.ModelObject;
import hudson.model.Run;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Models the HTML summary reports
 */
public class SummaryReport implements ModelObject {

    private String name = "";
    private String color = "";
    private String duration = "";
    private String pass = "";
    private String fail = "";
    private Run<?,?> build = null;
    private DirectoryBrowserSupport _directoryBrowserSupport = null;

    /**
     * Instantiates a new Summary report.
     *
     * @param build                   the build
     * @param name                    the name
     * @param directoryBrowserSupport the directory browser support
     */
    public SummaryReport(Run<?,?> build, String name, DirectoryBrowserSupport directoryBrowserSupport) {
        this.build = build;
        this.name = name;
        _directoryBrowserSupport = directoryBrowserSupport;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    /**
     * Gets build.
     *
     * @return the build
     */
    public Run<?,?> getBuild() {
        return build;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Do dynamic.
     *
     * @param req the req
     * @param rsp the rsp
     * @throws IOException      the io exception
     * @throws ServletException the servlet exception
     */
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {

        if (_directoryBrowserSupport != null)
            _directoryBrowserSupport.generateResponse(req, rsp, this);
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param value the value
     */
    public void setColor(String value) {
        color = value;
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets duration.
     *
     * @param value the value
     */
    public void setDuration(String value) {
        duration = value;
    }

    /**
     * Gets pass.
     *
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * Sets pass.
     *
     * @param value the value
     */
    public void setPass(String value) {
        pass = value;
    }

    /**
     * Gets fail.
     *
     * @return the fail
     */
    public String getFail() {
        return fail;
    }

    /**
     * Sets fail.
     *
     * @param value the value
     */
    public void setFail(String value) {
        fail = value;
    }

}
