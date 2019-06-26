package gov.va.bip.framework.test.utils.wiremock.server;

import com.github.tomakehurst.wiremock.WireMockServer;

public class WireMockServerInstance {

	private static final WireMockServer wireMockServer = new WireMockServer(9999);

	public static WireMockServer getWiremockserver() {
		return wireMockServer;
	}

}
