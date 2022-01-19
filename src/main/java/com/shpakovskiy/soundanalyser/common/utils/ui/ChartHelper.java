package com.shpakovskiy.soundanalyser.common.utils.ui;

import javafx.scene.chart.XYChart;

//TODO: Think of better name
public class ChartHelper {
    public static XYChart.Series<Number, Number> convertToChartSeries(int[] chartValues) {
        return convertToChartSeries(chartValues, null);
    }

    public static XYChart.Series<Number, Number> convertToChartSeries(int[] chartValues, String legendString) {
        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();

        chartSeries.setName(legendString);

        for (int i = 0; i < chartValues.length; i++) {
            XYChart.Data<Number, Number> chartData = new XYChart.Data<>(i, chartValues[i]);
            chartSeries.getData().add(chartData);
        }

        return chartSeries;
    }
}