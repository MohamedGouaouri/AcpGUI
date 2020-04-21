package kernel;

import java.util.Objects;

public class Performance {
    private double percentage;
    private double threshold;
    private double recognized;
    private double confusion;
    private double rejected;

    public Performance(double percentage, double threshold, double recognized, double confusion, double rejected) {
        this.percentage = percentage;
        this.threshold = threshold;
        this.recognized = recognized;
        this.confusion = confusion;
        this.rejected = rejected;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getRecognized() {
        return recognized;
    }

    public void setRecognized(double recognized) {
        this.recognized = recognized;
    }

    public double getConfusion() {
        return confusion;
    }

    public void setConfusion(double confusion) {
        this.confusion = confusion;
    }

    public double getRejected() {
        return rejected;
    }

    public void setRejected(double rejected) {
        this.rejected = rejected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Performance that = (Performance) o;
        return Double.compare(that.getPercentage(), getPercentage()) == 0 &&
                getThreshold() == that.getThreshold() &&
                getRecognized() == that.getRecognized() &&
                getConfusion() == that.getConfusion() &&
                getRejected() == that.getRejected();
    }
}
