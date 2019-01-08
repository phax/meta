# Installing Apache httpd with SVN on Win64

## Install Win64 OpenSSL

* Download from https://slproweb.com/products/Win32OpenSSL.html
* This is required for mod_ssl to work

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
* conf/extra/httpd-ssl.conf
* conf/extra/httpd-vhosts.conf
* conf/ssl/*

## Test config and start httpd

* Run `httpd -t` for configuration test
* Run `httpd -k install` as Administrator! Make sure to uninstall the old service first (with `httpd -k uninstall`).
