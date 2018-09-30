package com.changqing.demo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.changqing.demo.mvp.main.MainModel;
import com.changqing.demo.mvp.other.MvpActivity;
import com.changqing.demo.retrofit.RetrofitCallback;
import com.changqing.demo.mvp.main.MainPresenter;
import com.changqing.demo.mvp.main.MainView;
import com.changqing.demo.retrofit.ApiCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * 由Activity/Fragment实现View里方法，包含一个Presenter的引用
 * Created by WuXiaolong on 2015/9/23.
 * github:https://github.com/WuXiaolong/
 * 微信公众号：吴小龙同学
 * 个人博客：http://wuxiaolong.me/
 */
public class MainActivity extends MvpActivity<MainPresenter> implements MainView {

    @Bind(com.changqing.demo.R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.changqing.demo.R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBarAsHome("MVP+Retrofit+Rxjava");

    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }


    @Override
    public void getDataSuccess(MainModel model) {
        //接口成功回调
        dataSuccess(model);
    }

    @Override
    public void getDataFail(String msg) {
        toastShow("网络不给力");

    }


    @OnClick({com.changqing.demo.R.id.button0, com.changqing.demo.R.id.button1, com.changqing.demo.R.id.button2})
    public void onClick(View view) {
        switch (view.getId()) {
            case com.changqing.demo.R.id.button0:
                loadDataByRetrofit();
                break;
            case com.changqing.demo.R.id.button1:
                loadDataByRetrofitRxjava();
                break;
            case com.changqing.demo.R.id.button2:
                //请求接口
                mvpPresenter.loadDataByRetrofitRxjava("101310222");
                break;
        }
    }

    private void loadDataByRetrofit() {
        showProgressDialog();
        Call<MainModel> call = apiStores().loadDataByRetrofit("101190201");
        call.enqueue(new RetrofitCallback<MainModel>() {
            @Override
            public void onSuccess(MainModel model) {
                dataSuccess(model);
            }

            @Override
            public void onFailure(int code, String msg) {
                toastShow(msg);
            }

            @Override
            public void onThrowable(Throwable t) {
                toastShow(t.getMessage());
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
        addCalls(call);
    }

    //全国+国外主要城市代码http://mobile.weather.com.cn/js/citylist.xml
    private void loadDataByRetrofitRxjava() {
        showProgressDialog();
        addSubscription(apiStores().loadDataByRetrofitRxjava("101220602"),
                new ApiCallback<MainModel>() {
                    @Override
                    public void onSuccess(MainModel model) {
                        dataSuccess(model);
                    }

                    @Override
                    public void onFailure(String msg) {
                        toastShow(msg);

                    }

                    @Override
                    public void onFinish() {
                        dismissProgressDialog();
                    }
                });
    }

    private void dataSuccess(MainModel model) {
        MainModel.WeatherinfoBean weatherinfo = model.getWeatherinfo();
        String showData = getResources().getString(com.changqing.demo.R.string.city) + weatherinfo.getCity()
                + getResources().getString(com.changqing.demo.R.string.wd) + weatherinfo.getWD()
                + getResources().getString(com.changqing.demo.R.string.ws) + weatherinfo.getWS()
                + getResources().getString(com.changqing.demo.R.string.time) + weatherinfo.getTime();
        text.setText(showData);
    }
}
