tHelloWorldMaven
=============
This is essentially a Talend Open Studio skeleton component to be used as a starting point to whom which want to develop their components in a Maven lifecycle environment

This 'chuck was made to you by [Gabriele Baldassarre](http://gabrielebaldassarre.com) and you can click the link for more detailed docs, tutorial on how to use and support requests.

## Why to use Maven in TOS customer components development

There are a lots of reasons to use a maven life-cycle build management system even in TOS components development. Here's what Maven can do for you:

* It automates repetitive tasks
* It automatically manages dependencies and third party libraries
* It automatically patches XML component descriptors (ie. mantains <IMPORTS> elements in component descriptor file. No need to add them manually anymore!)
* Allows you to build testsuite using junit in a hassle
* It speeds up build and deploy process to users' custom-component folders
* It's more robust than "mee-manually-edit-javajet" and "mee-copy-somewhere" approach


## Configure the template
This is a 20-seconds-quick-start-guide.

To enter in the magic world of maven-managed component creation, just clone this repository and start playing. Out-of-the-box, this is just a dummy component that prints out an hello world to the console, but it's maven-ready and it's ready to be adapted to your needs.

Please don't forget to rename all mandatory resources file (*.javajet files, png icon, message files...) using the target component name prefix (follow [this tutorial](http://www.powerupbi.com/talend/componentCreation_3.html) if you have no idea of what I'm talking about). These files are stored in src/main/resources.


### The pow.xml file
The provided pow file needs minimal editing before being usable. You obviously need to change at least *artifactID* and *name* with target component name.

You may want to [change dependencies](http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html) and add repositories following your needs. The TOS component XML descriptor file will be patched to include automatically all your declared  dependencies.

Finally, you may want to change some properties to new default values, like *component-author* or *componentsFolder*. However, these properties can be also set in command line and they will be explained later in this file.


### Extra JAR
Most TOS components are just made of 1 to 3 javajet files and a bunch of external dependencies. But sometimes, you may want to minimize inline javajet code (difficult to write and debug) and take part of component logic in an external JAR, instead. For example, you could decide to build a *client* that enacpsulate most logic inside it and that implements Iterator, then just instantiate and loop while inside the javajet code.

This can be done. Just add your classes inside src/main/java as you would code an ordinary java app. A jar with the name of the artifact will be automatically packaged and XML patched accordingly.

## Build the component
To build the component, open a command line in the component basedir  and give:

    mvn clean install

This will package the component and copy it in your TOS custom components folder (default to $HOME$/talend_components)

If you want to specify a different custom component location, issue the command:

     mvn clean install -DcomponentsFolder=/path/to/your/tos_custom_components

If you want to package the component in the basedire but **not** deploy it in custom component folder, issue the command:

    mvn clean package
     

## Command-line parameters
You can set some parameters in command-line using the -D flag. Here's a list:

* **skipTests** (default: true) to skip/unskip the test phase
* **component-author** (default: Gabriele Baldassarre...well...It's me) to set the component author in XML descriptor
* **addMavenDescription** (default: true) to add/cut the META-INF/maven stuff in client JAR
* **dependencyVersion** (default: true) to add/cut the version number suffix to all external dependencies
* **useSnapshot** (default: true) to use or not snaphsot releases in all external dependencies
* **componentsFolder** (default: $HOME$/talend_components) path to your installation TOS custom component folder   


