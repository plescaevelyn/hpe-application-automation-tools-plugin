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

package com.microfocus.application.automation.tools.model;

import com.microfocus.application.automation.tools.sv.model.AbstractSvRunModel;
import com.microfocus.sv.svconfigurator.core.impl.jaxb.ServiceRuntimeConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

public class SvChangeModeModel extends AbstractSvRunModel {

    private static final SvDataModelSelection NONE_DATA_MODEL = new SvDataModelSelection(SvDataModelSelection.SelectionType.NONE, null);
    private static final SvPerformanceModelSelection NONE_PERFORMANCE_MODEL = new SvPerformanceModelSelection(SvPerformanceModelSelection.SelectionType.NONE, null);
    private final ServiceRuntimeConfiguration.RuntimeMode mode;
    private final SvDataModelSelection dataModel;
    private final SvPerformanceModelSelection performanceModel;

    @DataBoundConstructor
    public SvChangeModeModel(String serverName, boolean force, ServiceRuntimeConfiguration.RuntimeMode mode,
                             SvDataModelSelection dataModel, SvPerformanceModelSelection performanceModel, SvServiceSelectionModel serviceSelection) {
        super(serverName, force, serviceSelection);
        this.mode = mode;
        this.dataModel = dataModel;
        this.performanceModel = performanceModel;
    }

    public ServiceRuntimeConfiguration.RuntimeMode getMode() {
        return mode;
    }

    public SvDataModelSelection getDataModel() {
        return (mode == ServiceRuntimeConfiguration.RuntimeMode.STAND_BY) ? NONE_DATA_MODEL : dataModel;
    }

    public SvPerformanceModelSelection getPerformanceModel() {
        return (mode == ServiceRuntimeConfiguration.RuntimeMode.STAND_BY) ? NONE_PERFORMANCE_MODEL : performanceModel;
    }
}