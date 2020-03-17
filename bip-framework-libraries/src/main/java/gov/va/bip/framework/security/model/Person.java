package gov.va.bip.framework.security.model;

import gov.va.bip.framework.transfer.DomainTransferObjectMarker;
import io.swagger.annotations.ApiModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

@ApiModel(description = "Model that identifies a single individual used in the security context")
public class Person extends AbstractPersonTraitsObject implements DomainTransferObjectMarker {
	private static final long serialVersionUID = -1266043145253422198L;

	public Person(final String username, final String password, final Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public Person() {
		super("NA", "NA", AuthorityUtils.NO_AUTHORITIES);
	}

}