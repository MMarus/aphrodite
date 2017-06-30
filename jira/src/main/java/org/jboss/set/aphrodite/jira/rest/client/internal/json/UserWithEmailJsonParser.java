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

import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.google.common.base.Preconditions;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.set.aphrodite.domain.User;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/28/17.
 */
public class UserWithEmailJsonParser implements JsonObjectParser<User> {
    public UserWithEmailJsonParser() {
    }

    public User parse(JSONObject json) throws JSONException {
        BasicUser basicUser = (BasicUser) Preconditions.checkNotNull(JsonParseUtil.parseBasicUser(json));
        String emailAddress = JsonParseUtil.getOptionalString(json, "emailAddress");
        return new User(emailAddress, basicUser.getDisplayName());
    }
}
