package org.gatein.portal.injector.user;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.management.annotations.Impact;
import org.exoplatform.management.annotations.ImpactType;
import org.exoplatform.management.annotations.Managed;
import org.exoplatform.management.annotations.ManagedDescription;
import org.exoplatform.management.annotations.ManagedName;
import org.exoplatform.management.jmx.annotations.NameTemplate;
import org.exoplatform.management.jmx.annotations.Property;
import org.exoplatform.management.rest.annotations.RESTEndpoint;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
//import org.gatein.common.logging.Logger;
//import org.gatein.common.logging.LoggerFactory;
import org.picocontainer.Startable;

@Managed
@ManagedDescription("User data injector")
@NameTemplate({@Property(key = "view", value = "portal")
               , @Property(key = "service", value = "userInjector")
               , @Property(key = "type", value = "userDataInject")
            })
@RESTEndpoint(path = "userInjector")
public class UserDataInjector implements Startable
{
   //   private static Logger LOG = LoggerFactory.getLogger(UserDataInjector.class);

   private OrganizationService orgService;

   public UserDataInjector(OrganizationService orgService)
   {
      this.orgService = orgService;
   }

   @Override
   public void start()
   {
   }

   @Override
   public void stop()
   {
   }

   public void createUser(String userName, String password, String email, String firstName, String lastName)
   {
      try
      {
         boolean newUser = false;
         User user = orgService.getUserHandler().findUserByName(userName);
         if (user == null)
         {
            user = orgService.getUserHandler().createUserInstance(userName);
            newUser = true;
         }
         user.setPassword(password);
         user.setEmail(email);
         user.setFirstName(firstName);
         user.setLastName(lastName);

         if (newUser)
         {
            orgService.getUserHandler().createUser(user, true);
            return;

         }
         orgService.getUserHandler().saveUser(user, true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Managed
   @ManagedDescription("Create amount of new users")
   @Impact(ImpactType.WRITE)
   public void createListUsers(@ManagedDescription("user name") @ManagedName("userName") String userName,
      @ManagedDescription("amount of user need to create") @ManagedName("amount") int amount,
      @ManagedDescription("if not specific it will be default 123456") @ManagedName("password") String password,
      @ManagedDescription("if amount > 1 email will be automatically asigned") @ManagedName("email") String email,
      @ManagedDescription("first name") @ManagedName("firstName") String firstName,
      @ManagedDescription("last name") @ManagedName("lastName") String lastName)
   {
      PortalContainer pc = RootContainer.getInstance().getPortalContainer("portal");
      RequestLifeCycle.begin(pc);

      if (amount > 1)
      {
         for (int i = 0; i < amount; i++)
         {
            String userNameTemp = userName + "_" + i;
            if (password == null || password.trim().length() == 0)
            {
               password = new String("123456");
            }
            email = new String(userNameTemp + "@localhost");
            if (firstName == null || firstName.trim().length() == 0)
            {
               firstName = userNameTemp;
            }
            if (lastName == null || lastName.trim().length() == 0)
            {
               lastName = userNameTemp;
            }
            createUser(userNameTemp, password, email, firstName.trim(), lastName.trim());
         }
      }
      else
      {
         if (!email.contains("@"))
         {
            email = email + "@localhost";
         }
         if (firstName == null || firstName.trim().length() == 0)
         {
            firstName = userName;
         }
         if (lastName == null || lastName.trim().length() == 0)
         {
            lastName = userName;
         }

         createUser(userName, password, email, firstName.trim(), lastName.trim());
      }

      RequestLifeCycle.end();
   }

   @Managed
   @ManagedDescription("remove amount of users")
   @Impact(ImpactType.WRITE)
   public void removeListUsers(@ManagedDescription("user name") @ManagedName("userName") String userName,
      @ManagedDescription("list of userName_i need to remove with i form 0 to amount") @ManagedName("amount") int amount)
   {
      PortalContainer pc = RootContainer.getInstance().getPortalContainer("portal");
      RequestLifeCycle.begin(pc);
      try
      {
         if (amount > 1)
         {
            for (int i = 0; i < amount; i++)
            {
               String userNameTemp = userName + "_" + i;
               orgService.getUserHandler().removeUser(userNameTemp, true);
            }
         }
         else
         {
            orgService.getUserHandler().removeUser(userName, true);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         RequestLifeCycle.end();
      }
   }   
}