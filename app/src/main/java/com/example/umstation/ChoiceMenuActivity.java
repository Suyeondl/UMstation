package com.example.umstation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChoiceMenuActivity extends AppCompatActivity {
    private Button button_UmRent;
    private Button button_UmRetrun;
    private Button button_oldUm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_menu2);

        //QR불러온 정보
        String StationQR = getIntent().getStringExtra("ReadStationNum");
        String UserID = getIntent().getStringExtra("UserID");
        System.out.println(StationQR);

        //레트로핏 객체 생성
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://b6a8-27-117-234-165.jp.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        OldUmData OldUmData = retrofit.create(OldUmData.class);


        //대여하기(station정보, 대여정보를 우산qrActivity로 보내기
        button_UmRent = findViewById(R.id.button_UmRent);
        button_UmRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceMenuActivity.this, RentQRActivity.class);
                intent.putExtra("StationQR", StationQR);
                intent.putExtra("ChoiceMenu", "rent");
                intent.putExtra("UserID", UserID);
                startActivity(intent);
            }
        });

        //반납하기(station정보, 반납정보를 우산qrActivity로 보내기
        button_UmRetrun = findViewById(R.id.button_UmReturn);
        button_UmRetrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceMenuActivity.this, ChocieUmActivity.class);
                intent.putExtra("StationQR", StationQR);
                intent.putExtra("ChoiceMenu", "return");
                intent.putExtra("UserID", UserID);
                startActivity(intent);
            }
        });

        //server로 전달 - 폐우산(station정보, 폐우산 정보)
        button_oldUm = findViewById(R.id.button_OldUm);
        button_oldUm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = "OldUm";
                String ReadStationNum = StationQR;
                String ReadState = state;
                String ReadUserID = UserID;

                PostOldUm postOldUm = new PostOldUm(
                        ReadStationNum,
                        ReadState,
                        ReadUserID
                );

                System.out.println(ReadStationNum);
                System.out.println(ReadState);
                Call<PostOldUm> call = OldUmData.OldUmData(postOldUm);
                call.enqueue(new Callback<PostOldUm>() {
                    @Override
                    public void onResponse(Call<PostOldUm> call, Response<PostOldUm> response) {
                        if(response.isSuccessful()){

                            //상태코드
                            PostOldUm resource = response.body();
                            int type = Integer.parseInt(resource.response_type());

                            switch (type) {
                                case 404 : //우산 반납 안됨

                                    break;

                                case 407 : //스테이션에 우산 적재할 자리 없음

                                    break;

                                case 408 : //Return 이외 태그 삽입 시 오류

                                    break;

                                case 204 : //반납 성공
                                    Intent intent = new Intent(ChoiceMenuActivity.this, MainActivity.class);
                                    intent.putExtra("UserID", UserID);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<PostOldUm> call, Throwable t) { }
                });
            }
        });
    }
}