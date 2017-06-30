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

import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/28/17.
 */
public class CustomIssue extends Issue {
    private List<JiraChangelogGroup> customChangelog = new ArrayList<>();

    //TODO: Can remove the casts to Collection after this is merged https://bitbucket.org/atlassian/jira-rest-java-client/pull-requests/14/use-more-precise-return-types-to-enable/diff
    public CustomIssue(Issue issue, List<JiraChangelogGroup> customChangelog) {
        super(issue.getSummary(), issue.getSelf(), issue.getKey(), issue.getId(), issue.getProject(), issue.getIssueType(),
                issue.getStatus(), issue.getDescription(), issue.getPriority(), issue.getResolution(),
                (Collection<Attachment>) issue.getAttachments(), issue.getReporter(), issue.getAssignee(),
                issue.getCreationDate(), issue.getUpdateDate(), issue.getDueDate(),
                (Collection<Version>) issue.getAffectedVersions(), (Collection<Version>) issue.getFixVersions(),
                (Collection<BasicComponent>) issue.getComponents(), issue.getTimeTracking(),
                (Collection<IssueField>) issue.getFields(), (Collection<Comment>) issue.getComments(),
                issue.getTransitionsUri(), (Collection<IssueLink>) issue.getIssueLinks(), issue.getVotes(),
                (Collection<Worklog>) issue.getWorklogs(), issue.getWatchers(), issue.getExpandos(),
                (Collection<Subtask>) issue.getSubtasks(), (Collection<ChangelogGroup>) issue.getChangelog(),
                issue.getOperations(), issue.getLabels());

        this.customChangelog = customChangelog;
    }

    public List<JiraChangelogGroup> getCustomChangelog() {
        return customChangelog;
    }

}
