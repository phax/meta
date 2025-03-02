# My personal Git cheat sheet

## Global settings

Basic user info:

```
git config --global user.email "philip@helger.com"
git config --global user.name "Philip Helger"
```

Store credentials when using commandline git.
Windows:

```
git config --global credential.helper wincred
```

Linux:

```
git config --global credential.helper cache
```

Newline settings for Windows users:

```
git config --global core.autocrlf true
# git config --global core.eol lf
```

Proxy settings:

```
set HTTP_PROXY=http://proxy.mycompany.org:80
git config --global http.proxy %HTTP_PROXY%
```

Note: git does not differentiate between `http` and `https`

Exclude certain hosts from using the proxy:

```
set NO_PROXY=.company.com,localhost,127.0.0.1,::1
```

Note: change `.company.com` to your domain(s) and keep the leading dot!

Disable TLS verification for all repos:

```
git config --global http.sslVerify false
```

Enable prune behaviour for every fetch

```
git config --global fetch.prune true
```

Define a custom git alias to prune all gone local branches (for Linux) (source https://stackoverflow.com/a/48649889/15254):

```
git config --global alias.prune-branches '!git remote prune origin && git branch -vv | grep '"'"': gone]'"'"' | awk '"'"'{print $1}'"'"' | xargs -r git branch -d'
```

## Select a Diff Tool

Select diff tool on Mac:
```
brew install kdiff3
git config --global merge.tool kdiff3
git config --global diff.tool kdiff3
```

## Release a branch with maven-release-plugin

Set the `<scm><tag>` element to e.g. `origin/1.2` where *1.2* is the name of the branch at origin.

## How to remove a file from the index in git?

```
git rm --cached [file]
```

If you omit the --cached option, it will also delete it from the working tree. git rm is slightly safer than git reset, because you'll be warned if the staged content doesn't match either the tip of the branch or the file on disk. (If it doesn't, you have to add --force.)

Remove ALL Eclipse project files:

```
git rm --cached -r .classpath .project .settings
```

## Security related

Globally ignore all certificate errors:

```
git config --global http.sslVerify false
```

## Delete a tag

Delete local:

```
git tag --delete tagname
```

Delete remote:

```
git push --delete origin tagname
```

## Eclipse and git

Get rid of all non-existing remote branches:

```
git fetch --prune
```

Disable acknowledgement box in Eclipse (not 100% sure):

```
git config push.default simple
```
