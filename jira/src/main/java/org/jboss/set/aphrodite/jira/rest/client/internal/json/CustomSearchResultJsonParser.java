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

package org.jboss.set.aphrodite.jira.rest.client.internal.json;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.SearchResultJsonParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Collections;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/29/17.
 */
public class CustomSearchResultJsonParser extends SearchResultJsonParser {

    public CustomSearchResultJsonParser() {
    }

    public SearchResult parse(JSONObject json) throws JSONException {
        int startAt = json.getInt("startAt");
        int maxResults = json.getInt("maxResults");
        int total = json.getInt("total");
        JSONArray issuesJsonArray = json.getJSONArray("issues");
        Object issues;
        if(issuesJsonArray.length() > 0) {
            IssueJsonParser issueParser = new CustomIssueJsonParser(json.getJSONObject("names"), json.getJSONObject("schema"));
            GenericJsonArrayParser<Issue> issuesParser = GenericJsonArrayParser.create(issueParser);
            issues = issuesParser.parse(issuesJsonArray);
        } else {
            issues = Collections.emptyList();
        }

        return new SearchResult(startAt, maxResults, total, (Iterable)issues);
    }

}
