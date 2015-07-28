# Vertx pdf-generator

This vert.x **worker** module generates PDF documents given an XHTML/XML input.

## Usage

This module registers itself on the vert.x bus and waits for a `Message<JsonObject>`.
Its address is `entcore.pdf.generator` by default, this can be changed by defining the field `pdf-generator-address` in the module configuration file.

Below is a short description of the json message syntax :

```javascript
{
    "content": //Array of bytes, containing the xml/xhtml to parse & render.
    "encoding": //[Optional] Content encoding, default is UTF-8.
    "baseUrl": //[Optional] Location to find data resources if they are included in the PDF (such as images). 
}
```

The reply is a JsonObject :

```javascript
{
	"status":  // "ok" or "error"
	"content": // If "ok", the PDF as a byte Array
	"message": // If "error", the error message if any
}
```

## Dependencies

- FlyingSaucer : http://flyingsaucerproject.github.io/
