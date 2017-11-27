/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.set.aphrodite.cdicontainer;

import org.jboss.set.aphrodite.container.Container;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.NameNotFoundException;
import java.util.Set;

public class CDIContainer extends Container {
    @Override
    public <T> T lookup(String name, Class<T> expected) throws NameNotFoundException {
        final BeanManager manager = CDI.current().getBeanManager();
        final Set<Bean<?>> beans = manager.getBeans(name);
        final Bean<?> bean = manager.resolve(beans);
        if (bean == null) throw new NameNotFoundException("Can't find bean " + name);
        final CreationalContext<?> ctx = manager.createCreationalContext(bean);
        return expected.cast(manager.getReference(bean, expected, ctx));
    }
}
