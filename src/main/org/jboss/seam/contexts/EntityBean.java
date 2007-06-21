package org.jboss.seam.contexts;

import static org.jboss.seam.contexts.PassivatedEntity.passivateEntity;

/**
 * Swizzles entities held in the conversation context at
 * the end of each request.
 * 
 * @see PassivatedEntity
 * 
 * @author Gavin King
 *
 */
class EntityBean implements Wrapper
{
   private static final long serialVersionUID = -2884601453783925804L;
   
   private Object instance;
   private PassivatedEntity passivatedEntity;
   
   public EntityBean(Object instance)
   {
      this.instance = instance;
   }
   
   //TODO: use @Unwrap
   public Object getInstance()
   {
      if ( passivatedEntity!=null && !passivatedEntity.isVersioned() ) 
      {
         //non-versioned entities can be lazily unpassivated 
         instance = passivatedEntity.toEntityReference(true);
      }
      return instance;
   }
   
   public boolean passivate()
   {
      if ( passivatedEntity==null || passivatedEntity.isVersioned() ) //and the version number changed!
      {
         if ( PassivatedEntity.isTransactionRolledBackOrMarkedRollback() )
         {
            passivatedEntity = null;
         }
         else
         {
            passivatedEntity = passivateEntity(instance);
            if (passivatedEntity!=null) instance = null;
         }
         return true;
      }
      else
      {
         return false;
      }
   }
   
   public void activate()
   {
      //versioned entities must be unpassivated at the beginning 
      //of the request 
      if ( passivatedEntity!=null && passivatedEntity.isVersioned() )
      {
         instance = passivatedEntity.toEntityReference(true);
      }
   }
   
}
