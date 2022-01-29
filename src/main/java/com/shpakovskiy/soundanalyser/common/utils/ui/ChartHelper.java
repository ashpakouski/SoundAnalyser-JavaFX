package com.shpakovskiy.soundanalyser.common.utils.ui;

import javafx.scene.chart.XYChart;

//TODO: Think of better name
public class ChartHelper {
    public static XYChart.Series<Number, Number> convertToLineChartSeries(int[] chartValues) {
        return convertToLineChartSeries(chartValues, null);
    }

    public static XYChart.Series<Number, Number> convertToLineChartSeries(int[] chartValues, String legendString) {
        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();

        chartSeries.setName(legendString);

        for (int i = 0; i < chartValues.length; i++) {
            XYChart.Data<Number, Number> chartData = new XYChart.Data<>(i, chartValues[i]);
            chartSeries.getData().add(chartData);
        }

        return chartSeries;
    }

    public static XYChart.Series<String, Number> convertToBarChartSeries(double[] chartValues) {
        return convertToBarChartSeries(chartValues, null);
    }

    public static XYChart.Series<String, Number> convertToBarChartSeries(double[] chartValues, String legendString) {
        XYChart.Series<String, Number> chartSeries = new XYChart.Series<>();

        chartSeries.setName(legendString);

        for (int i = 0; i < chartValues.length; i++) {
            XYChart.Data<String, Number> chartData = new XYChart.Data<>(String.valueOf(i), chartValues[i]);
            chartSeries.getData().add(chartData);
        }

        return chartSeries;
    }
}