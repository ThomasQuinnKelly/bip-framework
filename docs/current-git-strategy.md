#Current Git Strategy

This document is intended to identify and maintain the current repositories which are owned/supported by the API Framework team, as well as the current strategy for maintaining these repositories (Strategies around branches, PRs, merges, releases, etc.).

## Repositories
### Catalog of Repositories
##### Main Repos
   * [bip-framework](https://github.ec.va.gov/EPMO/bip-framework)
     * [DSVA Counterpart](https://github.com/department-of-veterans-affairs/bip-framework)
   * [bip-jenkins-lib](https://github.ec.va.gov/EPMO/bip-jenkins-lib)
     * [DSVA Counterpart](https://github.com/department-of-veterans-affairs/bip-jenkins-lib)
   
##### Reference Repos
   * [bip-reference-person](https://github.ec.va.gov/EPMO/bip-reference-person)
     * [DSVA Counterpart](https://github.com/department-of-veterans-affairs/bip-reference-person)
   * [bip-external-config](https://github.ec.va.gov/EPMO/bip-external-config)
     * [DSVA Counterpart](https://github.com/department-of-veterans-affairs/bip-external-config)
 
##### Archetype Repos
   * [bip-archetype-service](https://github.ec.va.gov/EPMO/bip-archetype-service)
     * [DSVA Counterpart](https://github.com/department-of-veterans-affairs/bip-archetype-service)
   * [bip-archetype-config](https://github.ec.va.gov/EPMO/bip-archetype-config)
     * [DSVA Counterpart](https://github.com/department-of-veterans-affairs/bip-archetype-config)
   
##### Sandbox Repos
   * [bip-archetypetest](https://github.ec.va.gov/EPMO/bip-archetypetest)
   * [bip-archetypetest-config](https://github.ec.va.gov/EPMO/bip-archetypetest-config)
   
##### Undefined Ownership Repos...
   * [bip-ci-k8s](https://github.ec.va.gov/EPMO/bip-ci-k8s)
   * [BIP-CI](https://github.ec.va.gov/EPMO/BIP-CI)
   * [bip-participant-intake](https://github.ec.va.gov/EPMO/bip-participant-intake)
   
### Branching Strategies
##### bip-framework
    Single 'master' branch
    Create new branch from 'master', commit work, push to main repo, create PR, merge
    No safeguards on PR merges
    Multiple dead branches
##### bip-jenkins-lib
    Single 'promotion' branch (Other tenant-specific 'promotion' branches, need to identify if these are in use)
    Create new branch from 'promotion', commit work, push to main repo, create PR, merge
    PR merges are blocked until at least one review approval has been confirmed
    Multiple dead branches
##### bip-reference-person
    Single 'master' branch
    Create new branch from 'master', commit work, push to main repo, create PR, merge
    No safeguards on PR merges
    Multiple dead branches
##### bip-external-config
    Main 'master' branch, with an active 'development' branch (Use of 'staging' or 'test' branches??)
    Create new branch from 'development', commit work, push to main repo, create PR, merge
    No safeguards on PR merges
    Multiple dead branches
##### bip-archetype-service
    Main branches 'master', 'master-db', and 'master-partner'
        Services are generated off of 'master'
        Generation script will merge in 'master-db' and/or 'master-partner' based on configured properties
    For submitting changes, see the [README](https://github.ec.va.gov/EPMO/bip-archetype-service/blob/master/README.md)
    No safeguards on PR merges
    Multiple dead branches
##### bip-archetype-config
    Single 'master' branch
    Create new branch from 'master', commit work, push to main repo, create PR, merge
    No safeguards on PR merges
    Multiple dead branches
##### bip-archetypetest
    TODO: Revisit
##### bip-archetypetest-config
    TODO: Revisit
##### bip-ci-k8s
    Single 'master' branch
    Create new branch from 'master', commit work, push to main repo, create PR, merge
    No safeguards on PR merges
    Multiple dead branches
##### BIP-CI
    Single 'master' branch
    Create new branch from 'master', commit work, push to main repo, create PR, merge
    No safeguards on PR merges
    Multiple dead branches
##### bip-participant-intake
    Main 'master' branch, with an active 'development' branch
    Create new branch from 'development', commit work, push to main repo, create PR, obtain review approval, merge
    PR merges are blocked until at least one review approval has been confirmed
    Multiple dead branches
    
### Release Strategies
##### bip-framework
    TODO
##### bip-jenkins-lib
    TODO
##### bip-reference-person
    TODO
##### bip-external-config
    TODO
##### bip-archetype-service
    TODO
##### bip-archetype-config
    TODO
##### bip-archetypetest
    TODO
##### bip-archetypetest-config
    TODO
    
    
##Possible Improvements
 * Enforce PRs to have at least one approval to merge
 * Adopt [Git-Flow](https://danielkummer.github.io/git-flow-cheatsheet/) approach for handling code changes to align with other teams/repos
 * Clean up dead branches
 * Enforce branch naming conventions (Possibly with Git-Flow)
 * Standardize release version reasoning (e.g. {Major Release}.{Minor Release}.{Hotfix} ??)
    