package pember.kmeans.geo

import spock.lang.Specification

/**
 * Created by steve on 7/29/14.
 */
class ClusterServiceSpec extends Specification {

    // create a quick private class; I didn't want to pollute the package with this trivial implementation
    private class Point implements GeographicPoint {
        BigDecimal latitude
        BigDecimal longitude
        @Override
        BigDecimal getLatitude() {
            this.latitude
        }

        @Override
        void setLatitude(BigDecimal latitude) {
            this.latitude = latitude
        }

        @Override
        BigDecimal getLongitude() {
            this.longitude
        }

        @Override
        void setLongitude(BigDecimal longitude) {
            this.longitude = longitude
        }
    }

    boolean containsCluster(List<Cluster>clusters, Set points) {
        boolean contains = false
        for (Cluster cluster: clusters) {
            if (cluster.points.equals(points)) {
                contains = true
            }
        }
        contains
    }


    void "Clustering, trivial"() {
        given:
        List points = [
                //2000 Route 38 Cherry Hill, NJ 8002.0                                 | 39.9385102 |  -75.0290682
                new Point(latitude: 39.9385102, longitude: -75.0290682),
                // 4840 Seminole Dr Cabazon, CA 92230.0                                 | 33.9194294 | -116.7704515
                new Point(latitude: 33.9194294, longitude: -116.7704515),
                // 5001 East Expressway 83 Mercedes, TX 78570.0                         | 26.1628092 |  -97.8879665
                new Point(latitude: 26.1628092, longitude: -97.8879665)
        ]

        when:
        List results = ClusterService.cluster(points, 3)

        then:
        results.size() == 3
        results.each {
            assert it.points.size() == 1
        }
    }

    void "Clustering, clear split"() {
        given:
        //1200 N Muldoon Rd, Ste E Anchorage, AK 99504                         | 61.2297853 | -149.7463111

        // 391 Merhar Ave Fairbanks, AK 99701                                   | 64.8568788 | -147.7029907

        // 400 Route 38 Moorestown, NJ 8057.0                                   | 39.9463408 |  -74.9630384

        //2000 Route 38 Cherry Hill, NJ 8002.0                                 | 39.9385102 |  -75.0290682
        Point point1 = new Point(latitude: 61.2297853, longitude: -149.7463111)
        Point point2 = new Point(latitude: 64.8568788, longitude: -147.7029907)
        Point point3 = new Point(latitude: 39.9463408, longitude: -74.9630384)
        Point point4 = new Point(latitude: 39.9385102, longitude: -75.0290682)
        // 7325 Woodhaven Blvd Glendale, NY 11385                               | 40.7104102 |  -73.8586204
        Point point5 = new Point(latitude: 40.7104102, longitude: -73.8586204 )
        List points = [
                point1, point2, point3, point4, point5
        ]

        when:
        List results = ClusterService.cluster(points, 2)

        then:
        results.size() == 2
        assert containsCluster(results, [point1, point2] as Set)
        assert containsCluster(results, [point3, point4, point5] as Set)



    }


}
