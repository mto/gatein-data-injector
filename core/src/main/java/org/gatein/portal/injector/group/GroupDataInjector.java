package org.gatein.portal.injector.group;

import org.exoplatform.management.annotations.Impact;
import org.exoplatform.management.annotations.ImpactType;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.management.rest.annotations.RESTEndpoint;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.portal.injector.AbstractInjector;

@Managed
@ManagedDescription("Group data injector")
@NameTemplate({@Property(key = "view", value = "portal")
            ,@Property(key = "service", value = "groupInjector")
            ,@Property(key = "type", value = "groupDataInject")})
@RESTEndpoint(path = "groupInjector")
public class GroupDataInjector extends AbstractInjector
{
   private static Logger LOG = LoggerFactory.getLogger(GroupDataInjector.class);

   private OrganizationService orgService;

   private GroupHandler groupHandler;

   public GroupDataInjector(OrganizationService orgService)
   {
      this.orgService = orgService;
      this.groupHandler = orgService.getGroupHandler();
   }

   @Override
   public Logger getLogger()
   {
      return LOG;
   }

   public void createGrop(String parentName, String groupName, String label, String description) throws Exception
   {
      Group group = groupHandler.createGroupInstance();
      group.setGroupName(groupName);
      group.setLabel(label);
      group.setDescription(description);

      if (parentName == null)
      {
         groupHandler.addChild(null, group, true);
      }
      else
      {
         Group parentGroup = groupHandler.findGroupById("/" + parentName);
         groupHandler.addChild(parentGroup, group, true);
      }
   }

   @Managed
   @ManagedDescription("Create amount of new groups")
   @Impact(ImpactType.WRITE)
   public void createGroups(@ManagedDescription("Group name") @ManagedName("groupName") String groupName
      ,@ManagedDescription("Parent group name") @ManagedName("parentName") String parentName
      ,@ManagedDescription("Amount of groups need to create") @ManagedName("amount") int amount
      ,@ManagedDescription("Creating parent if it doesn't exist") @ManagedName("createParent") boolean createParent)
   {
      startTransaction();
      try
      {
         if(!validateGroupInfo(groupName, parentName, amount))
         {
            throw new Exception();
         }

         if (parentName == null || parentName.trim().length() == 0)
         {
            parentName = null;
         }

         if (parentName != null)
         {
            parentName = parentName.trim();
            Group parentGrop = orgService.getGroupHandler().findGroupById("/" + parentName);
            if (parentGrop == null)
            {
               if (createParent)
               {
                  createGrop(null, parentName, parentName, parentName);
               }
               else
               {
                  LOG.error("Parent Group doesn't exist");
                  throw new Exception();
               }
            }
         }

         groupName = groupName.trim();
         for (int i = 0; i < amount; i++)
         {
            createGrop(parentName, groupName + "_" + i, groupName + "_" + i, groupName + "_" + i);
         }
      }
      catch (Exception e)
      {
      }
      finally
      {
         endTransaction();
      }
   }
   
   @Managed
   @ManagedDescription("Remove amount of groups")
   @Impact(ImpactType.WRITE)
   public void removeGroups(@ManagedDescription("Group name") @ManagedName("groupName") String groupName
      ,@ManagedDescription("Parent group name") @ManagedName("parentName") String parentName
      ,@ManagedDescription("Amount of groups need to create") @ManagedName("amount") int amount
      ,@ManagedDescription("Alow to delete parent") @ManagedName("deleteParent") boolean deleteParent) 
   {
      startTransaction();
      try
      {
         if (!validateGroupInfo(groupName, parentName, amount))
         {
            throw new Exception();
         }

         if (parentName == null || parentName.trim().length() == 0)
         {
            parentName = null;
         }

         if (parentName != null)
         {
            groupName = "/" + parentName + "/" + groupName;
         }

         for (int i = 0; i < amount; i++)
         {
            Group group = groupHandler.findGroupById(groupName + "_" + i);
            if (group != null)
               groupHandler.removeGroup(group, true);
         }
         
         if(parentName != null && deleteParent)
         {
            Group group = groupHandler.findGroupById("/" + parentName);
            if (group != null)
               groupHandler.removeGroup(group, true);
         }
      }
      catch (Exception e)
      {
      }
      finally
      {
         endTransaction();
      }
   }
   
   private boolean validateGroupInfo(String groupName, String parentName, int amount)
   {
      if(groupName == null || groupName.trim().length() == 0)
      {
         LOG.error("groupName cannot be null or empty");
         return false;
      }
      
      if (amount < 1)
      {
         LOG.error("amount must be >=  0");
         return false;
      }
      
      if (groupName.contains("/"))
      {
         LOG.error("Group name contains only alpha, digit, dash and underscore characters");
         return false;
      }
      
      if(parentName != null && parentName.trim().length()  > 0)
      {
         if(parentName.contains("/"))
         {
            LOG.error("Parent name contains only alpha, digit, dash and underscore characters");
            return false;
         }
      }
      return true;
   }
}
