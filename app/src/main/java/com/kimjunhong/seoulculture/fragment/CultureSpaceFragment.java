package com.kimjunhong.seoulculture.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kimjunhong.seoulculture.CultureSpaceService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.RecyclerViewAdapter;
import com.kimjunhong.seoulculture.item.CultureSpaceItem;
import com.kimjunhong.seoulculture.model.CultureSpace;
import com.kimjunhong.seoulculture.model.CultureSpaceData;
import com.kimjunhong.seoulculture.util.RecyclerViewDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 7. 24..
 */

public class CultureSpaceFragment extends Fragment {
    @BindView(R.id.cultureSpace_find_maps_text) TextView findMapsText;
    @BindView(R.id.cultureSpace_find_maps_layout) LinearLayout findMapsLayout;
    @BindView(R.id.recyclerView_cultureSpace) RecyclerView recyclerView;

    ArrayList<CultureSpaceItem> allItems = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;
    private int startIndex = 1;
    private int endIndex = 6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_space, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        initView();
        getCultureSpaces(startIndex, endIndex);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    private void getCultureSpaces(final int startIndex, int endIndex) {
        CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
        Call<CultureSpaceData> call = service.getCultureSpaces(startIndex, endIndex);
        call.enqueue(new Callback<CultureSpaceData>() {
            @Override
            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                ArrayList<CultureSpaceItem> items = new ArrayList<>();
                int itemSize = response.body().getSearchCulturalFacilitiesDetailService().getRow().size();
                ArrayList<CultureSpace> row = response.body().getSearchCulturalFacilitiesDetailService().getRow();

                CultureSpaceItem[] item = new CultureSpaceItem[itemSize];

                for(int i = 0; i < itemSize; i++) {
                    item[i] = new CultureSpaceItem(row.get(i).getFAC_CODE(),
                                                   row.get(i).getCODENAME(),
                                                   row.get(i).getFAC_NAME(),
                                                   row.get(i).getMAIN_IMG(),
                                                   row.get(i).getADDR(),
                                                   row.get(i).getENTRFREE(),
                                                   row.get(i).getETC_DESC());
                    items.add(item[i]);
                }
                allItems.addAll(items);

                if(startIndex == 1) {
                    initRecyclerView(allItems);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

            }
        });
    }

    private void initRecyclerView(ArrayList<CultureSpaceItem> items) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        mAdapter = new RecyclerViewAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(25));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;

                if(lastPosition == totalItemCount) {
                    startIndex = startIndex + 6;
                    endIndex = endIndex + 6;
                    getCultureSpaces(startIndex, endIndex);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initView() {
        findMapsText.setText(spannableString());
        findMapsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Maps", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SpannableString spannableString() {
        SpannableString string = new SpannableString(findMapsText.getText());
        string.setSpan(new StyleSpan(Typeface.BOLD), 5, 9, 0);
        string.setSpan(new RelativeSizeSpan(1.4f), 5, 9, 0);

        return string;
    }
}
