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

package com.microfocus.application.automation.tools.octane.actions.project;

import com.hp.octane.integrations.dto.parameters.CIParameter;
import com.hp.octane.integrations.dto.parameters.CIParameterType;
import com.hp.octane.integrations.dto.pipelines.PipelineNode;
import com.hp.octane.integrations.dto.pipelines.PipelinePhase;
import com.microfocus.application.automation.tools.octane.OctanePluginTestBase;
import com.microfocus.application.automation.tools.octane.tests.TestUtils;
import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.*;
import hudson.plugins.parameterizedtrigger.*;
import hudson.tasks.BuildTrigger;
import hudson.tasks.Fingerprinter;
import hudson.tasks.Shell;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: gullery
 * Date: 13/01/15
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */

@SuppressWarnings({"squid:S2699", "squid:S3658", "squid:S2259", "squid:S1872", "squid:S2925", "squid:S109", "squid:S1607", "squid:S2701"})
public class PluginMatrixTest extends OctanePluginTestBase {
    //  Structure test: matrix, no params, no children
    //
    @Test
    public void testStructureMatrixNoParamsNoChildren() throws IOException, SAXException {
        String projectName = "root-job-" + UUID.randomUUID().toString();
        rule.createProject(MatrixProject.class, projectName);

        String taskUrl = "nga/api/v1/jobs/" + projectName;
        PipelineNode pipeline = TestUtils.sendTask(taskUrl, PipelineNode.class);

        assertEquals(projectName, pipeline.getJobCiId());
        assertEquals(projectName, pipeline.getName());
        assertEquals(0, pipeline.getParameters().size());
        assertEquals(0, pipeline.getPhasesInternal().size());
        assertEquals(0, pipeline.getPhasesPostBuild().size());
    }

    //  Structure test: matrix, with params, no children
    //
    @Test
    public void testStructureMatrixWithParamsNoChildren() throws IOException, SAXException {
        String projectName = "root-job-" + UUID.randomUUID().toString();
        MatrixProject p = rule.createProject(MatrixProject.class, projectName);
        ParametersDefinitionProperty params = new ParametersDefinitionProperty(Arrays.asList(
                (ParameterDefinition) new BooleanParameterDefinition("ParamA", true, "bool"),
                (ParameterDefinition) new StringParameterDefinition("ParamB", "str", "string"),
                (ParameterDefinition) new TextParameterDefinition("ParamC", "txt", "text"),
                (ParameterDefinition) new ChoiceParameterDefinition("ParamD", new String[]{"1", "2", "3"}, "choice"),
                (ParameterDefinition) new FileParameterDefinition("ParamE", "file param")
        ));
        p.addProperty(params);

        CIParameter tmpParam;
        String taskUrl = "nga/api/v1/jobs/" + projectName;
        PipelineNode pipeline = TestUtils.sendTask(taskUrl, PipelineNode.class);

        assertEquals(projectName, pipeline.getJobCiId());
        assertEquals(projectName, pipeline.getName());
        assertEquals(5, pipeline.getParameters().size());
        assertEquals(0, pipeline.getPhasesInternal().size());
        assertEquals(0, pipeline.getPhasesPostBuild().size());

        tmpParam = pipeline.getParameters().get(0);
        assertEquals("ParamA", tmpParam.getName());
        assertEquals(CIParameterType.BOOLEAN, tmpParam.getType());
        assertEquals("bool", tmpParam.getDescription());
        assertEquals(true, tmpParam.getDefaultValue());
        assertNull(tmpParam.getChoices());

        tmpParam = pipeline.getParameters().get(1);
        assertEquals("ParamB", tmpParam.getName());
        assertEquals(CIParameterType.STRING, tmpParam.getType());
        assertEquals("string", tmpParam.getDescription());
        assertEquals("str", tmpParam.getDefaultValue());
        assertNull(tmpParam.getChoices());

        tmpParam = pipeline.getParameters().get(2);
        assertEquals("ParamC", tmpParam.getName());
        assertEquals(CIParameterType.STRING, tmpParam.getType());
        assertEquals("text", tmpParam.getDescription());
        assertEquals("txt", tmpParam.getDefaultValue());
        assertNull(tmpParam.getChoices());

        tmpParam = pipeline.getParameters().get(3);
        assertEquals("ParamD", tmpParam.getName());
        assertEquals(CIParameterType.STRING, tmpParam.getType());
        assertEquals("choice", tmpParam.getDescription());
        assertEquals("1", tmpParam.getDefaultValue());
        assertNotNull(tmpParam.getChoices());
        assertEquals(3, tmpParam.getChoices().length);
        assertEquals("1", tmpParam.getChoices()[0]);
        assertEquals("2", tmpParam.getChoices()[1]);
        assertEquals("3", tmpParam.getChoices()[2]);

        tmpParam = pipeline.getParameters().get(4);
        assertEquals("ParamE", tmpParam.getName());
        assertEquals(CIParameterType.FILE, tmpParam.getType());
        assertEquals("file param", tmpParam.getDescription());
        assertEquals("", tmpParam.getDefaultValue());
        assertNull(tmpParam.getChoices());
    }

    //  Structure test: matrix, with params, with children
    //
    @Test
    public void testStructureMatrixWithParamsWithChildren() throws IOException, SAXException {
        String projectName = "root-job-" + UUID.randomUUID().toString();
        MatrixProject p = rule.createProject(MatrixProject.class, projectName);
        FreeStyleProject p1 = rule.createFreeStyleProject("jobA");
        MatrixProject p2 = rule.createProject(MatrixProject.class, "jobB");
        FreeStyleProject p3 = rule.createFreeStyleProject("jobC");
        MavenModuleSet p4 = rule.createProject(MavenModuleSet.class, "jobD");
        CustomProject p5 = rule.getInstance().createProject(CustomProject.class, "jobE");
        ParametersDefinitionProperty params = new ParametersDefinitionProperty(Arrays.asList(
                (ParameterDefinition) new BooleanParameterDefinition("ParamA", true, "bool"),
                (ParameterDefinition) new StringParameterDefinition("ParamB", "str", "string")
        ));
        p.addProperty(params);
        p.getBuildersList().add(new TriggerBuilder(Arrays.asList(
                new BlockableBuildTriggerConfig("jobA, jobB", new BlockingBehaviour(
                        Result.FAILURE,
                        Result.UNSTABLE,
                        Result.FAILURE
                ), Arrays.asList(new AbstractBuildParameters[0])),
                new BlockableBuildTriggerConfig("jobC,jobD", null, Arrays.asList(new AbstractBuildParameters[0]))
        )));
        p.getBuildersList().add(new Shell(""));
        p.getPublishersList().add(new Fingerprinter(""));
        p.getPublishersList().add(new BuildTrigger("jobA, jobB, JobE", Result.SUCCESS));
        p.getPublishersList().add(new hudson.plugins.parameterizedtrigger.BuildTrigger(Arrays.asList(
                new BuildTriggerConfig("jobC,jobD", ResultCondition.ALWAYS, false, null)
        )));

        List<PipelinePhase> tmpPhases;
        PipelineNode tmpNode;
        CIParameter tmpParam;

        String taskUrl = "nga/api/v1/jobs/" + projectName;
        PipelineNode pipeline = TestUtils.sendTask(taskUrl, PipelineNode.class);
        assertEquals(projectName, pipeline.getJobCiId());
        assertEquals(projectName, pipeline.getName());
        assertEquals(2, pipeline.getParameters().size());

        tmpParam = pipeline.getParameters().get(0);
        assertEquals("ParamA", tmpParam.getName());
        assertEquals(CIParameterType.BOOLEAN, tmpParam.getType());
        assertEquals("bool", tmpParam.getDescription());
        assertEquals(true, tmpParam.getDefaultValue());
        assertNull(tmpParam.getChoices());

        tmpParam = pipeline.getParameters().get(1);
        assertEquals("ParamB", tmpParam.getName());
        assertEquals(CIParameterType.STRING, tmpParam.getType());
        assertEquals("string", tmpParam.getDescription());
        assertEquals("str", tmpParam.getDefaultValue());
        assertNull(tmpParam.getChoices());

        //  Phases Internal
        //
        tmpPhases = pipeline.getPhasesInternal();
        assertEquals(2, tmpPhases.size());

        //  Phase 0
        assertEquals("", tmpPhases.get(0).getName());
        assertEquals(true, tmpPhases.get(0).isBlocking());
        assertEquals(2, tmpPhases.get(0).getJobs().size());

        tmpNode = tmpPhases.get(0).getJobs().get(0);
        assertEquals("jobA", tmpNode.getJobCiId());
        assertEquals("jobA", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());
        tmpNode = tmpPhases.get(0).getJobs().get(1);
        assertEquals("jobB", tmpNode.getJobCiId());
        assertEquals("jobB", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());

        //  Phase 1
        assertEquals("", tmpPhases.get(1).getName());
        assertEquals(false, tmpPhases.get(1).isBlocking());
        assertEquals(2, tmpPhases.get(1).getJobs().size());

        tmpNode = tmpPhases.get(1).getJobs().get(0);
        assertEquals("jobC", tmpNode.getJobCiId());
        assertEquals("jobC", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());
        tmpNode = tmpPhases.get(1).getJobs().get(1);
        assertEquals("jobD", tmpNode.getJobCiId());
        assertEquals("jobD", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());

        //  Phases Post build
        //
        tmpPhases = pipeline.getPhasesPostBuild();
        assertEquals(2, tmpPhases.size());

        //  Phase 0
        assertEquals("downstream", tmpPhases.get(0).getName());
        assertEquals(false, tmpPhases.get(0).isBlocking());
        assertEquals(3, tmpPhases.get(0).getJobs().size());

        tmpNode = tmpPhases.get(0).getJobs().get(0);
        assertEquals("jobA", tmpNode.getJobCiId());
        assertEquals("jobA", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());
        tmpNode = tmpPhases.get(0).getJobs().get(1);
        assertEquals("jobB", tmpNode.getJobCiId());
        assertEquals("jobB", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());
        tmpNode = tmpPhases.get(0).getJobs().get(2);
        assertEquals("jobE", tmpNode.getJobCiId());
        assertEquals("jobE", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());

        //  Phase 1
        assertEquals("", tmpPhases.get(1).getName());
        assertEquals(false, tmpPhases.get(1).isBlocking());
        assertEquals(2, tmpPhases.get(1).getJobs().size());

        tmpNode = tmpPhases.get(1).getJobs().get(0);
        assertEquals("jobC", tmpNode.getJobCiId());
        assertEquals("jobC", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());
        tmpNode = tmpPhases.get(1).getJobs().get(1);
        assertEquals("jobD", tmpNode.getJobCiId());
        assertEquals("jobD", tmpNode.getName());
        assertEquals(0, tmpNode.getParameters().size());
        assertEquals(0, tmpNode.getPhasesInternal().size());
        assertEquals(0, tmpNode.getPhasesPostBuild().size());
    }
}
