# GenMyModel Eclipse Plugins

You can find in this repository two plugins used to manage your GenMyModel project and compile/execute your GenMyModel custom generators from eclipse. These plugins can help you to easily develop your own custom generator and perform some manipulation on your models. Once your custom generator is developed and you are happy with it, you can push it in github and enjoy it at any time, either from GenMyModel or from eclipse using these plugins.

Eclipse supported version >= Kepler (the plugin had been successfully tested on eclipse Kepler and eclipse Luna).


### CustomGen Development Kit Features

The current features are supported:
* New custom generator creation in your eclipse environment
* Custom generator compilation (Acceleo script compilation and error report)
* Custom generator execution via run configurations

Planned features:
* Deployment in github repository
* Intermediate metamodels (auto) registration
* GenMyModel QVTo libraries

### GenMyModel Project Explorer Features

The current features are supported:
* Multi-accounts management (Addition/Suppression from Eclipse, not GenMyModel plateform)
* Projects visibility modification
* Projects deletion
* GenMyModel protocol "genmyodel://_projectID_". This feature allows you to manipulate GenMyModel models into Eclipse EMF applications (in read only mode)
* Model opening in default UML/EMF tree viewer (in read only mode, model loading take time, will be profiled for next version)
* Code generation using registered custom generators in your workspace
* Manage your custom generator (register/modify/delete them)

Planned features:
* Project import/export from XMI (using drag/drop)
* Model modifications


## Installation

__EMF is required__ by the `org.genmymodel.plugin.resource` plugin. It can be installed from the eclipse update site. Acceleo eclipse plugin is not required, but you should install it if you want to code your generator in good conditions.

1. Get the plugin jars (In order to get the compiled jars of the plugins, you can either build them from the source, or you can download them in the [release](https://github.com/Axellience/gmm-eclipse-plugins/releases) section).
1. Copy the downloaded jars in the `plugins` directory of your elipse installation.
1. Restart your eclipse if needed.

## Build

* Using eclipse, import the projects (as maven projects), then simply export them as plugin.

* Using maven, simply run:
```
$ mvn clean package
```

## Restrictions

Currently, you cannot modify your model using EMF tools. You can only read them (_e.g._, you can use one of your GenMyModel models as QVTo transformation input). 

