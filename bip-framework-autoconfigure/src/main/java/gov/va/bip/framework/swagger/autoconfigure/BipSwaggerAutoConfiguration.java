package gov.va.bip.framework.swagger.autoconfigure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.classmate.TypeResolver;

import gov.va.bip.framework.rest.provider.ProviderResponse;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by vgadda on 8/3/17.
 */

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
@ConditionalOnProperty(prefix = "bip.framework.swagger", name = "enabled", matchIfMissing = true)
@Import({ BeanValidatorPluginsConfiguration.class })
public class BipSwaggerAutoConfiguration {

	private static final String MESSAGE_200 = "A Response which indicates a successful Request.  The Response may contain \"messages\" that could describe warnings or further information.";
	private static final String MESSAGE_403 = "The request is not authorized.  Please verify credentials used in the request.";
	private static final String MESSAGE_400 = "There was an error encountered processing the Request.  Response will contain a  \"messages\" element that will provide further information on the error.  This request shouldn\'t be retried until corrected.";
	private static final String MESSAGE_500 = "There was an error encountered processing the Request.  Response will contain a  \"messages\" element that will provide further information on the error.  Please retry.  If problem persists, please contact support with a copy of the Response.";
	private static final String AUTHORIZATION = "Authorization";
	private static final String PROVIDER_RESPONSE = "ProviderResponse";

	@Autowired
	private SwaggerProperties swaggerProperties;

	@Autowired
	private TypeResolver typeResolver;

	@Bean
	public Docket categoryApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName(swaggerProperties.getGroupName())
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
				.build()
				.ignoredParameterTypes(ApiIgnore.class)
				.additionalModels(typeResolver.resolve(ProviderResponse.class))
				.globalResponseMessage(RequestMethod.GET, globalResponseMessages())
				.globalResponseMessage(RequestMethod.POST, globalResponseMessages())
				.globalResponseMessage(RequestMethod.DELETE, globalResponseMessages())
				.enableUrlTemplating(false)
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(Arrays.asList(apiKey()));
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title(swaggerProperties.getTitle())
				.description(swaggerProperties.getDescription())
				.version(swaggerProperties.getVersion())
				.contact(new Contact(swaggerProperties.getContactName(),swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail()))
				.license(swaggerProperties.getLicense())
				.licenseUrl(swaggerProperties.getLicenseUrl())
				.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
				.build();
	}

	private ApiKey apiKey() {
		return new ApiKey(AUTHORIZATION, AUTHORIZATION, "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(swaggerProperties.getSecurePaths()))
				.build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;

		List<SecurityReference> list = new ArrayList<>();
		list.add(new SecurityReference(AUTHORIZATION, authorizationScopes));
		return list;
	}

	private List<ResponseMessage> globalResponseMessages() {
		List<ResponseMessage> responseMessages = new ArrayList<>();
		responseMessages.add(new ResponseMessageBuilder().code(200).message(MESSAGE_200).
				responseModel(new ModelRef(PROVIDER_RESPONSE)).build());
		responseMessages.add(new ResponseMessageBuilder().code(400).message(MESSAGE_400).
				responseModel(new ModelRef(PROVIDER_RESPONSE)).build());
		responseMessages.add(new ResponseMessageBuilder().code(500).message(MESSAGE_500).
				responseModel(new ModelRef(PROVIDER_RESPONSE)).build());
		responseMessages.add(new ResponseMessageBuilder().code(403).message(MESSAGE_403).
				responseModel(new ModelRef(PROVIDER_RESPONSE)).build());
		return responseMessages;
	}
}
