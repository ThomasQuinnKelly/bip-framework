package gov.va.bip.framework.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractPersonTraitsObject extends User {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "The person's birth date", example = "1978-05-20")
	private String birthDate;
	@ApiModelProperty(value = "The person's first name", example = "JANE")
	private String firstName;
	@ApiModelProperty(value = "The person's last name", example = "DOE")
	private String lastName;
	@ApiModelProperty(value = "The person's middle name", example = "M")
	private String middleName;
	@ApiModelProperty(value = "The prefix for the person's full name", example = "Ms")
	private String prefix;
	@ApiModelProperty(value = "The suffix for the person's full name", example = "S")
	private String suffix;
	@ApiModelProperty(value = "The person's gender", example = "FEMALE")
	private String gender;
	@ApiModelProperty(value = "The person's access assurance level", example = "2")
	private Integer assuranceLevel;
	@ApiModelProperty(value = "The person's email address", example = "jane.doe@va.gov")
	private String email;
	@ApiModelProperty(value = "The MVI correlation IDs list for the person",
			example = "[\n" +
					"\"77779102^NI^200M^USVHA^P\",\n"
					+ "\"912444689^PI^200BRLS^USVBA^A\",\n"
					+ "\"6666345^PI^200CORP^USVBA^A\",\n"
					+ "\"1105051936^NI^200DOD^USDOD^A\",\n"
					+ "\"912444689^SS\"\n"
					+ "]")
	private List<String> correlationIds;
	@ApiModelProperty(value = "The application's custom token passed as any String from consumers to the API services", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9")
	private String appToken;
	@ApiModelProperty(value = "The application taking the last action on the record", example = "ShareUI")
	private String applicationID;
	@ApiModelProperty(value = "The number representing the Regional Office of the person taking the last action on the record", example = "310")
	private String stationID;
	@ApiModelProperty(value = "The name associated with the person taking the last action on the record", example = "vhaislXXXXX")
	private String userID;

	public AbstractPersonTraitsObject(final String username, final String password,
			final Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public AbstractPersonTraitsObject(final String username, final String password, final boolean enabled, final boolean accountNonExpired,
			final boolean credentialsNonExpired, final boolean accountNonLocked,
			final Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	@Override
	@JsonIgnore
	public List<GrantedAuthority> getAuthorities() {
		return new ArrayList<>(super.getAuthorities());
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return super.getPassword();
	}

	@Override
	@JsonIgnore
	public String getUsername() {
		return super.getUsername();
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return super.isEnabled();
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return super.isAccountNonExpired();
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return super.isAccountNonLocked();
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return super.isCredentialsNonExpired();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(final String birthDate) {
		this.birthDate = birthDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public Integer getAssuranceLevel() {
		return assuranceLevel;
	}

	public void setAssuranceLevel(final Integer assuranceLevel) {
		this.assuranceLevel = assuranceLevel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public List<String> getCorrelationIds() {
		return correlationIds;
	}

	public void setCorrelationIds(final List<String> correlationIds) {
		this.correlationIds = correlationIds;
	}

	public String getUser() {
		return getFirstName() + " " + getLastName();
	}
	
	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(final String appToken) {
		this.appToken = appToken;
	}
	
	public String getApplicationID() {
		return applicationID;
	}

	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public boolean hasFirstName() {
		return !StringUtils.isEmpty(getFirstName());
	}

	public boolean hasLastName() {
		return !StringUtils.isEmpty(getLastName());
	}

	public boolean hasMiddleName() {
		return !StringUtils.isEmpty(getMiddleName());
	}

	public boolean hasPrefix() {
		return !StringUtils.isEmpty(getPrefix());
	}

	public boolean hasBirthDate() {
		return !StringUtils.isEmpty(getBirthDate());
	}

	public boolean hasGender() {
		return !StringUtils.isEmpty(getGender());
	}

	public boolean hasEmail() {
		return !StringUtils.isEmpty(getEmail());
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, getToStringEqualsHashExcludeFields());
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, getToStringEqualsHashExcludeFields());
	}

	@Override
	public String toString() {
		final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this);
		reflectionToStringBuilder.setExcludeFieldNames(getToStringEqualsHashExcludeFields());
		return reflectionToStringBuilder.toString();
	}

	protected String[] getToStringEqualsHashExcludeFields() {
		return new String[] {};
	}
}