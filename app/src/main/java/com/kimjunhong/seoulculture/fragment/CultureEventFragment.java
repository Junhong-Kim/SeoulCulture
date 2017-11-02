package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kimjunhong.seoulculture.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 7. 24..
 */

public class CultureEventFragment extends Fragment {
    @BindView(R.id.btn_cultureEvent_with_date) TextView date;
    @BindView(R.id.btn_cultureEvent_with_genre) TextView genre;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_event, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_culture_event, menu);
    }

    private void initView() {
        date.setBackgroundResource(R.drawable.bg_accent_top_right_corner);
        date.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        genre.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorDefault));
        genre.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        getFragmentManager().beginTransaction().add(R.id.cultureEvent_container, new CultureEventWithDateFragment()).commit();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setBackgroundResource(R.drawable.bg_accent_top_right_corner);
                date.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                genre.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorDefault));
                genre.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.cultureEvent_container, new CultureEventWithDateFragment());
                fragmentTransaction.commit();
            }
        });

        genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorDefault));
                date.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                genre.setBackgroundResource(R.drawable.bg_accent_top_left_corner);
                genre.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.cultureEvent_container, new CultureEventWithGenreFragment());
                fragmentTransaction.commit();
            }
        });
    }
}
