# BIP API Framework Release SOP

The purpose of this document is to identify and describe the Standard Operating Procedures for cutting a new release of the BIP API Framework codebase. Users should be able to follow this document as a step-by-step guide for creating a new release of the Framework.
  
## Step 1: Ensure Stable State

First, we need to guarantee that the current state of the `master` branch is stable, and that the current Jenkins builds are passing.
 
1. Ensure all necessary changes and pull requests have been successfully merged into the `master` branch for this release.
2. Navigate to the BIP Framework [Master Branch Jenkins Job](https://jenkins-blue-dev.dev8.bip.va.gov/job/bip-framework/job/master/).
3. Run a new build using the "Build with Parameters" button.
    1. Leave both parameters `releaseVersion` and `developmentVersion` empty.
4. Wait for the build to complete **successfully**. 
    1. If a failure occurs, the underlying issue will need to be resolved before continuing with this release cut.
5. **Note: The following step is only necessary to support the DSVA repository copy.**
    1. Synchronize all changes from the [EPMO repository](https://github.ec.va.gov/EPMO/bip-framework) to the [DSVA repository](https://github.com/department-of-veterans-affairs/bip-framework).
    
## Step 2: Cut a 1.x.x Release Branch

We need to cut a separate branch for a 1.x.x release, and merge in only the appropriate code changes for this release.

##### Note: This step will become unnecessary once remaining tenants utilizing the 1.x.x Framework versioning have transitioned to a 2.x.x+ version. You may want to request a data call to verify if any teams are still using these versions.

1. Create and push a new branch following the naming convention `release-1.x.x` based on the most recent `release-1.x.x` branch.
2. Manually pull in and merge the latest commits tied to this release. You should not include the older commits from 2.x.x releases.
    1. It's recommended to use the `git cherry-pick -n <<commit_id>>` command to pull in only the specific commits slated for this release.
3. Push the merged changes, and navigate to the Jenkins Job for this release branch and perform a build to ensure the new branch is stable.
    1. Leave both parameters `releaseVersion` and `developmentVersion` empty.
    
## Step 3: Perform Release Cuts

Next, we activate and run the Jenkins release cut pipeline steps, which will accomplish the following:
 * Updates project versioning to the `releaseVersion` Jenkins parameter value, and commit these changes to the branch
 * Tags this commit in Git with the `releaseVerison`, i.e. `2.x.x`
 * Generates release artifacts and deploy them to Nexus
 * Updates project versioning to the `developmentVersion` Jenkins parameter value, and commit these changes to the branch
 * Pushes all changes to the remote repository
 
Steps to run this job:
1. Navigate to the BIP Framework [Master Branch Jenkins Job](https://jenkins-blue-dev.dev8.bip.va.gov/job/bip-framework/job/master/).
2. Run a new build using the "Build with Parameters" button.
    1. Set the `releaseVersion` parameter to the desired version of the release you are cutting, i.e. `2.x.x`.
    2. Set the `developmentVersion` parameter to the next incremented development version, i.e. `2.x.y-SNAPSHOT`.
3. Wait for the build to complete successfully, and double-check the [Dev8 Nexus Repository](http://nexus.dev8.bip.va.gov/) for the newly pushed artifacts.
4. **Note: The following steps are only necessary to support `1.x.x` releases**
    1. Navigate to the `release-1.x.x` branch created in "Step 2: Cut a 1.x.x Release Branch".
    2. Run a new build using the "Build with Parameters" button.
        1. Set the `releaseVersion` parameter to the desired version of the release you are cutting, i.e. `1.x.x`.
        2. Set the `developmentVersion` parameter to the next incremented development version, i.e. `1.x.y-SNAPSHOT`.
    3. Wait for the build to complete successfully, and double-check the [Dev8 Nexus Repository](http://nexus.dev8.bip.va.gov/) for the newly pushed artifacts.
 
## Step 4: Update DSVA Repository

We need to update the corresponsing [BIP Framework DSVA repository](https://github.com/department-of-veterans-affairs/bip-framework) with release changes and Git tags.

##### Note: This step will become unnecessary once the Framework no longer needs to support the DSVA repository copies.

1. Manually perform changes to update all necessary project artifacts to the desired release version, i.e. `2.x.x`.
    1. It's recommended to use your IDE of choice and the "find and replace" functionality to perform this.
2. Check these changes into the `master` branch, ensure the code has a successful build, and add/push a git tag for the release.
    1. `$ git tag -a 2.x.x -m "Framework artifacts release cut 2.x.x"`
    2. `$ git push --tags`
3. Manually perform changes to update all necessary project artifacts to the next desired development version, i.e. `2.x.y-SNAPSHOT`.
    1. It's recommended to use your IDE of choice and the "find and replace" functionality to perform this.
4. Check these changes into the `master` branch, and push all changes to the remote DSVA repository.

## Step 5: Modify the Framework Release Notes

The release notes wiki page for the BIP Framework project needs to be updated to reflect the latest changes.

1. Navigate to the [BIP Framework Project Release Notes](https://github.ec.va.gov/EPMO/bip-framework/wiki/Framework-Release-Notes) page.
2. Update the notes with all relevant information for this release cut.
    1. Copy and paste a new "Release" section to the top of the wiki page (Don't overwrite an older release note section).
    2. Update any necessary versioning.
    3. Add the appropriate JIRA tickets which this release covers.
    4. Add any notable feature additions and code changes that may be relevant to consumers of the BIP Framework.
    
## Step 6: Update the BIP Archetype Service

The Archetype Service project needs to be updated to the latest Framework release, so new tenants starting with this project will start with the most recent BIP Framework version.

1. Manually update the bip-framework versioning in the [EPMO BIP Archetype Service](https://github.ec.va.gov/EPMO/bip-archetype-service) repository to the latest release version.
2. Ensure the project generates and builds successfully with the latest version.
3. Push these changes to the repository.
4. **Note: The following step is only necessary to support the DSVA repository copy.**
    1. Synchronize these changes to the [DSVA repository](https://github.com/department-of-veterans-affairs/bip-archetype-service).
    
## Step 7: Update the BIP Reference Person Project

If the most recent Framework release adds new features that should be reflected in the BIP Reference Person project for tenants to leverage as an example, a separate story should be created and slated for a future sprint to implement these features.