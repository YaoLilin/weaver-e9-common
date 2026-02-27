package com.customization.yll.common.exception;

public class ActionConfigException extends ConfigurationException implements FrontErrorMessage{
    public ActionConfigException() {
    }

    public ActionConfigException(String message) {
        super(message);
    }
}
