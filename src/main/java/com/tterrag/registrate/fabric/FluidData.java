package com.tterrag.registrate.fabric;

public record FluidData(String translationKey, int light) {

    public static class Builder {
        private String langKey;

        public Builder translationKey(String key) {
            this.langKey = key;
            return this;
        }

        public Builder luminosity(int light) {

            return this;
        }
    }

    public interface FluidAttributes {
        FluidData getData();

        void setData(FluidData data);
    }
}
