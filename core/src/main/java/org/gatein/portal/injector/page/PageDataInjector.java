/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.gatein.portal.injector.page;

import org.exoplatform.management.annotations.Impact;
import org.exoplatform.management.annotations.ImpactType;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.management.rest.annotations.RESTEndpoint;
import org.exoplatform.portal.mop.SiteKey;
import org.exoplatform.portal.mop.SiteType;
import org.exoplatform.portal.mop.page.PageContext;
import org.exoplatform.portal.mop.page.PageService;
import org.exoplatform.portal.mop.page.PageState;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.portal.injector.AbstractInjector;
import java.util.Collections;

/**
 * @author <a href="mailto:haithanh0809@gmail.com">Hai Thanh Nguyen</a>
 * @version $Id$
 *
 */

@Managed
@ManagedDescription("Page data injector")
@NameTemplate({@Property(key = "view", value = "portal"), @Property(key = "service", value = "pageInjector"),
   @Property(key = "type", value = "pageInject")})
@RESTEndpoint(path = "pageInjector")
public class PageDataInjector extends AbstractInjector
{
   private static Logger LOG = LoggerFactory.getLogger(PageDataInjector.class);

   private PageService pageService;

   public PageDataInjector(PageService _pageService)
   {
      pageService = _pageService;
   }

   @Override
   public Logger getLogger()
   {
      return LOG;
   }

   @Managed
   @ManagedDescription("Create new pages")
   @Impact(ImpactType.WRITE)
   public void createPages(@ManagedName("siteType") String type, @ManagedName("siteName") String name,
                           @ManagedName("pageNamePrefix") String pageNamePrefix, @ManagedName("pageTitlePrefix") String pageTitlePrefix,
                           @ManagedDescription("Starting index") @ManagedName("startIndex") int startIndex,
                           @ManagedDescription("Ending index") @ManagedName("endIndex") int endIndex)
   {
      try
      {
         startTransaction();
         SiteType siteType = SiteType.valueOf(type.toUpperCase());
         SiteKey siteKey = siteType.key(name);
         for (int i = startIndex; i < endIndex; i++)
         {
            PageContext page = new PageContext(siteKey.page(pageNamePrefix + "_" + i), new PageState(pageTitlePrefix + "_"
               + i, null, true, null, Collections.<String>emptyList(), null));
            pageService.savePage(page);
         }
      }
      catch (Exception e)
      {
         LOG.error("Failed to create new page", e);
      }
      finally
      {
         endTransaction();
      }
   }
}
