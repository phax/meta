#My Coding Styleguide

The following list gives a short overview of special programming techniques that are used inside all of my projects.

## Naming conventions
  * Classes always start with an upper case identifier and use camel case for naming (e.g. `StringHelper`)
  * Abstract classes always start with the term `Abstract`
  * All interfaces are named starting with a capital 'I' followed by a second uppercase character (like in `IHasID`). By default all interfaces contain only read-only methods except the interface name starts with `IMutable` in which case the interface also contains modifying methods.
  * All enumerations are named starting with a capital 'E' followed by a second uppercase character (like in `EUnicodeBOM`)
  * All member variables are private or protected and use the Hungarian notation (like `aList`). The used prefixes are:
    * `a` for all kind of objects that do not fall into any other category (e.g. `aList`) - this also includes all kind of arrays, even of primitive type
    * `b` for boolean variables (e.g. `bShow`)
    * `c` for character variables (e.g. `cFirst`)
    * `d` for double variables (e.g. `dAmount`)
    * `e` for enum variables (e.g. `eType`)
    * `f` for float variables (e.g. `fSum`)
    * `n` for byte, int, long and short variables (e.g. `nIndex`)
    * `s` for String variables (e.g. `sText`)
  * The scope of a field is indicated by either the prefix `m_` for instance (member) fields, and `s_` for static fields. A special case are "static final" fields which may omit this prefix and use only upper case character (e.g. `DEFAULT_VALUE` as in `public static final boolean DEFAULT_VALUE = true;`)
  * Private methods (no matter if static or not) always start with a single underscore (`_`) and are not declared `final` as they are implicitly final.
  
##Special annotations
  * All methods returning collections (lists, sets, maps etc.) and arrays are usually returning copies of the content. This helps ensuring thread-safety (where applicable) but also means that modifying returned collections has no impact on the content of the "owning" object. In more or less all cases, there are "add", "remove" and "clear" methods available to modify the content of an object directly. All the methods returning copies of collections and arrays should be annotated with `@ReturnsMutableCopy`. In contrast if the inner collection or array is returned directly (for whatever reason) it should be annotated with `@ReturnsMutableObject` in which case a special description is provided in the annotation. If an unmodifiable collection is returned, the corresponding annotation is `@ReturnsImmutableObject` (e.g. for `Collections.unmodifiableList` etc.) (this is not applicable for arrays).
  * For all non primitive parameter the annotations `@Nonnull` or `@Nullable` are used, indicating whether a parameter can be `null` or not. Additionally for Strings, collections and arrays the annotation `@Nonempty` may be present, indicating that empty values are also not allowed. All these annotations have no impact on the runtime of an application. They are just meant as hints for the developers.
  * Domain model classes are annotated with the FindBugs annotations `@Immutable`, `@ThreadSafe` or `@NotThreadSafe` to indicate their thread-safety level.
  
##General conventions
  * All projects include Eclipse project files (`.project`, `.classpath` and `.settings`)
  * All projects use Apache Maven 3.x for building (`pom.xml`)
  * Most projects contain a FindBugs configuration. Therefore the file `findbugs-exclude.xml` must be present in each projects root directory. 
  * All logging is done via SLF4J. The logger is more or less always declared as `private static final Logger s_aLogger`. In certain cases it might also be `protected`.
  * All projects (except for JDK extensions) use [ph-commons](https://github.com/phax/ph-commons) which is the most basic library and can be considered a JDK extension.
  * Synchronization (thread-safety) of code is achieved using `java.util.concurrent.locks.ReadWriteLock` which allows multiple readers but only a single writer to access a critical section at a time. For projects using JDK 8 (or higher) the respective class `com.helger.commons.concurrent.SimpleReadWriteLock` is used for better Lambda support.
  * Interface methods never contain the visibility specifier as it is implied.
  * No class defines a `serialVersionUID` as this is automatically done by the Java runtime upon execution.

##JDK 8 specialties
  * Starting with ph-commons 8 synchronization is done using `com.helger.commons.concurrent.SimpleReadWriteLock` which is a special `ReeentrantReadWriteLock` implementation with better support for the usage with Lambdas.
  * I'm not using the stream API since there are performance considerations and the regular iteration usually works pretty efficient and helper methods with Lambdas are available.
  * Starting with ph-commons 8 a set of collection interfaces and classes starting with `ICommons` (e.g. `ICommonsList`) resp. `Commons` (like `CommonsArrayList`) are used because they offer tons of default sanity methods.
