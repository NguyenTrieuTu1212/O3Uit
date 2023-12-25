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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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




    float rotationAngle =-45f;

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

    private void SelectTimerToDrawChart(){
        String typeTimer = autoCompleteTextViewTimer.getText().toString();
        if(typeTimer.equals(typeTimeHour)) GetDate(typeTimeHour);
        if(typeTimer.equals(typeTimeWeek)) GetDate(typeTimeWeek);
        if(typeTimer.equals(typeTimeDay)) GetDate(typeTimeDay);
        if(typeTimer.equals(typeTimeMonth)) GetDate(typeTimeMonth);
        if(typeTimer.equals(typeTimeYear)) GetDate(typeTimeYear);
    }


    private void GetDate(String typeTimer){

        String dateSelected = autoCompleteTextViewDialogTimer.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDate selectedDate = LocalDate.parse(dateSelected, formatter);
        LocalTime currentTime = LocalTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        LocalDateTime localDateTime = selectedDate.atTime(currentTime);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(ZoneOffset.ofHours(7)); // GMT+7
        toTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toString(); // Convert to UTC
        LocalDateTime previousDateTime = null;

        if(typeTimer.equals("Day")) previousDateTime = localDateTime.minusDays(1);
        if(typeTimer.equals("Week")) previousDateTime = localDateTime.minusWeeks(1);
        if(typeTimer.equals("Month")) previousDateTime = localDateTime.minusMonths(1);
        if(typeTimer.equals("Year")) previousDateTime = localDateTime.minusYears(1);
        if(typeTimer.equals("Hour")) previousDateTime = localDateTime.minusHours(1); // Added condition for "Hour"

        toTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).plusHours(1).toString();
        offsetDateTime = previousDateTime.atOffset(ZoneOffset.ofHours(7)); // GMT+7
        fromTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toString();

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
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.BLUE);
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
        xAxis.setLabelRotationAngle(rotationAngle);
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
        

        // Tạo hiệu ứng cho biểu đồ
        int animationDuration = 500;
        chart.animateX(animationDuration);

    }

    private void DisplayTimeCurrent(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        autoCompleteTextViewDialogTimer.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private void SelectTimer(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String selectedDate = sdf.format(selectedCalendar.getTime());
                autoCompleteTextViewDialogTimer.setText(selectedDate);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

}