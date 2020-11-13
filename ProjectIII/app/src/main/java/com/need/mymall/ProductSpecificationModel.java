package com.need.mymall;

import java.io.Serializable;

public class ProductSpecificationModel implements Serializable {

    private String featureName;
    private String featureValue;

    public ProductSpecificationModel(String featureName, String getFeatureValue) {
        this.featureName = featureName;
        this.featureValue = getFeatureValue;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }
}
