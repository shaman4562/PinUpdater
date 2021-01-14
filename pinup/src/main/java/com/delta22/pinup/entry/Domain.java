package com.delta22.pinup.entry;

public class Domain {
    private int id;
    private String domain;
    private String pin;
    private int certExpiration;
    private int pinExpiration;
    private int checkPeriod;
    private int usingAfterExpiration;
    private int disablePin;

    public Domain() {
    }

    public Domain(int id) {
        this.id = id;
    }

    public Domain(int id, String domain, String pin, int certExpiration, int pinExpiration, int checkPeriod, int usingAfterExpiration, int disablePin) {
        this.id = id;
        this.domain = domain;
        this.pin = pin;
        this.certExpiration = certExpiration;
        this.pinExpiration = pinExpiration;
        this.checkPeriod = checkPeriod;
        this.usingAfterExpiration = usingAfterExpiration;
        this.disablePin = disablePin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDomainName() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getCertExpiration() {
        return certExpiration;
    }

    public void setCertExpiration(int certExpiration) {
        this.certExpiration = certExpiration;
    }

    public int getPinExpiration() {
        return pinExpiration;
    }

    public void setPinExpiration(int pinExpiration) {
        if (pinExpiration == 0) {
            this.pinExpiration = this.certExpiration;
        }
        this.pinExpiration = pinExpiration;
    }

    public int getCheckPeriod() {
        return checkPeriod;
    }

    public void setCheckPeriod(int checkPeriod) {
        this.checkPeriod = checkPeriod;
    }

    public int getUsingAfterExpiration() {
        return usingAfterExpiration;
    }

    public void setUsingAfterExpiration(int usingAfterExpiration) {
        this.usingAfterExpiration = usingAfterExpiration;
    }

    public int getDisablePin() {
        return disablePin;
    }

    public void setDisablePin(int disablePin) {
        this.disablePin = disablePin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Domain)) return false;
        Domain domain1 = (Domain) o;
        return id == domain1.id &&
                certExpiration == domain1.certExpiration &&
                pinExpiration == domain1.pinExpiration &&
                checkPeriod == domain1.checkPeriod &&
                usingAfterExpiration == domain1.usingAfterExpiration &&
                disablePin == domain1.disablePin &&
                domain.equals(domain1.domain) &&
                pin.equals(domain1.pin);
    }

    @Override
    public int hashCode() {
        int result = pin.hashCode();
        result = 17 * result + id + certExpiration + pinExpiration + checkPeriod;
        return result;
    }

    @Override
    public String toString() {
        return "Domain{" +
                "id=" + id +
                ", domain='" + domain + '\'' +
                ", pin='" + pin + '\'' +
                ", certExpiration='" + certExpiration + '\'' +
                ", pinExpiration='" + pinExpiration + '\'' +
                ", checkPeriod='" + checkPeriod + '\'' +
                ", usingAfterExpiration=" + usingAfterExpiration +
                ", disablePin=" + disablePin +
                '}';
    }
}
