Core API Preview
================

What is this about?
------------------
This is a CORE API Preview for the CRISMA ICMS infrastructure building block. With this API other components 
can get infos about worldstates, about manipulation UI's and federated models.

You can find the model "behind" the api either in this document(Link zu Pascals UML Dokument) (or via calling ```/classe?domain=crisma``` ;-))

We set up a cids System that implements(besserer Begriff finden) that model.

We implemented a restful API and tried to fulfill the rules SP3 set.

The API is enriched by a cool documentation framework named Swagger. With this interactive API documentation 
you can explore the API and play around with the resources and methods.

What this is not about
-----------------------
The Swagger Site is not a UI for CRISMA. 
The API is a core API. There will be some helpers to support CRISMA developers in their work. At the moment we are not sure if these helpers are
a more client side component or if we enhance the API for this purpose.

The API is divided in different Sub APIs.
=========================================

What are the most important API parts?
--------------------------------------
![bildschirmfoto 2013-07-03 um 23 15 10](https://f.cloud.github.com/assets/837211/746167/ab707b34-e425-11e2-9dfa-813ee90288df.png)
In the classes sub API one can get information about the classes (schemas) of the stored entities.

![bildschirmfoto 2013-07-03 um 23 15 36](https://f.cloud.github.com/assets/837211/746169/c680273a-e425-11e2-87aa-f76162cb16ac.png)
In the entities sub API one get, modify, create and delete entities that are stored in the system

![bildschirmfoto 2013-07-03 um 23 16 20](https://f.cloud.github.com/assets/837211/746177/edb62d72-e425-11e2-9f03-00e7306b40a9.png)
In the actions sub API one can execute actions, retrieve the results and cancel long running tasks.

And the other ones?
-------------------
Well, since this is a preview the other sub APIs are not (fully) availlable yet. But here comes a quick overview 
what you can expect in the near future.

* configattributes: store an retrieve user/role based configuration paramters
* nodes: retrieve information about a custom structure (so called node structure) of the system
* permission: retrieve the user/role based permissions
* searches: execute custom searches and retrieve the results
* subscriptions: subscribe to certain properties and get noticed when something changes
* users: validate a user and get her roles

Limitations
============

There are several limitations of the API due to the early development phase.

* Authorization feature is disabled for the demo.
* The ```role``` parameter will be ignored.
* Collection ressources will be delivered completely (no ```limit``` and ```offset``` parameter functionality).
* No proper error codes.
* Only one domain (```crisma```) is queryable.
* The ```version``` parameter will be ignored.
* The ```expand``` parameter will be ignored (All objects will be delivered as a whole).
* The ```level``` parameter will be ignored.
* The ```profile``` parameter will be ignored.
* The ```fields``` parameter will only work in the top level of the object.
* No automatic key generation. You'll have to submit a proper (primary) key when you create an object.
* No partial updates

Experiments
===========

Feel free to try out the API Demo. You can either use the provided Swagger Page, your browser or your favourite commandline tool (curl, resty, ...)
Please delete only the objects that you created and keep in mind that we eill reset the demo data once in a while.

Examples
========

Get all classes of the crisma API demo simulation case:
-------------------------------------------------------

Browser:

![bildschirmfoto 2013-07-03 um 23 46 07](https://f.cloud.github.com/assets/837211/746304/02da1138-e42a-11e2-8cab-c3512c5f1650.png)

Swagger:

1. Go to the Swagger API Documentation Page
2. The API Description URL should point to: http://flexo.cismet.de:8890/resources
3. Open the classes sub API section
4. Open the GET /classes ressource documentation
5. Hit the [Try it out!] Button

![bildschirmfoto 2013-07-03 um 23 47 22](https://f.cloud.github.com/assets/837211/746306/300abe0a-e42a-11e2-8241-6b4daf1c8ef6.png)

Get the description of the crisma.categories
--------------------------------------------
With curl:
```bash
curl http://localhost:8890/classes/crisma.categories
```

Result:
```json
{
  "$self": "/crisma.categories",
  "configuration": {
    "name": "Categories",
    "policy": "STANDARD",
    "attributePolicy": "STANDARD",
    "pK_Field": "id"
  },
  "attributes": {
    "id": {
      "$self": "/crisma.categories/id",
      "name": "id",
      "position": 0,
      "javaclassname": "java.lang.Integer"
    },
    "key": {
      "$self": "/crisma.categories/key",
      "visible": true,
      "name": "key",
      "position": 1,
      "javaclassname": "java.lang.String"
    },
    "classification": {
      "$self": "/crisma.categories/classification",
      "visible": true,
      "optional": true,
      "name": "classification",
      "position": 2,
      "foreignKey": true,
      "referenceType": "/crisma.classifications"
    }
  }
}
```



Get the description of a certain attribute
----------------
With Swagger:
![bildschirmfoto 2013-07-04 um 13 23 56](https://f.cloud.github.com/assets/837211/749037/4c564666-e49c-11e2-85d7-dd1241d3e9c0.png)

Result:
```json
{
  "$self": "/crisma.categories/key",
  "visible": true,
  "name": "key",
  "position": 1,
  "javaclassname": "java.lang.String"
}
```

Get all categories
-------------------
With curl:
```bash
curl http://localhost:8890/crisma.categories
```


Get a certain category
----------------------
With curl again:
```bash
curl http://localhost:8890/crisma.categories/1
```

Result:
```json
{
  "$self": "/crisma.categories/1",
  "id": 1,
  "key": "critical_infrastructure",
  "classification": {
    "$self": "/crisma.classifications/1",
    "id": 1,
    "key": "worldstate_detail_component"
  }
}
```

Update a category
------------------
With Swagger:
![bildschirmfoto 2013-07-04 um 13 30 06](https://f.cloud.github.com/assets/837211/749058/201c2f1a-e49d-11e2-8e54-1017d139a5dd.png)

Result:
```json
{
  "$self": "/crisma.categories/1",
  "id": 1,
  "key": "critical_infrastructureXXX",
  "classification": {
    "$self": "/crisma.classifications/1",
    "id": 1,
    "key": "worldstate_detail_component"
  }
}
```

Add a classification
--------------
With Swagger:
![bildschirmfoto 2013-07-04 um 13 34 19](https://f.cloud.github.com/assets/837211/749076/c0d12d66-e49d-11e2-907a-ad9161931f96.png)

Result:
```json
{
  "$self": "/crisma.classifications/6",
  "id": 6,
  "key": "DIY classification"
}
```

Delete a classification
------------------
With curl again:
```bash
curl -X DELETE http://localhost:8890/crisma.classifications/6
```
Result:

none :-)

![bildschirmfoto 2013-07-04 um 13 40 49](https://f.cloud.github.com/assets/837211/749103/af61b2ca-e49e-11e2-8cb6-5e2d8bb7dc72.png)

Get all categories with a certain classification key and omit all properties with null values in the result
----------------------------------------------------
With curl again:
```bash
curl http://localhost:8890/crisma.categories?filter=classification.key:worldstate.*&omitNullValues=true
```

Execute an action with parameters
---------------------------------
With Swagger:
![bildschirmfoto 2013-07-04 um 13 48 59](https://f.cloud.github.com/assets/837211/749131/c35da85a-e49f-11e2-90f2-31f2cca12581.png)

Result:
```json
{
  "key": "1372938604952",
  "actionKey": "banner",
  "description": null,
  "parameters": {
    "$message": "What a great tool"
  },
  "status": "STARTING"
}
```

List the tasks of an action
-----------------------------
With curl:
```bash
curl  http://localhost:8890/actions/crisma.calculate/tasks
```

Result:
```json
{
  "$self": "http://localhost:8890/actions/crisma.calculate/tasks",
  "$offset": 0,
  "$limit": 100,
  "$first": "http://localhost:8890/actions/crisma.calculate/tasks",
  "$previous": null,
  "$next": "not available",
  "$last": "not available",
  "$collection": [
    {
      "key": "1372364267827",
      "actionKey": "calculate",
      "description": null,
      "parameters": {
        "$": "49/23*7-5"
      },
      "status": "FINISHED"
    }
  ]
}
```


List the results of a task
--------------------------
With curl again:
```bash
curl  http://localhost:8890/actions/crisma.calculate/tasks/1372364267827/results
```

Result:
```json
{
  "$self": "http://localhost:8890/actions/crisma.calculate/tasks/1372364267827/results",
  "$offset": 0,
  "$limit": 100,
  "$first": "http://localhost:8890/actions/crisma.calculate/tasks/1372364267827/results",
  "$previous": null,
  "$next": "not available",
  "$last": "not available",
  "$collection": [
    {
      "key": "stdout",
      "name": "Result: stdout",
      "description": null,
      "contentType": "text/plain",
      "additionalInfo": {}
    }
  ]
}
```

Get a certain result of a task
---------------------------
With Swagger:
![bildschirmfoto 2013-07-04 um 13 56 52](https://f.cloud.github.com/assets/837211/749157/dd3f3e68-e4a0-11e2-83b2-7410b6d0d868.png)

Result:

```
9.91304347826087
```

Play with it and find out more :-). Have fun. :-)



