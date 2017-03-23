#My personal Git cheat sheet

## Global settings

Basic user info:
```
git config --global user.email "philip@helger.com"
git config --global user.name "Philip Helger"
```

Store credentials when using commandline git:
```
git config --global credential.helper wincred
```

Newline settings for Windows users:
```
git config --global core.autocrlf true
git config --global core.eol lf
```

Proxy settings:
```
set HTTP_PROXY=http://proxy.mycompany.org:80
git config --global http.proxy %HTTP_PROXY%
```

Disable acknowledgement box in Eclipse:
```
git config push.default simple
```

## Release a branch with maven-release-plugin

Set the `<scm><tag>` element to e.g. `origin/1.2` where *1.2* is the name of the branch at origin.

## How to remove a file from the index in git?

```
git rm --cached [file]
```

If you omit the --cached option, it will also delete it from the working tree. git rm is slightly safer than git reset, because you'll be warned if the staged content doesn't match either the tip of the branch or the file on disk. (If it doesn't, you have to add --force.)

## Delete a tag

Delete local:
```
git tag --delete tagname
```

Delete remote:
```
git push --delete origin tagname
```
