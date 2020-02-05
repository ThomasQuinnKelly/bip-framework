mavenLibraryPipeline {

    skipTests = true
    skipFunctionalTests = true
    skipPerformanceTests = true
    skipSonar = true
    skipFortify = true

    //Specify to use the fortify maven plugin, instead of the Ant task to execute the fortify scan
    useFortifyMavenPlugin = true

    /*************************************************************************
    * Docker Build Configuration
    *************************************************************************/

    // Map of Image Names to sub-directory in the repository. If this is value is non-empty,
    // the build pipeline will build all images specified in the map. The example below will build an image tagged as
    // `blue/bip-framework:latest` using the Docker context of `./bip-reference-person`.
    dockerBuilds = [
        'blue/bip-framework': 'bip-framework'
    ]
}