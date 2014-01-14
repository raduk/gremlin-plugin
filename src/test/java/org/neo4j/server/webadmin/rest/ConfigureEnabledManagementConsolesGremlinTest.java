/**
 * Copyright (c) 2002-2014 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.server.webadmin.rest;

import org.dummy.web.service.DummyThirdPartyWebService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.server.NeoServer;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.neo4j.server.helpers.FunctionalTestHelper;
import org.neo4j.server.helpers.ServerHelper;
import org.neo4j.server.rest.JaxRsResponse;
import org.neo4j.server.rest.RestRequest;
import org.neo4j.server.rest.domain.GraphDbHelper;
import org.neo4j.test.server.ExclusiveServerTestBase;
import org.neo4j.test.server.SharedServerTestBase;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
//import static org.neo4j.server.helpers.ServerBuilder.server;

public class ConfigureEnabledManagementConsolesGremlinTest extends ExclusiveServerTestBase {
    private NeoServer server;


    @Before
    public void cleanTheDatabase() throws IOException {
        ServerHelper.cleanTheDatabase(server);
        server = ServerHelper.createNonPersistentServer();
    }

    @After
    public void stopServer()
    {
        if ( server != null )
        {
            server.stop();
        }
    }
    
    @Test
    public void shouldBeAbleToDisableGremlinConsole() throws Exception {

        
        assertThat(exec("g","gremlin").getStatus(), is(400));
        assertThat(exec("ls","shell").getStatus(),  is(200));
    }

    @Test
    public void shouldBeAbleToEnableGremlinConsole() throws Exception {
        server.stop();
        server = CommunityServerBuilder.server()
                .withProperty(Configurator.MANAGEMENT_CONSOLE_ENGINES, "shell,gremlin").build();
        server.start();


        assertThat(exec("g","gremlin").getStatus(), is(200));
        assertThat(exec("ls","shell").getStatus(),  is(200));
    }
    
    @Test
    public void shouldBeAbleToExplicitlySetConsolesToEnabled() throws Exception 
    {
        server.stop();
        server = CommunityServerBuilder.server().withProperty(Configurator.MANAGEMENT_CONSOLE_ENGINES, "").build();
        server.start();

        assertThat(exec("g","gremlin").getStatus(), is(400));
        assertThat(exec("ls","shell").getStatus(),  is(400));
    }
    

    
    @Test
    public void gremlinAndShellConsolesShouldNotBeEnabledByDefault() throws Exception {

        server.stop();
        server = CommunityServerBuilder.server().withProperty(Configurator.MANAGEMENT_CONSOLE_ENGINES, "shell").build();
        server.start();

        assertThat(exec("g","gremlin").getStatus(), is(400));
        assertThat(exec("ls","shell").getStatus(),  is(200));
    }

    private JaxRsResponse exec(String command, String engine)
    {
        return RestRequest.req().post(server.baseUri() + "db/manage/server/console", "{" +
                "\"engine\":\""+engine+"\"," +
                "\"command\":\""+command+"\\n\"}");
    }
}