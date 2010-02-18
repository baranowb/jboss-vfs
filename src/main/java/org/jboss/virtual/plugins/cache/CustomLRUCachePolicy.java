/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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
package org.jboss.virtual.plugins.cache;

import org.jboss.logging.Logger;
import org.jboss.util.LRUCachePolicy;
import org.jboss.virtual.spi.VFSContext;

/**
 * LRU cache policy vfs cache.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
class CustomLRUCachePolicy extends LRUCachePolicy
{
   private static Logger log = Logger.getLogger(CustomLRUCachePolicy.class);

   CustomLRUCachePolicy()
   {
   }

   CustomLRUCachePolicy(int min, int max)
   {
      super(min, max);
   }

   @Override
   protected void ageOut(LRUCacheEntry entry)
   {
      try
      {
         VFSContext context = (VFSContext) entry.m_object;
         context.cleanupTempInfo(""); // cleanup from root
      }
      catch (Exception e)
      {
         log.debug("Error cleaning up: " + e);
      }
      finally
      {
         super.ageOut(entry); // remove entry         
      }
   }

   @Override
   protected LRUList createList()
   {
      return new CustomLRUList();
   }

   private class CustomLRUList extends LRUList
   {
      @Override
      protected void entryRemoved(LRUCachePolicy.LRUCacheEntry entry)
      {
         ageOut(entry);
      }
   }
}