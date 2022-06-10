package com.example.umstation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PointActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        mListView = (ListView)findViewById(R.id.listView);

        dataSetting();
    }

    private void dataSetting(){

        //user 아이디
        String UserID = getIntent().getStringExtra("UserID");

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://f6af-203-230-13-2.jp.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        CouponAPI couponAPI = retrofit.create(CouponAPI.class);
        String ReadUserID = UserID;
        CouponData couponData = new CouponData(
                ReadUserID
        );

        Call<List<CouponData>> call = couponAPI.couponData(couponData);
        call.enqueue(new Callback<List<CouponData>>() {
            @Override
            public void onResponse(Call<List<CouponData>> call, retrofit2.Response<List<CouponData>> response) {
                if(response.isSuccessful()) {
                    System.out.println("coupon**********************");
                    List<CouponData> resource = response.body();

                    //리스트 생성
                    ArrayList<String> arrayCPnum = new ArrayList<>();
                    ArrayList<String> arrayCPdate = new ArrayList<>();

                    //각 정보 리스트에 저장
                    for(CouponData re : resource) {
                        arrayCPnum.add(re.coupon_num());
                        arrayCPdate.add(re.coupon_date());
                    }

                    System.out.println(arrayCPnum.get(0));
                    System.out.println(arrayCPdate.get(0));

                    //리스트 크기 측정을 위한 기준 생성
                    ArrayList arrayList = new ArrayList<>();
                    arrayList.addAll(arrayCPnum);

                    MyAdapter mMyAdapter = new MyAdapter();

                    for(int i = 0; i < arrayList.size(); i++) {
                        mMyAdapter.addCoupon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.coupon), arrayCPnum.get(i), arrayCPdate.get(i));
                    }
                    mListView.setAdapter(mMyAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CouponData>> call, Throwable t) {
                System.out.println("coupon#####################");
            }
        });
//
//        //UserID에 맞는 쿠폰 정보 가져옴
//        couponAPI.getData(UserID).enqueue(new Callback<List<CouponData>>() {
//
//            //통신 성공 시 사용자 쿠폰 리스트
//            @Override
//            public void onResponse(Call<List<CouponData>> call, Response<List<CouponData>> response) {
//                List<CouponData> resource = response.body();
//
//                //리스트 생성
//                ArrayList<Integer> arrayCPnum = new ArrayList<>();
//                ArrayList<Date> arrayCPdate = new ArrayList<>();
//
//                //각 정보 리스트에 저장
//                for(CouponData re : resource) {
//                    arrayCPnum.add(re.coupon_num());
//                    arrayCPdate.add(re.coupon_date());
//                }
//
//                //리스트 크기 측정을 위한 기준 생성
//                ArrayList arrayList = new ArrayList<>();
//                arrayList.addAll(arrayCPnum);
//
//                MyAdapter mMyAdapter = new MyAdapter();
//                for(int i = 0; i < arrayList.size(); i++) {
//                    mMyAdapter.addCoupon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.coupon), "쿠폰_"+ i, "기간_"+i);
//                }
//                mListView.setAdapter(mMyAdapter);
//            }
//
//            //통신 실패
//            @Override
//            public void onFailure(Call<List<CouponData>> call, Throwable t) {
//
//            }
//        });
        }
}