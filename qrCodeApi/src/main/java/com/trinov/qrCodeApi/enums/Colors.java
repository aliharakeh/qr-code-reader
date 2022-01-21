package com.trinov.qrCodeApi.enums;

public enum Colors {

    BLUE(0xFF0000FF),
    YELLOW(0xFFFFFF00),
    ORANGE(0xFFFF8000),
    WHITE(0xFFFFFFFF),
    BLACK(0xFF000000);

    private final int argb;

    Colors(final int argb) {
        this.argb = argb;
    }

    public int getArgb() {
        return argb;
    }
}
