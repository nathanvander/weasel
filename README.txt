README

Weasel is a very simple webserver designed to dynamically load WebObjects.  A WebObject is just a class that implements weasel.WebObject.
This uses the Apollo database server.

Instead of a file name, request the webobject.

For example:  http://localhost:18080/weasel.Index/?a=b

The purpose of this is to be a report server for the projects I am developing.