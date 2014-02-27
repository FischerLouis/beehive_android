package com.beehive.activities;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.beehive.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StatisticsActivity extends Activity implements OnClickListener {

	//Chart test
	private GraphicalView mChart;
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private XYSeries mCurrentSeries;
    private XYSeriesRenderer mCurrentRenderer;
    
    private void initChart() {
        mCurrentSeries = new XYSeries("Weekly statistics");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(mCurrentRenderer);
        mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        mRenderer.setXAxisMin(0);
        mRenderer.setXAxisMax(6);
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(6);
    }

    private void addSampleData() {
        mCurrentSeries.add(1, 2);
        mCurrentSeries.add(2, 3);
        mCurrentSeries.add(3, 2);
        mCurrentSeries.add(4, 5);
        mCurrentSeries.add(5, 4);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		//Retrieve data from intent
		Intent intent = getIntent();
		String title = intent.getStringExtra("TITLE");
		String subtitle = intent.getStringExtra("SUBTITLE");
		//Retrieve views
		TextView titleView = (TextView)findViewById(R.id.title);
		TextView subtitleView = (TextView)findViewById(R.id.subtitle);
		LinearLayout footer = (LinearLayout)findViewById(R.id.footer_contribution);
		//Setting up data
		titleView.setText(title);
		subtitleView.setText(subtitle);
		//Setting up listener
		footer.setOnClickListener(this);
	}
	
	protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        if (mChart == null) {
            initChart();
            addSampleData();
            mChart = ChartFactory.getBarChartView(this, mDataset, mRenderer, Type.DEFAULT);
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }
    }

	@Override
	public void onClick(View view) {
		Toast.makeText(this, "Coming soon ...", Toast.LENGTH_SHORT).show();
	}


}
