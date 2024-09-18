# XML vs. JSON

This document provides my personal comparison on XML vs. JSON and should give a background why I personally favour XML. 

* History
    * The XML Working Group conceived XML in 1996 and released its initial version in 1998.
      They derived XML from the Standard Generalized Markup Language (SGML).
      After introducing HTML in 1998, they developed XML as a data serialization tool.
    * Douglas Crockford and Chip Morningstar released JSON in 2001.
      They derived JSON from JavaScript. 
* Format
    * JSON uses key-value pairs to create a map-like structure.
      The key is a string, which will identify the pair.
      The value is the information that you give to that key.
      For example, we could have `"NumberProperty": 10`. In this, `NumberProperty` is the key, and `10` is the value.
    * In contrast, XML is a markup language — a subset of SGML with a structure similar to HTML.
      It stores data in a tree structure that presents layers of information that you can follow and read.
      The tree begins with a root (parent) element before giving information about child elements.
* Syntax
    * The syntax used in JSON is more compact and easier to write and read.
      It allows you to define objects easily.
      Special characters need to be escaped with a `\` character or be provided with the Unicode number.
      JSON only supports the UTF-8 character encoding which simplifies processing but limits the applicability, as not all business systems support UTF-8.
    * XML is more verbose and substitutes certain characters for entity references.
      For example, instead of the `<` character, XML uses the entity reference `&lt;`.
      XML also uses end tags, which makes it longer than JSON.
      XML supports different character encodings which supports a wider range of use.
* Parsing
    * You must parse XML with an XML parser.
    * You can parse JSON by a standard JavaScript function, which is more accessible. 
      Because of their simpler syntax and file size differences, you can also parse JSON faster than XML.
* Schema documentation
    * Schema documentation describes the purpose of a file, showing what you should use it for.
    * XML documents may have a link to their schema in the header.
      The schema is also in XML format, which allows you to read what you should expect to find in the file.
      You can then validate the document against the schema and check that everything has loaded correctly and without errors.
    * JSON also allows you to use schemas.
      However, they’re simpler and allow greater flexibility.
* Data type support
    * JSON only supports a limited range of data types like strings, numbers, boolean, objects and arrays.
    * However, XML is more flexible and supports complex data types like binary data and timestamps. 
* Ease of use
    * As a markup language, XML is more complex and requires a tag structure.
    * In contrast, JSON is a data format that extends from JavaScript. It does not use tags, which makes it more compact and easier to read for humans. JSON can represent the same data in a smaller file size for faster data transfer.
* Security
    * JSON parsing is safer than XML.
    * The structure of XML is vulnerable to unauthorized modifications, which creates a security risk known as XML external entity injection (XXE).
      It’s also vulnerable to unstructured external document type declaration (DTD).
      You can prevent both of these issues by easily turning off the DTD feature in transmission.
      XML also allows to read remote includes and schemas which can be a security issue.
* Document metadata
    * JSON does NOT support the provision of metadata in a standardized way, because the syntax does not support it.
      One way is to use specific object keys (e.g. `$doc`) to provide metadata, but that depends on the applications processing it.
      It's not a standard but only a convention.
    * XML support the provision of comments (`<!-- ... -->`) for additional information, processing instructions (`<? ... ?>`) for additional tooling support.
* Query languages
    * XML supports XPath to select or point to specific parts.
      It was introduced back in 1999 and is very mature.
    * JSON tries to standardize JSONPath, but that is still less powerful than XPath:
        * cannot obtain the value of an arbitrary expression in the result .. only values from with the Query Argument are available
        * cannot extract a list of member names (keys) from JSON Objects .. only member values or entire objects can be extracted
        * cannot construct an arbitrary JSON object or array to use as comparison node
        * JSONPath defines no variables that can be used from within its expressions
        * XPath has an extensive library for date manipulation, string manipulation, URI resolution, etc
        * XPath allows for user-defined functions, for-declensions, arbitrary map/array instantiation, higher-order function evaluation, etc
* Document validation
    * JSON documents can primarily be checked on the syntax level.
      JSON Schema is still a draft and may change in the future.
    * XML document can be verified on the syntactical level via XML Schema (XSD), RelaxNG (RNG or RNC) or DTD (out of date) - they define structure, cardinalities and data types.
      On top of that other validation languages like Schematron managed to become an ISO standard to verify the semantic integrity of a message.
      It facilitates XPath and XSLT to define technical assertions.
* Document transformation
    * The JSON ecosystem provides a couple of different possible solutions to extract and/or transform JSON documents into other formats.
      However, it seems like none of these proposals made it to a standard.
    * XML documents are transformed via XSLT into other XML, HTML or text documents.
      XSLT is an industry standard and widely supported.
      Additionally, XSL-FO can even be used to create PDF outputs based on an open standard. 

Summary table:

| Item | JSON | XML |
| ---- | ---- | --- |
| Stands for | JSON means JavaScript Object Notation. | XML means Extensible Markup Language. |
| History | Douglas Crockford and Chip Morningstar released JSON in 2001. | The XML Working Group released XML in 1998. |
| Format | JSON uses a maplike structure with key-value pairs. | XML stores data in a tree structure with namespaces for different data categories. |
| Syntax | The syntax of JSON is more compact. | The syntax of XML substitutes some characters for entity references, making it more verbose. |
| Parsing | You can parse JSON with a standard JavaScript function. | You need to parse XML with an XML parser. |
| Schema documentation | JSON is simple and less flexible. | XML is complex and more flexible. |
| Data types | JSON supports numbers, objects, strings, and Boolean arrays. | XML supports all JSON data types and additional types like Boolean, dates, images, and namespaces. |
| Ease of use | JSON has smaller file sizes and faster data transmission. | XML tag structure is more complex to write and read and results in bulky files. |
| Security | JSON is safer than XML. | You should turn off DTD when working with XML to mitigate potential security risks. |
| Document metadata | No standardized way in JSON | XML supports comments and powerful processing instructions. |
| Query languages | JSON supports JSONPath | XML supports XPath. XPath is more powerful than JSONPath |
| Document validation | JSON has support for structural validation only | XML supports different structural validation languages as well as semantical validations on top of it |
| Document transformation | No standardized way exists for JSON | XML uses XSLT and XSL-FO as powerful tools to transform XML to arbitrary outputs |

Existing sources:
* https://aws.amazon.com/compare/the-difference-between-json-xml/
* JSONPath: an IETF Proposed Standard, with comparisons to XPath; Alan Painter; Proceedings of XML Prague 2024
