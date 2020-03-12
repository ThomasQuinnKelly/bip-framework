# Overview of the package

## gov.va.bip.framework.localstack.autoconfigure:

The LocalstackAutoConfiguration class defines the Localstack configuration strategy for the BIP. This configuration takes care of starting up, configuring, stopping, and cleaning up localstack upon shutdown of running the application.

It handles the configuration of the properties of the Localstack configuration based on property values retrieved from implementing projects

bip.framework.localstack
    
    enabled - required for activation because this is used for @ConditionalOnProperty for this bean and every class in these packages
    
    externalHostName - external host name for Localstack. Defaults to localhost
    pullNewImage - determines whether to pull a new image each time or not. Defaults to true
    imageTag - which localstack image to pull. Defaults to latest latest
    randomizePorts - whether to randomize the service ports for enabled services. Defaults to false

The LocalstackProperties class handles inclusion and enabling of the selected services