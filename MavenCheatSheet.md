# My personal Maven cheat sheet

##Installation

  * Download Maven
  * Extract to `d:\tools\` &rarr; will create `apache-maven-x.y.z` folder
  * Set environment variable:
    * `set M2_HOME=d:\tools\apache-maven-x.y.z`
    * `set PATH=%PATH%;%M2_HOME%\bin`
  * Create settings.xml:

```xml
<?xml version="1.0" encoding="iso-8859-1"?>
<settings>
  <localRepository>D:/maven-repo/</localRepository>
  <servers>
    <server>
      <id>repo.ph.nexus</id>
      <username>xx</username>
      <password>yy</password>
    </server>
    <server>
      <id>ossrh</id>
      <username>xx</username>
      <password>yy</password>
    </server>
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>xx</username>
      <password>yy</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>xx</username>
      <password>yy</password>
    </server>
  </servers>
  <proxies>
    <proxy>
      <active>true</active>
      <protocol>http</protocol>
      <host>1.2.3.4</host>
      <port>8080</port>
      <!-- separate by | char -->
      <nonProxyHosts>*.gv.at</nonProxyHosts>
   </proxy>
  </proxies>
  <mirrors>
    <mirror>
      <id>repo.ph.nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>http://example.org/nexus/content/groups/public</url>
    </mirror>
  </mirrors>
  <pluginGroups>
    <pluginGroup>org.apache.axis2</pluginGroup>
  </pluginGroups>
</settings>
```
