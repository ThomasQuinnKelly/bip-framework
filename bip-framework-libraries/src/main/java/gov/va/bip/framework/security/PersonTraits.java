package gov.va.bip.framework.security;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import gov.va.bip.framework.security.model.AbstractPersonTraitsObject;

/**
 * VA person identifier data. Created by vgadda on 6/6/17.
 */

public class PersonTraits extends AbstractPersonTraitsObject {

	public enum PATTERN_FORMAT {
		BIRTHDATE_YYYYMMDD("YYYY-MM-dd");
		private String pattern;

		private PATTERN_FORMAT(final String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}

		public static PATTERN_FORMAT getDefault() {
			return BIRTHDATE_YYYYMMDD;
		}
	}

	// Same as the DOD EDIPI (Can be substituted for edipi in Veteran Identifier)
	private String dodedipnid;
	private String pnidType;
	private String pnid;
	private String pid;
	private String icn;
	private String fileNumber;
	private String tokenId;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PersonTraits(final String username, final String password, final Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public PersonTraits() {
		super("NA", "NA", AuthorityUtils.NO_AUTHORITIES);
	}

	public String getDodedipnid() {
		return dodedipnid;
	}

	public void setDodedipnid(final String dodedipnid) {
		this.dodedipnid = dodedipnid;
	}

	public String getPnidType() {
		return pnidType;
	}

	public void setPnidType(final String pnidType) {
		this.pnidType = pnidType;
	}

	public String getPnid() {
		return pnid;
	}

	public void setPnid(final String pnid) {
		this.pnid = pnid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(final String pid) {
		this.pid = pid;
	}

	public String getIcn() {
		return icn;
	}

	public void setIcn(final String icn) {
		this.icn = icn;
	}

	public String getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(final String fileNumber) {
		this.fileNumber = fileNumber;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(final String tokenId) {
		this.tokenId = tokenId;
	}

	public boolean hasDodedipnid() {
		return !StringUtils.isEmpty(dodedipnid);
	}

	public boolean hasPnidType() {
		return !StringUtils.isEmpty(pnidType);
	}

	public boolean hasPnid() {
		return !StringUtils.isEmpty(pnid);
	}

	public boolean hasPid() {
		return !StringUtils.isEmpty(pid);
	}

	public boolean hasIcn() {
		return !StringUtils.isEmpty(icn);
	}

	public boolean hasFileNumber() {
		return !StringUtils.isEmpty(fileNumber);
	}

	public boolean hasTokenId() {
		return !StringUtils.isEmpty(tokenId);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, getToStringEqualsHashExcludeFields());
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, getToStringEqualsHashExcludeFields());
	}
}