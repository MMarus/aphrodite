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
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSearchRestClient;
import com.atlassian.jira.rest.client.internal.json.SearchResultJsonParser;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.set.aphrodite.jira.rest.client.internal.json.CustomSearchResultJsonParser;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Set;

import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.CHANGELOG;
import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.NAMES;
import static com.atlassian.jira.rest.client.api.IssueRestClient.Expandos.SCHEMA;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/28/17.
 */
public class AsyncSearchRestClient extends AsynchronousSearchRestClient {
    private final SearchResultJsonParser searchResultJsonParser = new CustomSearchResultJsonParser();

    private static final Function<IssueRestClient.Expandos, String> EXPANDO_TO_PARAM = new Function<IssueRestClient.Expandos, String>() {
        @Override
        public String apply(IssueRestClient.Expandos from) {
            return from.name().toLowerCase();
        }
    };

    private static final String START_AT_ATTRIBUTE = "startAt";
    private static final String MAX_RESULTS_ATTRIBUTE = "maxResults";
    private static final int MAX_JQL_LENGTH_FOR_HTTP_GET = 500;
    private static final String JQL_ATTRIBUTE = "jql";
    private static final String SEARCH_URI_PREFIX = "search";
    private static final String EXPAND_ATTRIBUTE = "expand";
    private static final String FIELDS_ATTRIBUTE = "fields";

    private final URI searchUri;

    public AsyncSearchRestClient(final URI baseUri, final HttpClient asyncHttpClient) {
        super(baseUri, asyncHttpClient);

        this.searchUri = UriBuilder.fromUri(baseUri).path(SEARCH_URI_PREFIX).build();
    }

    public Promise<SearchResult> searchJql(@Nullable String jql, @Nullable Integer maxResults, @Nullable Integer startAt, @Nullable Set<String> fields) {
        final Iterable<String> expandosValues = Iterables.transform(ImmutableList.of(SCHEMA, NAMES, CHANGELOG), EXPANDO_TO_PARAM);
        final String notNullJql = StringUtils.defaultString(jql);
        if (notNullJql.length() > MAX_JQL_LENGTH_FOR_HTTP_GET) {
            return searchJqlImplPost(maxResults, startAt, expandosValues, notNullJql, fields);
        } else {
            return searchJqlImplGet(maxResults, startAt, expandosValues, notNullJql, fields);
        }
    }

    private Promise<SearchResult> searchJqlImplGet(@Nullable Integer maxResults, @Nullable Integer startAt, Iterable<String> expandosValues, String jql, @Nullable Set<String> fields) {
        final UriBuilder uriBuilder = UriBuilder.fromUri(searchUri)
                .queryParam(JQL_ATTRIBUTE, jql)
                .queryParam(EXPAND_ATTRIBUTE, Joiner.on(",").join(expandosValues));

        if (fields != null) {
            uriBuilder.queryParam(FIELDS_ATTRIBUTE, Joiner.on(",").join(fields));
        }
        addOptionalQueryParam(uriBuilder, MAX_RESULTS_ATTRIBUTE, maxResults);
        addOptionalQueryParam(uriBuilder, START_AT_ATTRIBUTE, startAt);

        return getAndParse(uriBuilder.build(), searchResultJsonParser);
    }

    private Promise<SearchResult> searchJqlImplPost(@Nullable Integer maxResults, @Nullable Integer startAt, Iterable<String> expandosValues, String jql, @Nullable Set<String> fields) {
        final JSONObject postEntity = new JSONObject();

        try {
            postEntity.put(JQL_ATTRIBUTE, jql)
                    .put(EXPAND_ATTRIBUTE, ImmutableList.copyOf(expandosValues))
                    .putOpt(START_AT_ATTRIBUTE, startAt)
                    .putOpt(MAX_RESULTS_ATTRIBUTE, maxResults);

            if (fields != null) {
                postEntity.put(FIELDS_ATTRIBUTE, fields); // putOpt doesn't work with collections
            }
        } catch (JSONException e) {
            throw new RestClientException(e);
        }
        return postAndParse(searchUri, postEntity, searchResultJsonParser);
    }

    private void addOptionalQueryParam(final UriBuilder uriBuilder, final String key, final Object... values) {
        if (values != null && values.length > 0 && values[0] != null) {
            uriBuilder.queryParam(key, values);
        }
    }
}
