/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.set.aphrodite.jira.rest.client.internal.async;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousMetadataRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSessionRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/28/17.
 */
public class AsyncJiraRestClient extends AsynchronousJiraRestClient {
    private final IssueRestClient issueRestClient;
    private final SearchRestClient searchRestClient;

    public AsyncJiraRestClient(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        URI baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build(new Object[0]);
        MetadataRestClient metadataRestClient = new AsynchronousMetadataRestClient(baseUri, httpClient);
        SessionRestClient sessionRestClient = new AsynchronousSessionRestClient(serverUri, httpClient);

        //Custom Implementations
        this.issueRestClient = new AsyncIssueRestClient(baseUri, httpClient, sessionRestClient, metadataRestClient);
        this.searchRestClient = new AsyncSearchRestClient(baseUri, httpClient);
    }

    public IssueRestClient getIssueClient() {
        return this.issueRestClient;
    }

    public SearchRestClient getSearchClient() {
        return this.searchRestClient;
    }

}
