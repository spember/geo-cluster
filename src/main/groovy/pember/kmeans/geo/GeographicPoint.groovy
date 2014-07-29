package pember.kmeans.geo

import groovy.transform.CompileStatic

/**
 * Created by steve on 7/28/14.
 */
@CompileStatic
public interface GeographicPoint {
    public BigDecimal getLatitude()
    public void setLatitude(BigDecimal latitude)
    public BigDecimal getLongitude()
    public void setLongitude(BigDecimal longitude)
}