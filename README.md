## The EJPRD Resource Metadata Standard(ERMS) - Minimal Information 

The ERMS-MI consists of seven modules that reflect the process of generating, integrating, validating, and using a ERMS-MI model: 
### EJPRD RESOURCE METADATA IMPLEMENTATION

* This is currently hosted at the EJP here http://www.ejprd.org/resource_metadata
* A REST API for Resource Metadata Implementation is described in The API Documentation

### The API Documentation
This page describes how to use the Resource Matadata Implementation to validate the metadata the resources metadata you want to publish the EJPRD virtual platform for resource discovery. 

All requests should be made to the root URL of the Semerd API, which is as shown below. 
The root URL for the API is "http://localhost:8080/semanticmodel/api".

Coverting your Resources metadata to RDF formats: You can use this api to convert your resources metadatafrom JSON to RDF which can further be transform to JSON-LD for easy validation of the metadata. The final result confirms the validity of your metadata compliance with the EJPRD reources metadata standards via a report.

Example Request:
Covert a metadata in JSON format to RDF format and validate it: 

###Sample Resource Metadata:



