package acme.features.inventor.toolkit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.configuration.SystemConfiguration;
import acme.entities.toolkits.Toolkit;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractCreateService;
import acme.helpers.SpamHelper;
import acme.roles.Inventor;

@Service
public class InventorToolkitCreateService implements AbstractCreateService<Inventor, Toolkit>{

	// Internal state ---------------------------------------------------------

	@Autowired
	protected InventorToolkitRepository repository;
	
	@Autowired
	protected SpamHelper helper; 
	
	// AbstractCreateService<Inventor, Toolkit> ---------------------------

	@Override
	public boolean authorise(final Request<Toolkit> request) {
		assert request != null;

		return true;
	}
	
	@Override
	public Toolkit instantiate(final Request<Toolkit> request) {
		assert request != null;

		Toolkit result;
		Inventor inventor;

		inventor = this.repository.findOneInventorById(request.getPrincipal().getActiveRoleId());
		result = new Toolkit();
		result.setDraftMode(true);
		result.setInventor(inventor);

		return result;
	}

	@Override
	public void bind(final Request<Toolkit> request, final Toolkit entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors, "code", "title","description","assemblyNotes","link");
		
	}

	@Override
	public void validate(final Request<Toolkit> request, final Toolkit entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		if(!errors.hasErrors("code")) {
			
			Toolkit existing;
			
			existing = this.repository.findOneToolkitByCode(entity.getCode());
			
			errors.state(request, existing == null || existing.getId() == entity.getId(), "code", "inventor.toolkit.form.error.duplicated-code");
		}
		
		if(!errors.hasErrors("title")) {
			final SystemConfiguration sc = this.repository.findSystemConfiguration();
			final boolean spamFree = this.helper.spamChecker(entity.getTitle(),sc.getStrongSpamWords(),sc.getWeakSpamWords(),sc.getStrongSpamThreshold(),sc.getWeakSpamThreshold());
			errors.state(request, spamFree, "title", "form.error.spam");
		}
		
		if(!errors.hasErrors("description")) {
			final SystemConfiguration sc = this.repository.findSystemConfiguration();
			final boolean spamFree = this.helper.spamChecker(entity.getDescription(),sc.getStrongSpamWords(),sc.getWeakSpamWords(),sc.getStrongSpamThreshold(),sc.getWeakSpamThreshold());
			errors.state(request, spamFree, "description", "form.error.spam");
		}
		
		if(!errors.hasErrors("assemblyNotes")) {
			final SystemConfiguration sc = this.repository.findSystemConfiguration();
			final boolean spamFree = this.helper.spamChecker(entity.getAssemblyNotes(),sc.getStrongSpamWords(),sc.getWeakSpamWords(),sc.getStrongSpamThreshold(),sc.getWeakSpamThreshold());
			errors.state(request, spamFree, "assemblyNotes", "form.error.spam");
		}
		
		
		
	}
	
	@Override
	public void unbind(final Request<Toolkit> request, final Toolkit entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "code", "title","description","assemblyNotes","link", "draftMode");
		
	}

	@Override
	public void create(final Request<Toolkit> request, final Toolkit entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
		
	}
	
}
