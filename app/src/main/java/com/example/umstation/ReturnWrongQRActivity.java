package com.example.umstation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//rent인 경우의 우산 qr인식 후 server로 값 전달
public class ReturnWrongQRActivity extends AppCompatActivity {
    private IntentIntegrator qrScan;
    private TextView text_UmCode;
    private Button UmCamera;
    private Button Rent;
    int flag = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_wrong_qractivity);

        //레트로핏 객체 생성
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://b6a8-27-117-234-165.jp.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        ReturnWrongAPI stationAPI = retrofit.create(ReturnWrongAPI.class);

        qrScan = new IntentIntegrator(this);
        text_UmCode = (TextView) findViewById(R.id.text_UmCode);
        String StationQR = getIntent().getStringExtra("StationNum");
        String state = getIntent().getStringExtra("State");;
        String UserID = getIntent().getStringExtra("UserID");

        UmCamera = findViewById(R.id.UmCamera);
        UmCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("우산의 QR을 인식하세요!");
                qrScan.initiateScan();
            }
        });

        Rent = findViewById(R.id.button_Rent);
        Rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 1){
                    String ReadStationNum = StationQR;
                    String ReadUmNum = text_UmCode.getText().toString();
                    String ReadState = state;
                    String ReadUserID = UserID;
                    PostStation postUserStation = new PostStation(
                            ReadStationNum,
                            ReadUmNum,
                            ReadState,
                            ReadUserID
                    );
                    System.out.println(ReadStationNum);
                    System.out.println(ReadUmNum);
                    System.out.println(ReadState);
                    Call<PostStation> call = stationAPI.StationData(postUserStation);
                    call.enqueue(new Callback<PostStation>() {
                        @Override
                        public void onResponse(Call<PostStation> call, Response<PostStation> response) {
                            if(response.isSuccessful()){
                                //상태코드
                                PostStation resource = response.body();
                                int type = Integer.parseInt(resource.response_type());

                                switch (type) {
                                    case 404 : //우산 반납 안됨

                                        break;

                                    case 407 : //스테이션에 우산 적재할 자리 없음

                                        break;

                                    case 408 : //Return 이외 태그 삽입 시 오류

                                        break;

                                    case 204 : //반납 성공
                                        Intent intent = new Intent(ReturnWrongQRActivity.this, MainActivity.class);
                                        intent.putExtra("UserID", UserID);
                                        startActivity(intent);
                                        break;
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<PostStation> call, Throwable t) { }
                    });
                }else{
                    showButtonAlertDialog2();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        //QR코드 인식 성공 시
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    text_UmCode.setText(obj.getString("code"));
                    flag = 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void showButtonAlertDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("UmStation");
        builder.setMessage("우산 QR을 인식해주세요");
        builder.show();
    }

}