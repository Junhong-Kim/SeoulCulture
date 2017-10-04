package com.kimjunhong.seoulculture.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.CultureEventSearchWithNameService;
import com.kimjunhong.seoulculture.adapter.GridViewAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventData;
import com.kimjunhong.seoulculture.model.CultureEventSearchWithName;
import com.kimjunhong.seoulculture.model.CultureEventSearchWithNameData;
import com.kimjunhong.seoulculture.util.ClearEditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 10. 4..
 */

public class CultureEventSearchActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.search_editText) ClearEditText clearEditText;
    @BindView(R.id.gridView_cultureEvent_search) GridView gridView;
    @BindView(R.id.defaultLayout_cultureEvent_search) FrameLayout defaultLayout;

    public ArrayList<CultureEventItem> searchItems = new ArrayList<>();
    private GridViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_event_search);
        ButterKnife.bind(this);

        initToolbar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        clearEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        // 키보드 숨기기
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                        final String title = String.valueOf(clearEditText.getText());

                        if(title.equals("")) {
                            // 검색명이 비었을 때
                            Toast.makeText(getApplicationContext(), "문화행사 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            // 이전 검색 리스트 지운 후 검색
                            searchItems.clear();

                            CultureEventSearchWithNameService service = CultureEventSearchWithNameService.retrofit.create(CultureEventSearchWithNameService.class);
                            // 해당 검색명으로 몇개의 행사가 있는지 조회 후 검색
                            Call<CultureEventSearchWithNameData> call = service.getCultureEvents(1, 1, title);
                            call.enqueue(new Callback<CultureEventSearchWithNameData>() {
                                @Override
                                public void onResponse(Call<CultureEventSearchWithNameData> call, Response<CultureEventSearchWithNameData> response) {
                                    try {
                                        // 검색된 행사가 있는 경우
                                        getCultureEvents(1, response.body().getSearchConcertNameService().getList_total_count(), title);
                                    } catch (Exception e) {
                                        // 검색된 행사가 없는 경우
                                        Toast.makeText(getApplicationContext(), "문화행사가 없습니다", Toast.LENGTH_SHORT).show();
                                        defaultLayout.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onFailure(Call<CultureEventSearchWithNameData> call, Throwable t) {

                                }
                            });
                        }
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });

        clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (clearEditText.isFocused()) // 포커스 됬을때 만 판별
                    clearEditText.setClearIconVisible(charSequence.length() > 0); // 비어있지 않으면 아이콘 사라지기

//                if (charSequence.length() > 0) {
//                    // 텍스트 길이가 0보다 크면 숨기기
//                    defaultLayout.setVisibility(View.INVISIBLE);
//                } else {
//                    // 텍스트 길이가 0이면 보이기
//                    defaultLayout.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getCultureEvents(int startIndex, int endIndex, String title) {
        // 검색 서비스
        CultureEventSearchWithNameService service = CultureEventSearchWithNameService.retrofit.create(CultureEventSearchWithNameService.class);
        Call<CultureEventSearchWithNameData> call = service.getCultureEvents(startIndex, endIndex, title);
        call.enqueue(new Callback<CultureEventSearchWithNameData>() {
            @Override
            public void onResponse(Call<CultureEventSearchWithNameData> call, Response<CultureEventSearchWithNameData> response) {
                // 검색된 행사 개수
                int size = response.body().getSearchConcertNameService().getRow().size();
                // 검색된 행사 정보
                ArrayList<CultureEventSearchWithName> row = response.body().getSearchConcertNameService().getRow();

                // 행사 상세 정보 가져오기
                CultureEventService cultureEventService = CultureEventService.retrofit.create(CultureEventService.class);
                // 검색된 행사 개수 만큼 반복
                for(int i = 0; i< size; i++) {
                    // 검색된 행사 코드
                    Log.v("log", "id: " + row.get(i).getCULTCODE());

                    final int count = i;
                    final CultureEventItem[] item = new CultureEventItem[size];

                    Call<CultureEventData> detailCall = cultureEventService.getCultureEvent(1, 1, row.get(i).getCULTCODE());
                    detailCall.enqueue(new Callback<CultureEventData>() {
                        @Override
                        public void onResponse(Call<CultureEventData> call, Response<CultureEventData> response) {
                            CultureEvent cultureEvent = response.body().getSearchConcertDetailService().getRow().get(0);
                            item[count] = new CultureEventItem(cultureEvent.getCULTCODE(),
                                                               cultureEvent.getMAIN_IMG(),
                                                               cultureEvent.getIS_FREE(),
                                                               cultureEvent.getCODENAME(),
                                                               cultureEvent.getTITLE(),
                                                               cultureEvent.getGCODE(),
                                                               cultureEvent.getPLACE(),
                                                               cultureEvent.getSTRTDATE(),
                                                               cultureEvent.getEND_DATE());
                            searchItems.add(item[count]);
                            initGridViewEvents(searchItems);
                        }

                        @Override
                        public void onFailure(Call<CultureEventData> call, Throwable t) {

                        }
                    });
                    defaultLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CultureEventSearchWithNameData> call, Throwable t) {

            }
        });
    }

    // 검색 GridView 초기화
    public void initGridViewEvents(ArrayList<CultureEventItem> items) {
        mAdapter = new GridViewAdapter(getApplicationContext(), items);
        gridView.setAdapter(mAdapter);
    }
}
