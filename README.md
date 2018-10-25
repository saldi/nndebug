## NnDebug
Small tool to illustrate:
####How to Neural Network works

`
Tip: Works only in Java 8 because in Java 10 Oracle remove JavaFx.
`

###Usage:
After initialize neural network add NetListener::

```java
multiLayerNetwork.setListeners(NetListenerBuilder.Builder()
		.name("AppName")
		.data(multiLayerConfiguration, multiLayerNetwork, trainDataSet, testDataSet)
		.build()
);
```

###App window:
![NnDebug app window](https://raw.githubusercontent.com/shadoq/nndebug/master/_doc/img/d1.PNG)


