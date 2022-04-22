
package acme.features.any.item;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.items.Item;

import acme.entities.toolkits.Toolkit;

import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.roles.Any;
import acme.framework.services.AbstractShowService;

@Service
public class AnyItemShowService implements AbstractShowService<Any, Item> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AnyItemRepository repository;

	// AbstractShowService<Any, Toolkit> ---------------------------
	
	@Override
	public boolean authorise(final Request<Item> request) {
		assert request != null;
    String s;
		s = request.getServletRequest().getRequestURI();
		int id;
    id = request.getModel().getInteger("id");
  
		if(s.contains("/any/item/show")) {
			
      Collection<Item> items;
      Item item;
			
			items = this.repository.findItemsPublished();
			
			item = this.repository.findOneItemById(id);
			
			return items.contains(item)?true:false;
			
		} else {
			
      Collection<Toolkit> toolkits;

      
      toolkits = this.repository.findManyPublishedToolkitsByItemId(id);
      return (!toolkits.isEmpty());

			
			
		}
		
	}

	@Override
	public Item findOne(final Request<Item> request) {
		assert request != null;
		
		Item result=null;
		int id;
		
		id = request.getModel().getInteger("id");
		result = this.repository.findOneItemById(id);
		
		return result;
	}

	@Override
	public void unbind(final Request<Item> request, final Item entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		request.unbind(entity, model, "type", "name","code",
			"technology","description","retailPrice","link");
		
		// Inventor full name

		String inventor;
		inventor = entity.getInventor().getIdentity().getFullName();

		model.setAttribute("inventor", inventor);
		
	}


	


}
