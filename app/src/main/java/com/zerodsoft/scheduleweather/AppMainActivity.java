package com.zerodsoft.scheduleweather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.zerodsoft.scheduleweather.activity.editschedule.ScheduleEditActivity;
import com.zerodsoft.scheduleweather.calendarfragment.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarfragment.DayFragment;
import com.zerodsoft.scheduleweather.calendarfragment.MonthFragment;
import com.zerodsoft.scheduleweather.calendarfragment.WeekFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAppMainBinding;
import com.zerodsoft.scheduleweather.databinding.SideNavHeaderBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Date;

public class AppMainActivity extends AppCompatActivity
{
    private EventTransactionFragment calendarTransactionFragment;

    private static final int REQ_SIGN_GOOGLE = 100;
    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    private ActivityAppMainBinding mainBinding;
    private SideNavHeaderBinding sideNavHeaderBinding;

    public static int getDisplayHeight()
    {
        return DISPLAY_HEIGHT;
    }

    public static int getDisplayWidth()
    {
        return DISPLAY_WIDTH;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_main);
        sideNavHeaderBinding = SideNavHeaderBinding.bind(mainBinding.sideNavigation.getHeaderView(0));

        init();
        setNavigationView();

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

        setSupportActionBar(mainBinding.mainToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        View customToolbar = getLayoutInflater().inflate(R.layout.app_main_toolbar, null);
        actionBar.setCustomView(customToolbar);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        firebaseAuth = FirebaseAuth.getInstance();

        calendarTransactionFragment = new EventTransactionFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_layout, calendarTransactionFragment, EventTransactionFragment.TAG).commit();
    }

    private void init()
    {
        KakaoLocalApiCategoryUtil.loadCategories(getApplicationContext());

        sideNavHeaderBinding.signinGoogle.setVisibility(View.VISIBLE);
        sideNavHeaderBinding.signoutGoogle.setVisibility(View.GONE);
        sideNavHeaderBinding.googleAccountEmail.setVisibility(View.GONE);
    }

    private void setNavigationView()
    {
        mainBinding.sideNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.month_type:
                        calendarTransactionFragment.replaceFragment(MonthFragment.TAG);
                        break;
                    case R.id.week_type:
                        calendarTransactionFragment.replaceFragment(WeekFragment.TAG);
                        break;
                    case R.id.day_type:
                        calendarTransactionFragment.replaceFragment(DayFragment.TAG);
                        break;
                    case R.id.favorite:
                        break;
                    case R.id.app_setting:
                        break;
                }
                mainBinding.drawerLayout.closeDrawer(mainBinding.sideNavigation);
                return true;
            }
        });

        sideNavHeaderBinding.signinGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                signInGoogle();
            }
        });
        sideNavHeaderBinding.signoutGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                signOutGoogle();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickToolbar(View view)
    {
        switch (view.getId())
        {
            case R.id.open_navigation_drawer:
                mainBinding.drawerLayout.openDrawer(mainBinding.sideNavigation);
                break;
            case R.id.calendar_month:
                break;
            case R.id.add_schedule:
                Intent intent = new Intent(AppMainActivity.this, ScheduleEditActivity.class);
                intent.putExtra("requestCode", ScheduleEditActivity.ADD_SCHEDULE);
                startActivityForResult(intent, ScheduleEditActivity.ADD_SCHEDULE);
                break;
            case R.id.go_to_today:
                calendarTransactionFragment.goToToday();
                break;
            case R.id.refresh_calendar:
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // updateUI
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_SIGN_GOOGLE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try
                {
                    GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(googleSignInAccount.getIdToken());
                } catch (ApiException e)
                {

                }
        }

        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case ScheduleEditActivity.ADD_SCHEDULE:
                        //새로운 일정이 추가됨 -> 달력 이벤트 갱신
                        calendarTransactionFragment.refreshCalendar((Date) data.getSerializableExtra("startDate"));
                        break;
                }
                break;
            case RESULT_CANCELED:
                break;
        }
    }

    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(AppMainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            // 이메일 주소 표시, 로그인 버튼 날리기
                            sideNavHeaderBinding.googleAccountEmail.setVisibility(View.VISIBLE);
                            sideNavHeaderBinding.googleAccountEmail.setText(user.getEmail());
                            sideNavHeaderBinding.signinGoogle.setVisibility(View.GONE);
                            sideNavHeaderBinding.signoutGoogle.setVisibility(View.VISIBLE);
                        } else
                        {
                            Toast.makeText(AppMainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void signInGoogle()
    {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, REQ_SIGN_GOOGLE);
    }

    private void signOutGoogle()
    {
        if (firebaseAuth.getCurrentUser() != null)
        {
            firebaseAuth.signOut();
            // Google sign out
            googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(AppMainActivity.this, "로그아웃 완료", Toast.LENGTH_SHORT).show();
                        sideNavHeaderBinding.googleAccountEmail.setVisibility(View.GONE);
                        sideNavHeaderBinding.signinGoogle.setVisibility(View.VISIBLE);
                        sideNavHeaderBinding.signoutGoogle.setVisibility(View.GONE);
                    }
                }
            });
        } else
        {
            Toast.makeText(AppMainActivity.this, "로그인 되어 있지 않음", Toast.LENGTH_SHORT).show();
        }
    }

}

