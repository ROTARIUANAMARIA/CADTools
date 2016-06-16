package org.openstreetmap.josm.plugins.cadtools;

public class Configuration {
    
    private static Configuration conf;
    private String cutCornersPercent;
    private String cutCornetsLenght;
    private String curCornersSegments;
    private String circleSegments;
    private String ellipseSegments;
    private String multiplyCircularCopies;
    
    private Configuration() {
        
    }
    
    public static Configuration getInstance() {
        
        if (conf == null) {
            conf = new Configuration();
            conf.setDefaultConfiguration();
        }
        return conf;        
    }
    
    private void setDefaultConfiguration() {
        cutCornersPercent = "20";
        curCornersSegments = "1";
    }

    public String getCutCornersPercent() {
        return cutCornersPercent;
    }

    public void setCutCornersPercent(String cutCornersPercent) {
        this.cutCornersPercent = cutCornersPercent;
    }

    public String getCutCornetsLenght() {
        return cutCornetsLenght;
    }

    public void setCutCornetsLenght(String cutCornetsLenght) {
        this.cutCornetsLenght = cutCornetsLenght;
    }

    public String getCurCornersSegments() {
        return curCornersSegments;
    }

    public void setCurCornersSegments(String curCornersSegments) {
        this.curCornersSegments = curCornersSegments;
    }

    public String getCircleSegments() {
        return circleSegments;
    }

    public void setCircleSegments(String circleSegments) {
        this.circleSegments = circleSegments;
    }

    public String getEllipseSegments() {
        return ellipseSegments;
    }

    public void setEllipseSegments(String ellipseSegments) {
        this.ellipseSegments = ellipseSegments;
    }

    public String getMultiplyCircularCopies() {
        return multiplyCircularCopies;
    }

    public void setMultiplyCircularCopies(String multiplyCircularCopies) {
        this.multiplyCircularCopies = multiplyCircularCopies;
    }
    
    

}
