package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Template;

/**
 * A Seam component that TBD.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Name("facesMessages")
@Intercept(NEVER)
public class FacesMessages 
{
   
   private List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();

   public void beforeRenderResponse() 
   {
      for (FacesMessage facesMessage: facesMessages)
      {
         FacesContext.getCurrentInstance().addMessage(null, facesMessage);
      }
      clear();
   }
   
   public void clear()
   {
      facesMessages.clear();
   }
   
   /**
    * Add a FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(FacesMessage facesMessage) 
   {
      facesMessages.add(facesMessage);
   }
   
   public static FacesMessages instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (FacesMessages) Component.getInstance(FacesMessages.class, ScopeType.CONVERSATION, true);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(String messageTemplate)
   {
      add(FacesMessage.SEVERITY_INFO, messageTemplate);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(Severity severity, String messageTemplate)
   {
      add( new FacesMessage( severity, Template.render(messageTemplate), null ) );
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. 
    */
   public void addFromResourceBundle(String key)
   {
      addFromResourceBundle(FacesMessage.SEVERITY_INFO, key);
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. 
    */
   public void addFromResourceBundle(Severity severity, String key)
   {
      addFromResourceBundle(severity, key, key);
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addFromResourceBundle(Severity severity, String key, String defaultMessageTemplate)
   {
      String messageTemplate = defaultMessageTemplate;
      java.util.ResourceBundle resourceBundle = ResourceBundle.instance();
      if (resourceBundle!=null) 
      {
         try
         {
            String bundleMessage = resourceBundle.getString(key);
            if (bundleMessage!=null) messageTemplate = bundleMessage;
         }
         catch (MissingResourceException mre) {} //swallow
      }
      add(severity, messageTemplate);
   }
  
}
