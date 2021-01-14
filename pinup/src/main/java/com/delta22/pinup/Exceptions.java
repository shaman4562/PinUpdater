package com.delta22.pinup;

public class Exceptions {

    public static class ReadConfigException extends Exception{

        public ReadConfigException(String s) {
            super(s);
        }
    }

    public static class UsingOldCertificateException extends Exception{

        public UsingOldCertificateException(String domainName) {
            super("Can't use old certificate for " + domainName + "!");
        }
    }

    public static class ExpiredCertificateException extends Exception{

        public ExpiredCertificateException(String domainName) {
            super("Certificate for " + domainName + " is expired!");
        }
    }

    public static class NoSuchPinException extends Exception{

        public NoSuchPinException(String domainName) {
            super("Pin for " + domainName +" not found in database!");
        }
    }

    public static class NoSuchDomainException extends Exception{

        public NoSuchDomainException(String domainName) {
            super("Domain for " + domainName +" not found in database!");
        }
    }

    public static class RequestDomainException extends Exception{

        public RequestDomainException(String domainName, String message) {
            super("Error while request domain for " + domainName +" : " + message);
        }
    }
}
