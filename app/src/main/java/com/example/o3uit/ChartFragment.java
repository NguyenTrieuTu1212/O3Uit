package com.example.o3uit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.o3uit.Chart.DataMarkerView;
import com.example.o3uit.Chart.DataPoint;
import com.example.o3uit.Service.ApiService;
import com.example.o3uit.Service.RetrofitClient;
import com.example.o3uit.Token.Token;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChartFragment extends Fragment {



    private String assetId = "5zI6XqkQVSfdgOrZ1MyWEf";

    private String attributeName="";

    private String fromTime="";
    private String toTime= "";

    private ApiService apiService;

    private LineChart chart;

    private float lineWidth = 4f,
            valueTextSize = 10f;


    private TextInputLayout textInputAttributes,
                            textInputTimer;
    private MaterialAutoCompleteTextView autoCompleteTextViewAtrributes,
            autoCompleteTextViewTimer,autoCompleteTextViewDialogTimer;

    private Button btnShow;

    private String typeTempAttribute ="Temperature",
            typeHumidityAttribute ="Humidity",
            typeWindSpeedAttribute="WindSpeed",
            typeRainFallAttribute="RainFall";

    private String typeTimeHour ="Hour",
            typeTimeWeek ="Week",
            typeTimeDay ="Day",
            typeTimeMonth="Month",
            typeTimeYear="Year";





    TextView textView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chart = view.findViewById(R.id.lineChart);
        textInputAttributes = view.findViewById(R.id.inputAttributteName);
        textInputTimer = view.findViewById(R.id.inputLayoutTimer);

        autoCompleteTextViewAtrributes =view.findViewById(R.id.inputTVAttributteName);
        autoCompleteTextViewTimer = view.findViewById(R.id.inputTVTimer);
        autoCompleteTextViewDialogTimer = view.findViewById(R.id.inputTVDialogTimer);

        btnShow = view.findViewById(R.id.btnShowChart);
        DisplayTimeCurrent();

        autoCompleteTextViewDialogTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectTimer();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowChart();
            }
        });

        return view;
    }




    private void ShowChart(){
        SelectAtributeToDrawChart();
        SelectTimerToDrawChart();
        GetDataPointFromJson(Token.getToken(),fromTime,toTime,assetId,attributeName);
    }

    private void SelectTimerToDrawChart(){
        String typeTimer = autoCompleteTextViewTimer.getText().toString();
        String selectTime = autoCompleteTextViewDialogTimer.getText().toString();
        if(typeTimer.equals(typeTimeHour)) getTimes(selectTime,typeTimeHour);
        if(typeTimer.equals(typeTimeWeek)) getTimes(selectTime,typeTimeWeek);
        if(typeTimer.equals(typeTimeDay)) getTimes(selectTime,typeTimeDay);
        if(typeTimer.equals(typeTimeMonth)) getTimes(selectTime,typeTimeMonth);
        if(typeTimer.equals(typeTimeYear)) getTimes(selectTime,typeTimeYear);
    }


    private void SelectAtributeToDrawChart(){
        String typeAttribute = autoCompleteTextViewAtrributes.getText().toString();
        if(typeAttribute.equals(typeTempAttribute))
            attributeName = "temperature";
        if(typeAttribute.equals(typeHumidityAttribute))
            attributeName ="humidity";
        if(typeAttribute.equals(typeWindSpeedAttribute))
            attributeName ="windSpeed";
        if(typeAttribute.equals(typeRainFallAttribute))
            attributeName ="rainfall";
    }
    
    public void GetDataPointFromJson(String token,String fromTime, String toTime, String assetId,String attributeName) {

        apiService= RetrofitClient.getClient().create(ApiService.class);
        String json = "{ \"fromTimestamp\": 0, " +
                "\"toTimestamp\": 0, " +
                "\"fromTime\": \"" + fromTime + "\", " +
                "\"toTime\": \"" + toTime + "\", " +
                "\"type\": \"string\" }";

        // Tạo request body trong raw để post (PostMan)
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        // Truyền các tham số cần thiết vào
        Call<List<DataPoint>> call = apiService.getDataPoints("application/json", "Bearer " + token, "application/json", assetId,attributeName, requestBody);
        call.enqueue(new Callback<List<DataPoint>>() {
            @Override
            public void onResponse(Call<List<DataPoint>> call, Response<List<DataPoint>> response) {
                if (response.isSuccessful()) {
                    List<DataPoint> dataPoints = response.body();
                    DrawLineChart(dataPoints);
                } else {
                    // Xử lý lỗi
                }
            }

            @Override
            public void onFailure(Call<List<DataPoint>> call, Throwable t) {
                // Xử lý lỗi
            }
        });

    }

    private void DrawLineChart(List<DataPoint> dataPoints){

        DataMarkerView mv = new DataMarkerView(getActivity(), R.layout.custom_marker_view);
        chart.setMarker(mv);


        List<Entry> entries = new ArrayList<>();
        for (int i =dataPoints.size()-1;i>=0;i--) {
            // Chuyển đổi dữ liệu của bạn thành Entry và thêm vào danh sách
            entries.add(new Entry(dataPoints.get(i).getX(), dataPoints.get(i).getY()));
        }



        LineDataSet lineDataSet = new LineDataSet(entries, "Nhiet do");
        lineDataSet.setDrawValues(false);

        lineDataSet.setLineWidth(lineWidth);
        lineDataSet.setValueTextSize(valueTextSize);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setCircleRadius(5f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);


        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.CYAN);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(3f);
        xAxis.setTextSize(14f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return format.format(new Date((long) value));
            }
        });
        
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(14f);
        leftAxis.setTextColor(Color.CYAN);
        leftAxis.setGranularity(1f);
        leftAxis.setAxisLineWidth(3f);

        chart.setData(data);
        chart.invalidate();
    }




    public void getTimes(String ISO8601Time,String typeTimer) {
        toTime = ISO8601Time;
        ZonedDateTime endingTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        ZonedDateTime startTime = ZonedDateTime.parse(ISO8601Time, formatter);
        if(typeTimer.equals("Day")) endingTime = startTime.minus(1, ChronoUnit.DAYS);
        if(typeTimer.equals("Week")) endingTime = startTime.minus(1, ChronoUnit.WEEKS);
        if(typeTimer.equals("Hour")) endingTime = startTime.minus(1, ChronoUnit.HOURS);
        if(typeTimer.equals("Month")) endingTime = startTime.minus(1, ChronoUnit.MONTHS);
        if(typeTimer.equals("Year")) endingTime = startTime.minus(1, ChronoUnit.YEARS);
        fromTime = endingTime.format(formatter);

    }


    private void DisplayTimeCurrent(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        autoCompleteTextViewDialogTimer.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void SelectTimer(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity());
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                LocalDate date = LocalDate.of(year, month , dayOfMonth+1);
                ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"));
                String formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
                autoCompleteTextViewDialogTimer.setText(formattedDate);
            }
        });

        // Hiển thị DatePickerDialog
        datePickerDialog.show();
    }

}