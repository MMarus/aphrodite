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

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.rest.client.api.SessionRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousIssueRestClient;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.jboss.set.aphrodite.jira.rest.client.internal.json.CustomIssueJsonParser;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.EnumSet;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/28/17.
 */
public class AsyncIssueRestClient extends AsynchronousIssueRestClient {
    private final IssueJsonParser issueParser = new CustomIssueJsonParser();
    private final URI baseUri;
    private static final EnumSet<Expandos> DEFAULT_EXPANDS;
    private static final Function<Expandos, String> EXPANDO_TO_PARAM;


    public AsyncIssueRestClient(URI baseUri, HttpClient client, SessionRestClient sessionRestClient, MetadataRestClient metadataRestClient) {
        super(baseUri, client, sessionRestClient, metadataRestClient);
        this.baseUri = baseUri;

    }

    public Promise<Issue> getIssue(String issueKey, Iterable<Expandos> expand) {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri);
        Iterable<Expandos> expands = Iterables.concat(DEFAULT_EXPANDS, expand);
        uriBuilder.path("issue").path(issueKey).queryParam("expand", new Object[]{Joiner.on(',').join(Iterables.transform(expands, EXPANDO_TO_PARAM))});
        return this.getAndParse(uriBuilder.build(new Object[0]), this.issueParser);
    }

    static {
        DEFAULT_EXPANDS = EnumSet.of(Expandos.NAMES, Expandos.SCHEMA, Expandos.TRANSITIONS);
        EXPANDO_TO_PARAM = new Function<Expandos, String>() {
            public String apply(Expandos from) {
                return from.name().toLowerCase();
            }
        };
    }
}
