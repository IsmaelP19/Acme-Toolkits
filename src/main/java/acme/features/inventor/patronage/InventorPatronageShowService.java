package acme.features.inventor.patronage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.patronages.Patronage;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractShowService;
import acme.roles.Inventor;
import acme.roles.Patron;

@Service
public class InventorPatronageShowService implements AbstractShowService<Inventor, Patronage>{

	@Autowired
	protected InventorPatronageRepository repository;

	@Override
	public boolean authorise(final Request<Patronage> request) {
		assert request != null;
		boolean result;
		int masterId;
		Patronage patronage;
		Inventor inventor;
		Principal principal;
		
		masterId = request.getModel().getInteger("id");
		patronage = this.repository.findOnePatronageById(masterId);
		inventor = patronage.getInventor();
		principal = request.getPrincipal();
		result = (
			inventor.getUserAccount().getId()==principal.getAccountId());
			
		return result;
	}

	@Override
	public Patronage findOne(final Request<Patronage> request) {
		assert request != null;
		
		Patronage result;
		int id;
		
		id=request.getModel().getInteger("id");
		result = this.repository.findOnePatronageById(id);
		return result;
	}

	@Override
	public void unbind(final Request<Patronage> request, final Patronage entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		request.unbind(entity, model, "code", "legalStuff", "budget", "creationDate", "initialPeriodDate", "finalPeriodDate", "link");
		
		//Patron details:
		final Patronage patronage = this.repository.findOnePatronageById(entity.getId());
		final Patron patron = patronage.getPatron();
		final String company = patron.getCompany();
		final String statement = patron.getStatement();
		final String patronLink = patron.getLink();
		
		model.setAttribute("company", company);
		model.setAttribute("statement", statement);
		model.setAttribute("patronLink", patronLink);
		
	}
	
	
}
