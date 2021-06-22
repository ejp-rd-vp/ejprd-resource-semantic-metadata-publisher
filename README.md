## The EJPRD Resource Metadata Standard(ERMS) - Minimal Information 

The ERMS-MI consists of seven modules that reflect the process of generating, integrating, validating, and using a ERMS-MI model: 
### EJPRD RESOURCE METADATA IMPLEMENTATION

* This is currently hosted at the EJP [here] (https://$/)
* A REST API for Resource Metadata Implementation is described in The API Documentation
* For detailed information on the modules of the model click [here](https://github.com/ejp-rd-vp/resource-metadata-schema)
* For additional resources such as the EJPRD Ontology click [here](https://github.com/EBISPOT/EJP-Ontology/releases/tag/19-11-2019)
* See the metadata model diagram [here](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/images/resourceSemanticMetadataModel.png)

### The API Documentation
This page describes how to use the Resource Matadata Implementation to validate the metadata the resources metadata you want to publish the EJPRD virtual platform for resource discovery. 

All requests should be made to the root URL of the Semerd API, which is as shown below. 
The root URL for the API is "http://$/api/v1/semanticmodel/".

Coverting your Resources metadata to RDF formats: You can use this api to convert your resources metadata from JSON or your data in RDF format directly to RDF which can further be transform to JSON-LD for easy validation of the metadata. 
The following steps demonstrates how to check if your metadata is compliant with the EJPRD resources metadata standards.

### Lists of modules that can be validated : 

- [Overview](#1-overview)
- Modules

  - [Organisation](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/organisation.md)
  - [Location](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/location.md)
  - [Dataset](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/dataset.mdt)
  - [Catalog](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/catalog.md)
  - [Resources](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/resource.md)
  - [Dataservices](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/dataservices.md)
  - [Distribution](https://github.com/ejp-rd-vp/resource-metadata-schema/blob/master/docs/distribution.md)
  - [Person]()

| Modules       | Ontology  | Description                                     |
| -----------  |--------|-------------------------------------------------|
| Person       | foaf | The Person module represents people. The Person class is a sub-class of the Agent class, since all people are considered 'agents' in FOAF.        |
| Location     | dct |  Spatial region or named place.                  |
| Organisation | foaf | The Organization class represents a kind of Agent corresponding to social instititutions such as organisations, societies etc.                      |
| Dataset      | dcat | A collection of data, published or curated by a single agent, and available for access or download in one or more representations.        |
| Catalog      | dcat | A curated collection of metadata about resources (e.g., datasets and data services in the context of a data catalog)           |
| Resources    | dcat | Resource published or curated by a single agent.|
| Dataservices | dcat | A collection of operations that provides access to one or more datasets or data processing functions.       |
| Distribution | dcat | A specific representation of a dataset. A dataset might be available in multiple serializations that may differ in various ways, including natural language, media-type or format, schematic organization, temporal and spatial resolution, level of detail or profiles (which might specify any or all of the above).          |

The listed modules are the metadata that are allowed to be validated via this platform. In case you have other metadata kindly drop the metadata type and its properties in the issues section and this will be addressed in subsequent versions.

### How to check your metadata : 
1. Get your metadata ready either in JSON format or RDF format e.g. The person module in json format  and RDF format

#### Person in json: 
```
{
    "person": {
      "id": "EBI09394092020",
      "last name": "John",
      "first name": "Mathew",
      "workInfoHomepage": "www.dcat.org",
      "email Address": "ego@gmail.com",
      "contactPhoneNumber": "+44(0)0124511234",
      "officeDesignation": "Research Officer",
      "publication": "Elsevier Journal of Science"
    }
  }

This is saved as e.g. "person.json"

```
#### Person in RDF format:

```
@prefix foaf: <http://xmlns.com/foaf/0.1/> .
@prefix ns0: <http://semanticscience.org/resource/> .
@prefix ns1: <http://purl.obolibrary.org/obo/> .
@prefix ns2: <http://purl.allotrope.org/ontologies/result#> .

<http://example.org/EBI09394092020>
a         foaf:Person ;
ns0:SIO_000182 "John" ;
ns1:IAO_0000429 "ego@gmail.com" ;
ns0:SIO_001324 "+44(0)0124511234" ;
ns0:SIO_000087 "Elsevier Journal of Science" ;
ns1:NCIT_C19467 "www.dcat.org" ;
ns0:SIO_000181 "Mathew" ;
ns2:AFRL_0000403 "Research Officer" .

```

2. Your resource metadata can be checked for compliant using the EJPRD resource metadata publisher through the HTML interface or through its API `http://$/api/v1/semanticmodel/` 
Click on the link to access the link to the [Html index page](http://$/api/v1/semanticmodel/) or use the API via the command line.

3. Click on the appropriate tab for your data format (Convert your json file or Convert your RDF file) as shown in the image below: 

<p align="center"> 
    <a href="https://github.com/ejp-rd-vp/ejprd-resource-semantic-metadata-publisher/blob/master/person.png" target="_blank">
        <img src="../blob/master/person.png"> 
    </a>
</p>

4. The result for the validation is shown in the shEx report section. 
The result will inform you if your metadata is compliant with the EJPRD resource metadata model. 
   

# API documentation

This repository contains the documentation for API.

#### Contents


Example Request:
Covert a metadata in JSON format to RDF format and validate it:

## 1. Overview

EJPRD RESOURCE METADATA’s API is a JSON-based OAuth2 API. All requests are made to endpoints beginning:
`http://$/api/v1/semanticmodel/`

All requests must be secure, i.e. `https`, not `http`.

##2. Resources

The API is RESTful and arranged around resources. All requests must be made with your metadata or data in either JSON format or RDF (*.ttl or *.nt). All requests must be made using `https`.

Typically, the your request can be checking each of your modules entities or the main metadata for your resources: 

### Examples 

### 2.1. Users

#### Checking and validating your metadata against the EJPRD Resource metadata model 

Returns the validation result for the users resources metadata depending on the modules selected for valiation.

```
POST "http://$/api/v1/semanticmodel/semerd"
```

####Example request:

```
POST /upload

HTTP request that looks as follows:

Host: http://$/semanticmodel/semerd
Content-Length: 808
Content-Type:  multipart/form-data; boundary=abcde12345
[file content goes there]
Content-Type:  text/plain; boundary=abcde12345
[text content goes there]

```
####Example of User's file in Json format (Person object) to be validated:
```
{
    "person": {
      "id": "EBI09394092020",
      "last name": "John",
      "first name": "Mathew",
      "workInfoHomepage": "www.dcat.org",
      "email Address": "ego@gmail.com",
      "contactPhoneNumber": "+44(0)0124511234",
      "officeDesignation": "Research Officer",
      "publication": "Elsevier Journal of Science"
    }
  }

This is saved as e.g. "person.json"

```



The HTTP request will look as follows:

####Example request:

```

POST /semanticmodel/semerd HTTP/1.1
Host: localhost:8080
Content-Length: 289
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="multipartFile"; filename="person.json"
Content-Type: application/json

(data)
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="mappingFile"

Person
----WebKitFormBoundary7MA4YWxkTrZu0gW

```


```
curl --location --request POST 'http://localhost:8080/semanticmodel/semerd' \
--form 'multipartFile=@"/Users/dipo/Documents/GitHub/ejprd-resource-semantic-metadata-publisher/semanticmodel/src/main/resources/jsonfiles/person.json"' \
--form 'mappingFile="Person"'


# wget doesn't support file upload via form data, use curl -F \
wget --no-check-certificate --quiet \
  --method POST \
  --timeout=0 \
  --header '' \
  --body-data 'mappingFile=Person' \
   'http://localhost:8080/semanticmodel/semerd'

```

#### Output / Result  when your model conforms with the EJPRD Person module: 


```
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
{
    "inputFile": "{\n\n    \"person\": {\n      \"id\": \"EBI09394092020\",\n      \"last name\": \"John\",\n      \"first name\": \"Mathew\",\n      \"workInfoHomepage\": \"www.dcat.org\",\n      \"email Address\": \"ego@gmail.com\",\n      \"contactPhoneNumber\": \"+44(0)0124511234\",\n      \"officeDesignation\": \"Research Officer\",\n      \"publication\": \"Elsevier Journal of Science\"\n    }\n  }\n\n\n",
   
    "outputFileInTTl": "<http://example.org/EBI09394092020> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://xmlns.com/foaf/0.1/Person>.\n<http://example.org/EBI09394092020> <http://semanticscience.org/resource/SIO_000182> \"John\".\n<http://example.org/EBI09394092020> <http://purl.obolibrary.org/obo/IAO_0000429> \"ego@gmail.com\".\n<http://example.org/EBI09394092020> <http://semanticscience.org/resource/SIO_001324> \"+44(0)0124511234\".\n<http://example.org/EBI09394092020> <http://semanticscience.org/resource/SIO_000087> \"Elsevier Journal of Science\".\n<http://example.org/EBI09394092020> <http://purl.obolibrary.org/obo/NCIT_C19467> \"www.dcat.org\".\n<http://example.org/EBI09394092020> <http://semanticscience.org/resource/SIO_000181> \"Mathew\".\n<http://example.org/EBI09394092020> <http://purl.allotrope.org/ontologies/result#AFRL_0000403> \"Research Officer\".\n",
    
    "userJsonLDFile": "{\n  \"@context\" : {\n    \"@base\" : \"http://purl.org/ejp-rd/vocabulary/\",\n    \"@vocab\" : \"http://example.org/\",\n    \"ejp\" : \"./\",\n    \"rdf\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\",\n    \"rdfs\" : \"http://www.w3.org/2000/01/rdf-schema#\",\n    \"owl\" : \"http://www.w3.org/2002/07/owl#\",\n    \"xsd\" : \"http://www.w3.org/2001/XMLSchema#\",\n    \"dct\" : \"../../../dc/terms/\",\n    \"dc\" : \"../../../dc/elements/1.1/\",\n    \"dcat\" : \"http://www.w3.org/ns/dcat#\",\n    \"vcard\" : \"http://www.w3.org/ns/dcat#\",\n    \"foaf\" : \"http://xmlns.com/foaf/0.1/\",\n    \"afr\" : \"http://purl.allotrope.org/ontologies/role#\",\n    \"sio\" : \"http://semanticscience.org/resource/\",\n    \"iao\" : \"http://purl.obolibrary.org/obo/\",\n    \"hancestro\" : \"iao:HANCESTRO_\",\n    \"ncit\" : \"iao:NCIT_\",\n    \"so\" : \"iao:SO_\"\n  },\n  \"@graph\" : [ {\n    \"@id\" : \"http://example.org/EBI09394092020\",\n    \"@type\" : \"foaf:Person\",\n    \"Email Address\" : null,\n    \"Email address\" : null,\n    \"Last name\" : null,\n    \"Person\" : null,\n    \"Position\" : null,\n    \"Post\" : null,\n    \"contactPhoneNumber\" : null,\n    \"email Address\" : null,\n    \"emailAddress\" : null,\n    \"family_name\" : null,\n    \"first name\" : null,\n    \"label\" : null,\n    \"last name\" : null,\n    \"last_name\" : null,\n    \"lastname\" : null,\n    \"officeDesignation\" : null,\n    \"publication\" : null,\n    \"surname\" : null,\n    \"value\" : null,\n    \"workInfoHomepage\" : null,\n    \"http://purl.allotrope.org/ontologies/result#AFRL_0000403\" : \"Research Officer\",\n    \"iao:IAO_0000429\" : \"ego@gmail.com\",\n    \"ncit:C19467\" : \"www.dcat.org\",\n    \"sio:SIO_000087\" : \"Elsevier Journal of Science\",\n    \"sio:SIO_000181\" : \"Mathew\",\n    \"sio:SIO_000182\" : \"John\",\n    \"sio:SIO_001324\" : \"+44(0)0124511234\"\n  } ]\n}",
    
    "sheVal": "Validation is successful, the  http://a.example/boolean-true has shape <http://a.example/S-boolean> true"
}
```








