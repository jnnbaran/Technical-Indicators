package com.example.data;

import java.math.BigDecimal;
import java.util.Date;

public class Record {
    private String ticker;
    private Date date;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Integer vol;

    public static class Builder {
        private String ticker;
        private Date date;
        private BigDecimal open;
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal close;
        private Integer vol;

        public Builder ticker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder open(BigDecimal open) {
            this.open = open;
            return this;
        }

        public Builder high(BigDecimal high) {
            this.high = high;
            return this;
        }

        public Builder low(BigDecimal low) {
            this.low = low;
            return this;
        }

        public Builder close(BigDecimal close) {
            this.close = close;
            return this;
        }

        public Builder vol(Integer vol) {
            this.vol = vol;
            return this;
        }

        public Record build() {
            return new Record(this);
        }
    }

    private Record(Builder builder) {
        this.ticker = builder.ticker;
        this.date = builder.date;
        this.open = builder.open;
        this.high = builder.high;
        this.low = builder.low;
        this.close = builder.close;
        this.vol = builder.vol;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public Integer getVol() {
        return vol;
    }

    public void setVol(Integer vol) {
        this.vol = vol;
    }

    @Override
    public String toString() {
        return "com.example.reader.Record{" +
                "ticker='" + ticker + '\'' +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", vol=" + vol +
                '}';
    }
}
