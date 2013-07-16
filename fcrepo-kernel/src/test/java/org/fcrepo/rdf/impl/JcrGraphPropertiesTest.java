/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.rdf.impl;

import static com.hp.hpl.jena.query.DatasetFactory.createMem;
import static com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.fcrepo.rdf.GraphProperties.URI_SYMBOL;
import static org.fcrepo.utils.JcrRdfTools.getGraphSubject;
import static org.fcrepo.utils.JcrRdfTools.getJcrPropertiesModel;
import static org.fcrepo.utils.JcrRdfTools.getJcrTreeModel;
import static org.fcrepo.utils.JcrRdfTools.getProblemsModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.fcrepo.DummyURIResource;
import org.fcrepo.rdf.GraphSubjects;
import org.fcrepo.utils.JcrPropertyStatementListener;
import org.fcrepo.utils.JcrRdfTools;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.util.Symbol;


@RunWith(PowerMockRunner.class)
//PowerMock needs to ignore some packages to prevent class-cast errors
//PowerMock needs to ignore unnecessary packages to keep from running out of heap
@PowerMockIgnore({
    "org.slf4j.*",
    "org.apache.xerces.*",
    "javax.xml.*",
    "org.xml.sax.*",
    "javax.management.*",
    "com.hp.hpl.*",
    "com.google.common.*",
    "com.codahale.metrics.*"
    })
@PrepareForTest({JcrRdfTools.class, JcrPropertyStatementListener.class})
public class JcrGraphPropertiesTest {

    private JcrGraphProperties testObj = new JcrGraphProperties();

    @Mock
    private Node mockNode;

    @Mock
    private GraphSubjects mockSubjects;

    private Dataset testDataset;

    @Before
    public void setUp() {
        testDataset = createMem();
    }

    @Test
    public void testGetPropertiesDataset() throws RepositoryException {

        mockStatic(JcrRdfTools.class);
        final Resource mockResource = new DummyURIResource("info:fedora/xyz");
        when(getGraphSubject(mockSubjects, mockNode)).thenReturn(mockResource);

        final Model propertiesModel = createDefaultModel();
        when(getJcrPropertiesModel(mockSubjects, mockNode)).thenReturn(
                propertiesModel);
        final Model treeModel = createDefaultModel();
        when(getJcrTreeModel(mockSubjects, mockNode, 0, -1)).thenReturn(
                treeModel);
        final Model problemsModel = createDefaultModel();
        when(getProblemsModel()).thenReturn(
                problemsModel);
        final Dataset dataset =
            testObj.getProperties(mockNode, mockSubjects, testDataset, 0, -1);

        assertTrue(dataset.containsNamedModel(testObj.getPropertyModelName()));
        assertEquals(treeModel, dataset.getNamedModel(testObj
                .getPropertyModelName()));

        assertEquals(propertiesModel, dataset.getDefaultModel());
        assertEquals("info:fedora/xyz", dataset.getContext().get(
                Symbol.create("uri")));
    }

    @Test
    public void testGetPropertiesDefaultLimits()
            throws RepositoryException {

        mockStatic(JcrRdfTools.class);
        final Resource mockResource = new DummyURIResource("info:fedora/xyz");
        when(JcrRdfTools.getGraphSubject(mockSubjects, mockNode)).thenReturn(mockResource);

        final Model propertiesModel = createDefaultModel();
        when(getJcrPropertiesModel(mockSubjects, mockNode)).thenReturn(
                propertiesModel);
        final Model treeModel = createDefaultModel();
        when(getJcrTreeModel(mockSubjects, mockNode, 0, -1)).thenReturn(
                treeModel);
        final Model problemsModel = createDefaultModel();
        when(getProblemsModel()).thenReturn(problemsModel);
        final Dataset dataset =
            testObj.getProperties(mockNode, mockSubjects, testDataset);

        verifyStatic();
        getJcrTreeModel(mockSubjects, mockNode, 0, -1);
        assertTrue(dataset.containsNamedModel(testObj.getPropertyModelName()));
        assertEquals(treeModel, dataset.getNamedModel(testObj
                .getPropertyModelName()));

        assertEquals(propertiesModel, dataset.getDefaultModel());
        assertEquals("info:fedora/xyz", dataset.getContext().get(
                Symbol.create("uri")));

        assertTrue(dataset.containsNamedModel("problems"));
    }

    @Test
    public void testGetPropertiesDatasetDefaults() throws RepositoryException {

        mockStatic(JcrRdfTools.class);

        final Resource mockResource = new DummyURIResource("info:fedora/xyz");
        when(getGraphSubject(mockSubjects, mockNode)).thenReturn(
                mockResource);

        final Model propertiesModel = createDefaultModel();
        when(getJcrPropertiesModel(mockSubjects, mockNode))
                .thenReturn(propertiesModel);
        final Model treeModel = createDefaultModel();
        when(getJcrTreeModel(mockSubjects, mockNode, 0, -1))
                .thenReturn(treeModel);
        final Model problemsModel = createDefaultModel();
        when(getProblemsModel()).thenReturn(
                problemsModel);
        final Dataset dataset =
            testObj.getProperties(mockNode, mockSubjects, testDataset);

        assertTrue(dataset.containsNamedModel(testObj.getPropertyModelName()));
        assertEquals(treeModel, dataset.getNamedModel(testObj
                .getPropertyModelName()));

        assertEquals(propertiesModel, dataset.getDefaultModel());
        assertEquals("info:fedora/xyz", dataset.getContext().get(URI_SYMBOL));
    }
}