package com.kimjunhong.seoulculture.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.activity.BookmarkActivity;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;
import com.kimjunhong.seoulculture.model.CultureEventData;
import com.kimjunhong.seoulculture.model.CultureSpaceBookmark;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 10. 7..
 */

public class MyCultureFragment extends Fragment {
    @BindView(R.id.my_culture_bookmark) LinearLayout bookmarkLayout;
    @BindView(R.id.my_culture_bookmark_count) TextView bookmarkCount;
    @BindView(R.id.my_culture_license) LinearLayout licenseLayout;
    @BindView(R.id.my_culture_review) LinearLayout reviewLayout;
    @BindView(R.id.my_culture_version) TextView version;

    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_culture, container, false);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    @Override
    public void onResume() {
        setBookmarkCount();
        super.onResume();
    }

    private void initView() {
        // 북마크
        bookmarkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookmarkActivity.class));
            }
        });
        // 아이콘 라이선스
        licenseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://icons8.com/")));
            }
        });
        // 리뷰
        reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.kimjunhong.seoulculture")));
            }
        });
        // 버전 이름
        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = pi.versionName;
            version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setBookmarkCount();
        deleteExpiredBookmark();
    }

    // 북마크 개수
   private void setBookmarkCount() {
        try {
            realm = realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // 현재 (문화행사 + 문화공간) 북마크 개수
                    RealmResults<CultureEventBookmark> eventBookmarks = CultureEventBookmark.findAll(realm);
                    RealmResults<CultureSpaceBookmark> spaceBookmarks = CultureSpaceBookmark.findAll(realm);
                    bookmarkCount.setText(String.valueOf(eventBookmarks.size() + spaceBookmarks.size()));
                }
            });
        } finally {
            realm.close();
        }
    }

    // 기간이 지난 북마크 삭제
   private void deleteExpiredBookmark() {
       try {
           realm = Realm.getDefaultInstance();
           realm.executeTransaction(new Realm.Transaction() {
               @Override
               public void execute(final Realm realm) {
                   // 현재 문화행사 북마크 개수
                   RealmResults<CultureEventBookmark> eventBookmarks = CultureEventBookmark.findAll(realm);

                   // 지난 문화행사 삭제 프로세스
                   for (int i = 0; i < eventBookmarks.size(); i++) {
                       final CultureEventBookmark bookmark = eventBookmarks.get(i);

                       CultureEventService service = CultureEventService.retrofit.create(CultureEventService.class);
                       Call<CultureEventData> call = service.getCultureEvent(1, 1, bookmark.getEventId());
                       call.enqueue(new retrofit2.Callback<CultureEventData>() {
                           @Override
                           public void onResponse(Call<CultureEventData> call, Response<CultureEventData> response) {
                               try {
                                   // 믄화행사 불러오기
                                   response.body().getSearchConcertDetailService().getRow().get(0);
                               } catch (Exception e) {
                                   // 지난 문화행사 삭제
                                   realm.executeTransaction(new Realm.Transaction() {
                                       @Override
                                       public void execute(Realm realm) {
                                           CultureEventBookmark.delete(realm, bookmark.getEventId());
                                       }
                                   });
                               } finally {
                                   realm.close();
                               }
                           }

                           @Override
                           public void onFailure(Call<CultureEventData> call, Throwable t) {

                           }
                       });
                   }
               }
           });
       } finally {
           realm.close();
       }
    }
}
