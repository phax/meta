#My personal Git cheat sheet

## Global settings
Store credentials when using commandline git:
```
git config --global credential.helper wincred
```

## Release a branch with maven-release-plugin

Set the `<scm><tag>` element to e.g. `origin/1.2` where *1.2* is the name of the branch at origin.

## How to remove a file from the index in git?

```
git rm --cached [file]
```

If you omit the --cached option, it will also delete it from the working tree. git rm is slightly safer than git reset, because you'll be warned if the staged content doesn't match either the tip of the branch or the file on disk. (If it doesn't, you have to add --force.)
