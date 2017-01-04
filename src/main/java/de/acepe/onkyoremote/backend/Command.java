package de.acepe.onkyoremote.backend;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Command {
    private String displayKey;
    private String code;

    public Command(String displayKey, String code) {
        this.displayKey = displayKey;
        this.code = code;
    }


    public String getDisplayKey() {
        return displayKey;
    }

    public void setDisplayKey(String displayKey) {
        this.displayKey = displayKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("displayKey", displayKey).toString();
    }
}
