# GateIn Data Injector

GateIn Data Injector defines service components used for generating dataset. Those services are exposed as JMX beans
and REST endpoint. Using GateIn Data Injector, testers are capable of building big test data via

- JConsole

- GateIn Management Gadget

- JMeter

# How to deploy

- Run _mvn clean install_ from the root directory of project

- Copy _datainject-core_ artifact under core/target to _lib_ directory of GateIn's Tomcat packaging

# Usage

Service components are exoposed as JMX beans and REST endpoints. Users could interact with them in two ways

 - Via JConsole (under exo JMX beans collection)

 - Via GateIn Management Gadget, which is displayed in http://localhost:8080/portal/g/:platform:administrators/administration/servicesManagement

## Manipulate navigation data

### Create new navigations with page nodes

Method: createNavs

Params: navType, navOwner, prefix, startIndex, endIndex

Ex: Invoking createNavs with navType=portal, navOwner=testClassic, prefix=testNode, startIndex=0, endIndex=100 would create a new
navigation named _testClassic_ of type _portal_ and inject 100 children named (testNode_0, testNode_1,..., testNode_99) to navigation's root node

### Insert nodes into root node of existing navigation

Method: insertNodes

Params: navType, navOwner, prefix, startIndex, endIndex

Ex: Invoking insertNodes with navType=portal, navOwner=classic, prefix=testNode, startIndex=0, endIndex=100 would inject 100 children named (testNode_0, testNode_1,..., testNode_99) to root
node of navigation determined by type=portal, owner=classic

### Insert nodes into node specified by path from root node of existing navigation

Method: insertNodes

Params: navType, navOwner, absolutePath, prefix, startIndex, endIndex

Ex: Invoking insertNodes with navType=portal, navOwner=classic, absolutePath=level_0/level_1, prefix=testNode, startIndex=0, endIndex=100 would inject 100 children named (testNode_0, testNode_1,..., testNode_99) to
node specified by path level_0/level_1 under navigation (type=portal, owner=classic)

### Delete node specified by path from root node of existing navigation

Method: deleteNode

Params: navType, navOwner, absolutePath

Ex: Invoking deleteNode with navType=portal, navOwner=classic, pathFromRoot=level_0/level_1 would delete node specified by path level_0/level_1 under navigation (type=portal, owner=classic)


## Manipulate page data


## Manipulate organization data

