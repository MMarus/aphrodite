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
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.google.common.collect.Sets;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/28/17.
 */
public class CustomIssueJsonParser extends IssueJsonParser {
    private final JiraChangelogJsonParser changelogJsonParser = new JiraChangelogJsonParser();

    private static Set<String> SPECIAL_FIELDS = Sets.newHashSet(IssueFieldId.ids());

    private final JSONObject providedNames;
    private final JSONObject providedSchema;

    public CustomIssueJsonParser() {
        super();
        this.providedNames = null;
        this.providedSchema = null;
    }

    public CustomIssueJsonParser(JSONObject providedNames, JSONObject providedSchema) {
        super(providedNames, providedSchema);
        this.providedNames = providedNames;
        this.providedSchema = providedSchema;
    }

    public Issue parse(JSONObject issueJson) throws JSONException {
        List<JiraChangelogGroup> jiraChangelog = parseOptionalArray(issueJson,
                changelogJsonParser, new String[] { "changelog", "histories" });

        // Remove changelog from the json to avoid it's double parsing
        Issue issue = super.parse(issueJson);

        CustomIssue customIssue = new CustomIssue(issue, jiraChangelog);

        return customIssue;
    }

    @Nullable
    private List<JiraChangelogGroup> parseOptionalArray(JSONObject json, JiraChangelogJsonParser changelogJsonParser,
            String[] path) throws JSONException {
        JSONArray jsonArray = JsonParseUtil.getNestedOptionalArray(json, path);
        if (jsonArray == null) {
            return null;
        } else {
            List<JiraChangelogGroup> res = new ArrayList(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); ++i) {
                res.add(changelogJsonParser.parse(jsonArray.getJSONObject(i)));
            }

            return res;
        }
    }
}
