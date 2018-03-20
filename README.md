[![Build Status](https://api.travis-ci.org/symbiote-h2020/SemanticMapping.svg?branch=staging)](https://api.travis-ci.org/symbiote-h2020/SemanticMapping)
[![](https://jitpack.io/v/symbiote-h2020/SemanticMapping.svg)](https://jitpack.io/#symbiote-h2020/SemanticMapping)
[![codecov.io](https://codecov.io/github/symbiote-h2020/SemanticMapping/branch/staging/graph/badge.svg)](https://codecov.io/github/symbiote-h2020/SemanticMapping)
# Semantic Mapping
This repository contains common SymbIoTe libraries used throughout different components and different layers. Its main use is to standardize classes which will be used for communication among components. Furthermore, it also contains security packages to facilitate secure access to resources (e.g. *SecurityHandler*).
## How to include them in your code
[Jitpack](https://jitpack.io/) can be used to easily import Semantic Mapping in your code. In Jitpack's website you can find guidelines about how to include repositories for different build automation systems. In the symbIoTe project which utilizes [gradle](https://gradle.org/), developers have to add the following in the *build.gradle*:

1. Add jitpack in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. Add the dependency:
```
compile('com.github.symbiote-h2020:SemanticMapping:develop-SNAPSHOT')
```
As you notice above, during development (i.e. feature and develop branches of component repositories) the ***develop*** branch of the SemanticMapping needs to be used, in order to make sure that the latest version is always retrieved. In the official releases (i.e. master branches of Component repositories), this dependecy will be changed to:

```
compile('com.github.symbiote-h2020:SemanticMapping:{tag}')
```
by the **SymbIoTe integrators**.
