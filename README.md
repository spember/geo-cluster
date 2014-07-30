#Geographical KMeans

This small library is an implementation of KMeans clustering for geographical points, written in Groovy. It uses the [Haversine Formula][haversine] as its distance metric.

KMeans is both clever and powerful but it has two important things to think about: 1) which distance metric you use to calculate distance between points (e.g. x/y grid distance may not always be correct) and 2) how to optimize for _k_, the number of clusters. KMeans makes no assumption naively about your choice of _k_ and it may take several runs to find the 'correct' _k_ value for your domain. For example, one may run this algrothim several times to find the optimal _k_ where no cluster has locations more than 100 kilometers away from the center.


### Build

[Gradle](http://www.gradle.org) is used as the build tool. Run ```gradlew jar``` (or just ```gradle jar``` if gradle is installed on your system)to create the jar file, then include in your project.


### Usage
The basic approach is to pass a List of objects which implement the GeographicPoint interface to the ClusterService's ```cluster``` method, along with _k_, the number of clusters to create.

```
	List<GeographicPoint> points = [point1, point2,...]
	// cluster the points into a list
	List<Cluster> clusters = ClusterService.cluster(points, 10)
	clusters.each {cluster->
		println "Cluster has ${cluster.points.size()} points:"
		cluster.points.each {point->
			println "${point.latitude} / ${point.longitude}"
		}
	}

```

__Note__: the cluster algorithm is iterative and may take several lengthy iterations to converge. Additionally, if the initial clusters are generated randomly (as is the default), clusters are not guaranteed to contain the same points on each run.

### Potential Future work
As this is a first pass, it is extremely simplistic. There are several things I'd like to add, time permitting:

* An algorithm more accurate than Haversine. The earth, after all, is not a sphere but rather a non-uniform ellipse
* Increased Unit testing for different clustering scenarios
* Structure for setting latitude and longitude precision
* Framework for iteratively finding the optimal _k_ value based on some pluggable criteria (e.g. no clusters more than __X__ distance away from the center)


[kmeans]: http://en.wikipedia.org/wiki/K-means_clustering "K-Means"
[haversine]: http://en.wikipedia.org/wiki/Haversine_formula "Haversine Formula"