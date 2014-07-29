package pember.kmeans.geo

import groovy.transform.CompileStatic

/**
 * Created by steve on 6/30/14.
 */
@CompileStatic
class Cluster implements GeographicPoint {

    BigDecimal latitude
    BigDecimal longitude

    Set<GeographicPoint> points

    public Cluster() {
        clearPoints()
    }

    public void clearPoints() {
        points = new HashSet<>()
    }

    public String toString() {
        "Cluster: $latitude, $longitude"
    }

    public Cluster clone() {
        Cluster clone2 = new Cluster(latitude: this.latitude, longitude: this.longitude)
        clone2.points = points.collect() as Set
        clone2
    }

    @Override
    BigDecimal getLatitude() {
        return latitude
    }

    @Override
    void setLatitude(BigDecimal latitude) {
        this.latitude = latitude
    }

    @Override
    BigDecimal getLongitude() {
        return longitude
    }

    @Override
    void setLongitude(BigDecimal longitude) {
        this.longitude = longitude
    }
}