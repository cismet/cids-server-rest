Dies ist ein temporäres Projekt. Wenn das Design abgeschlossen ist, werden wahrscheinlich mehrere draus. Im Moment wird es nur für die CRISMA API Demo verwendet, deswegen hier die CRISMA API Demo Readme:

Core API Preview
================

What is this about
------------------
This is a CORE API Preview for the CRISMA ICMS. 

<cids Bezug> 

What this is not
----------------

<kein Helper> 

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

Get the description of a certain attribute
----------------

Get all categories
-------------------


Get a certain category
----------------------

Update a category
------------------

Add a category
--------------


Delete a category
------------------


Get all categories with a certain classification key
----------------------------------------------------

Execute an action
-----------------

Execute an action with parameters
---------------------------------

Cancel a task
-------------

Get the result of an action
---------------------------






