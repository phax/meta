# Installing Apache httpd with SVN on Win64

Version as of 2019-01, tested with OpenSSL 1.0.1j and Apache 2.4.37 VC15.

## Install Win64 OpenSSL

* This is required for mod_ssl to work
* Download from https://slproweb.com/products/Win32OpenSSL.html
* Option "copy into Windows directory" MUST be checked

## Get a binary version of Apache from ApacheLounge:

* Download from https://www.apachelounge.com/download/
* Unzip to defined folder

## Download Win64 SVN build

* Download from https://github.com/nono303/win-svn/
* Go to tag of appropriate SVN release
* Unzip into binary httpd folder - overwrite all

## Copy configuration from old directory

Affected files:
* conf/httpd.conf
* conf/extra/httpd-sni.conf
* conf/extra/httpd-vhosts.conf
* conf/ssl/*

## Test config and start httpd

* Run `httpd -t` for configuration test
* Run `httpd -k install` as Administrator!
    * Make sure to uninstall the old service first (with `httpd -k uninstall`).
    * Optionally the `-n name` parameter can be used to chose a service name (must also be provided upon uninstall)

## Generate self signed certificate for httpd

Must be run in the Apache home folder!

Source: http://www.akadia.com/services/ssh_test_certificate.html

```
::Step 1: Generate a Private Key
bin\openssl genrsa -des3 -out server.key 2048

::Step 2: Generate a CSR (Certificate Signing Request)
bin\openssl req -new -key server.key -out my-server.csr

::Step 3: Remove Passphrase from Key
copy server.key server.key.org
bin\openssl rsa -in server.key.org -out server.key
::bin\openssl req -new -key server.key -out server.csr -config conf\openssl.cnf

::Step 4: Generating a Self-Signed Certificate
bin\openssl x509 -in my-server.csr -out server.crt -req -signkey server.key -days 10000

::Convert to DER format
bin\openssl x509 -in server.crt -out server.der.crt -outform DER

::Copy to httpd folder
::for %%i in (server.der.crt server.csr server.key .rnd privkey.pem server.crt) do move %% i conf\
for %%i in (server.crt server.key) do copy %%i conf\ssl
```
