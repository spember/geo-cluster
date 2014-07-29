package pember.kmeans.geo


import org.apache.log4j.Logger

/**
 * Created by steve on 7/29/14.
 */
class ClusterService {

    private static final Logger log = Logger.getLogger(getClass())

    // radius of the earth (approx), in meters
    public static final double R = 6372.8 * 1000;

    /**
     * Uses the KMeans algorithm to generate k clusters from the set of {@link GeographicPoint}s. Will initialize
     * with a random set of k {@ Cluster}s
     *
     * @param points a List of {@link GeographicPoint}s
     * @param k
     * @return
     */
    public static List<Cluster> cluster(List<GeographicPoint> points, int k) {
        cluster(points, k, buildRandomInitialClusters(points, k))
    }

    /**
     * Uses the KMeans algorithm to generate k clusters from the set of points using a predefined starting set of
     * {@link Cluster}
     *
     * @param points a List of {@link GeographicPoint}
     * @param k
     * @param clusters
     * @return
     */
    public static List<Cluster> cluster(List<GeographicPoint> points, int k, List<Cluster> clusters) {
        // adapted from http://datasciencelab.wordpress.com/2013/12/12/clustering-with-k-means-in-python/
        // Uses KMeans to converge the clusters on the points
        // Kmeans is an iterative, two step apporach:
        // 1. Assign each point to the closest centroid
        // 2. Recalculate each centroid to be the center of its assigned points
        // The algorithm is initialized with k random Centroids.
        // create empty old clusters
        List<Cluster> oldClusters = []
        (1..k).each {
            oldClusters << new Cluster()
        }
        int i = 0
        while (!hasConverged(oldClusters, clusters)) {
            log.debug("On iteration $i")
            i++
            // grr... cloning in java / groovy is a bit tricky when it comes to lists
            // they're copied by reference, so in order for the converged
            oldClusters = clusters.collect {it.clone()}
            clusters = assignPointsToClusters points, clusters
            clusters = adjustClusterCenters clusters
        }
        clusters
    }

    // support methods for clustering

    /**
     * Calculates the approximate distance between two geographic points on the earth. The earth is not a globe / sphere;
     * it is a very rough, slightly amorphous ellipsoid. We use the haversine formula in order to calculate an approximate distance
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // taken from http://rosettacode.org/wiki/Haversine_formula#Java
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        // R is the radius of the earth, set above statically
        return R * c;
    }

    public static double haversineDistance(GeographicPoint point1, GeographicPoint point2) {
        haversineDistance(point1.latitude.toDouble(), point1.longitude.toDouble(), point2.latitude.toDouble(), point2.longitude.toDouble())
    }

    /**
     * Used to generate some random initial {@link Cluster}s
     *
     * @param points
     * @param k
     * @return
     */
    protected static List<Cluster> buildRandomInitialClusters(List<GeographicPoint> points, int k) {
        // shuffle the points randomly, then use the first k as clusters
        Collections.shuffle points
        List<Cluster> clusters = []
        (1..k).each {
            clusters << new Cluster(latitude: points[it-1].latitude, longitude: points[it-1].longitude)
        }
        // reshuffle to avoid putting the points into the cluster they generated?
        Collections.shuffle points
        int i = 0
        // distribute each random point to a cluster
        for (GeographicPoint point : points) {
            clusters[i].points.add(point)
            i++
            if (i == k) {
                i = 0
            }
        }
        clusters
    }

    /**
     * Distributes each point to the most appropriate {@link Cluster}, in this case, the geographic closest according to
     * the Haversine distance
     *
     * @param points
     * @param clusters
     * @return
     */
    private static List<Cluster> assignPointsToClusters(List<GeographicPoint> points, List<Cluster> clusters) {
        // for each point, find the cluster with the closest center
        clusters.each {cluster->
            cluster.clearPoints()
        }

        for (GeographicPoint point: points) {
            Cluster current = clusters[0]
            for (Cluster cluster: clusters) {
                if (haversineDistance(point, cluster) < haversineDistance(point, current)) {
                    current = cluster
                }
            }
            log.debug("Adding ${point} to $current")
            current.points.add point
        }
        clusters
    }

    /**
     * Re-adjusts the center of each {@link Cluster} based on the current {@link GeographicPoint}s assigned to it
     *
     * @param clusters
     */
    private static List<Cluster> adjustClusterCenters(List<Cluster> clusters) {
        // calculate the mean of all lats and longs
        for (Cluster cluster : clusters) {
            if (cluster.points.size() > 0) {
                cluster.latitude = cluster.points.sum {it.latitude} / cluster.points.size()
                cluster.longitude = cluster.points.sum {it.longitude} / cluster.points.size()
            }
        }
        clusters
    }

    /**
     * Determines if the {@link Cluster}s have converged. This is done by comparing the contents of the current cluster
     * iteration and the previous to see if any {@link GeographicPoint}s have moved Clusters. If no movement has occured,
     * we assume the clusters are stable
     *
     * @param previous
     * @param current
     * @return
     */
    private static boolean hasConverged(List<Cluster> previous, List<Cluster> current) {
        if (previous.size() != current.size()) {
            throw new IllegalArgumentException("Clusters must be the same size")

        }
        for (int i = 0; i < previous.size(); i++) {
            if (!previous[i].points.equals(current[i].points)) {
                return false
            }
        }
        log.debug("Converged!")
        true
    }
}