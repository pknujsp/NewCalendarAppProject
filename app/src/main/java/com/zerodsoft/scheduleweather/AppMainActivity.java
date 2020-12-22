package com.zerodsoft.scheduleweather;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.Date;

public class AppMainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private EventTransactionFragment calendarTransactionFragment;

    private static int DISPLAY_WIDTH = 0;
    private static int DISPLAY_HEIGHT = 0;
    private static int CALENDAR_VIEW_HEIGHT = 0;

    public static final int WEEK_FRAGMENT = 0;
    public static final int DAY_FRAGMENT = 1;
    public static final int MONTH_FRAGMENT = 2;

    private View customToolbar;

    private NavigationView navigationView;
    private View navHeader;
    private TextView googleLogin;
    private TextView googleLogout;
    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    private GoogleSignInClient googleSignInClient;
    private static final int REQ_SIGN_GOOGLE = 100;

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
        setContentView(R.layout.activity_app_main);

        init();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.side_navigation);
        navHeader = navigationView.getHeaderView(0);
        googleLogin = (TextView) navHeader.findViewById(R.id.google_login);
        googleLogout = (TextView) navHeader.findViewById(R.id.google_logout);
        setNavigationView();

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        DISPLAY_WIDTH = point.x;
        DISPLAY_HEIGHT = point.y;

        setSupportActionBar(findViewById(R.id.main_toolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        customToolbar = getLayoutInflater().inflate(R.layout.app_main_toolbar, null);
        actionBar.setCustomView(customToolbar);

        customToolbar.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /*
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
*/

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();

        calendarTransactionFragment = new EventTransactionFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.calendar_layout, calendarTransactionFragment, EventTransactionFragment.TAG).commit();
    }

    private void init()
    {
        KakaoLocalApiCategoryUtil.loadCategories(getApplicationContext());
    }

    private void setNavigationView()
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.google_login:
                        loginGoogle();
                        break;
                    case R.id.google_logout:
                        logoutGoogle();
                        break;
                }
                return true;
            }
        });

        googleLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                loginGoogle();
            }
        });
        googleLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                logoutGoogle();
            }
        });
    }

    public void onClickToolbar(View view)
    {
        switch (view.getId())
        {
            case R.id.open_navigation_drawer:
                drawerLayout.openDrawer(navigationView);
                break;
            case R.id.calendar_month:
                //
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
            case R.id.calendar_type:
                PopupMenu popupMenu = new PopupMenu(AppMainActivity.this, view);
                getMenuInflater().inflate(R.menu.calendar_type_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
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
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
                    onResultLogin(googleSignInAccount.getIdToken());
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

    private void onResultLogin(String idToken)
    {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Toast.makeText(AppMainActivity.this, user.getEmail(), Toast.LENGTH_SHORT).show();

                    /*
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("nickName", googleSignInAccount.getDisplayName());
                    intent.putExtras("photoUrl", String.valueOf(googleSignInAccount.getPhotoUrl()));
                    startActivity(intent);

                     */

                } else
                {
                    Toast.makeText(AppMainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static int getCalendarViewHeight()
    {
        return CALENDAR_VIEW_HEIGHT;
    }

    ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
    {
        @Override
        public void onGlobalLayout()
        {
            CALENDAR_VIEW_HEIGHT = DISPLAY_HEIGHT - customToolbar.getHeight();
            //리스너 제거 (해당 뷰의 상태가 변할때 마다 호출되므로)
            removeOnGlobalLayoutListener(customToolbar.getViewTreeObserver(), mGlobalLayoutListener);
        }
    };


    private static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener)
    {
        if (observer == null)
        {
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            observer.removeGlobalOnLayoutListener(listener);
        } else
        {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }

    private void loginGoogle()
    {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, REQ_SIGN_GOOGLE);
    }

    private void logoutGoogle()
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
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

}

