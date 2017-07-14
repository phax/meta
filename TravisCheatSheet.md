# Travis

Ensure the Travis commandline tool is installed.
Requires Ruby.
```
gem install travis
```

## Create secret key per project

1. Login with GitHub credentials
```
travis login
```

2. Create an encrypted text
```
travis encrypt
```

Ensure the repository is correct.
When asked for the text type:
```
SONATYPE_PASSWORD=xxx<F6>
<ENTER>
<ENTER>
```
<F6> being the replacement for <Strg>+<D> on Windows...

Copy the text starting with `secure:` in your `.travis.yml` file.

3. Logout again
```
travis logout
```
Should print `Successfully logged out!`
