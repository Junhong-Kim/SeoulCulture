package com.kimjunhong.seoulculture.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.seoulculture.CultureEventSearchWithNameService;
import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureEventAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;
import com.kimjunhong.seoulculture.model.CultureEventData;
import com.kimjunhong.seoulculture.model.CultureEventSearchWithName;
import com.kimjunhong.seoulculture.model.CultureEventSearchWithNameData;
import com.kimjunhong.seoulculture.util.ClearEditText;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 10. 4..
 */

public class CultureEventSearchActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.search_editText) ClearEditText clearEditText;
    @BindView(R.id.gridRecyclerView_cultureEvent_search) RecyclerView recyclerView;
    @BindView(R.id.defaultLayout_cultureEvent_search) FrameLayout defaultLayout;

    public ArrayList<CultureEventItem> searchItems = new ArrayList<>();
    private CultureEventAdapter mAdapter;

    private boolean isBookmark = false;
    private Realm realm;

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
                            // 검색어가 비었을 때
                            Toast.makeText(getApplicationContext(), "문화행사 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            // 이전 검색 리스트 지운 후 검색
                            searchItems.clear();

                            CultureEventSearchWithNameService service = CultureEventSearchWithNameService.retrofit.create(CultureEventSearchWithNameService.class);
                            // 해당 검색어로 몇개의 행사가 있는지 조회 후 검색
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
                if (clearEditText.isFocused()) { // 포커스 판별
                    clearEditText.setClearIconVisible(charSequence.length() > 0); // 비어있지 않으면 아이콘 사라지기
                }
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
        defaultLayout.setVisibility(View.INVISIBLE);

        // 검색 서비스
        CultureEventSearchWithNameService searchService = CultureEventSearchWithNameService.retrofit.create(CultureEventSearchWithNameService.class);
        Call<CultureEventSearchWithNameData> call = searchService.getCultureEvents(startIndex, endIndex, title);
        call.enqueue(new Callback<CultureEventSearchWithNameData>() {
            @Override
            public void onResponse(Call<CultureEventSearchWithNameData> call, Response<CultureEventSearchWithNameData> response) {
                final ArrayList<CultureEventSearchWithName> searchRow = response.body().getSearchConcertNameService().getRow();
                final int searchSize = searchRow.size();

                final CultureEventService eventService = CultureEventService.retrofit.create(CultureEventService.class);
                // 검색된 항목 동기처리
                new AsyncTask<Void, Integer, ArrayList<CultureEventItem>>() {
                    @Override
                    protected ArrayList<CultureEventItem> doInBackground(Void... voids) {
                        CultureEventItem[] eventItem = new CultureEventItem[searchSize];
                        for (int i = 0; i < searchSize; i++) {
                            final int pos = i;
                            try {
                                // 검색된 항목이 북마크 되어있는지
                                realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        CultureEventBookmark eventBookmark = CultureEventBookmark.findOne(realm, searchRow.get(pos).getCULTCODE());
                                        isBookmark = true;
                                        Log.v("log", "bookmark : " + eventBookmark.getEventId());
                                    }
                                });
                            } catch (Exception e) {
                                isBookmark = false;
                                Log.v("log", "bookmark : " + e);
                            } finally {
                                // 검색된 항목 상세 정보 가져오기
                                Call<CultureEventData> eventCall = eventService.getCultureEvent(1, 1, searchRow.get(pos).getCULTCODE());
                                try {
                                    CultureEvent cultureEvent = eventCall.execute().body().getSearchConcertDetailService().getRow().get(0);
                                    eventItem[pos] = new CultureEventItem(cultureEvent.getCULTCODE(),
                                                                          cultureEvent.getMAIN_IMG(),
                                                                          cultureEvent.getIS_FREE(),
                                                                          cultureEvent.getCODENAME(),
                                                                          cultureEvent.getTITLE(),
                                                                          cultureEvent.getGCODE(),
                                                                          cultureEvent.getPLACE(),
                                                                          cultureEvent.getSTRTDATE(),
                                                                          cultureEvent.getEND_DATE(),
                                                                          isBookmark);
                                    searchItems.add(eventItem[pos]);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    realm.close();
                                }
                            }
                        }
                        return searchItems;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<CultureEventItem> searchItems) {
                        super.onPostExecute(searchItems);
                        initGridRecyclerView(searchItems);
                    }
                }.execute();
            }

            @Override
            public void onFailure(Call<CultureEventSearchWithNameData> call, Throwable t) {

            }
        });
    }

    public void initGridRecyclerView(ArrayList<CultureEventItem> eventItems) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        mAdapter = new CultureEventAdapter(getApplicationContext(), eventItems);
        recyclerView.setAdapter(mAdapter);
    }
}
