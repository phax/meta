#My personal Git cheat sheet

## Global settings
Store credentials when using commandline git:
```
git config --global credential.helper wincred
```

## Release a branch with maven-release-plugin

Set the `<scm><tag>` element to e.g. `origin/1.2` where *1.2* is the name of the branch at origin.
 