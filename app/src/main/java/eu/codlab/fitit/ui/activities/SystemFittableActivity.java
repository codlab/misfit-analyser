package eu.codlab.fitit.ui.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.codlab.fitit.R;

/**
 * Created by kevinleperf on 31/03/15.
 */
public abstract class SystemFittableActivity extends ActionBarActivity {
    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";

    private int mStatusBarHeight;
    private SystemBarTintManager _tint_manager;
    private int _primary_color_dark;
    private int _primary_color;

    @InjectView(R.id.toolbar)
    protected Toolbar _toolbar;

    @InjectView(R.id.root)
    View _parent;

    private boolean _not_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _not_set = true;

        //checkLollipopFullTransparency();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(_toolbar);

        _primary_color_dark = getResources().getColor(R.color.colorPrimaryDark);
        _primary_color = getResources().getColor(R.color.colorPrimary);

        _tint_manager = getSystemBarTintManager();
        _tint_manager.setStatusBarTintEnabled(true);
        _tint_manager.setNavigationBarTintEnabled(true);


        //_stack_manager = getFragmentStackManager();

        _tint_manager.setTintColor(0x00000000);
        setSystemColor(0x0, 0);

        findViewById(R.id.container).setFitsSystemWindows(false);
    }

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        if (_not_set || Build.VERSION.SDK_INT >= 19) {
            _not_set = false;
            super.setSupportActionBar(toolbar);
        }
    }

    protected SystemBarTintManager getSystemBarTintManager() {
        if (_tint_manager == null) ;
        _tint_manager = new SystemBarTintManager(this);
        return _tint_manager;
    }

    //protected abstract FragmentStackManager getFragmentStackManager();

    private void setSystemColor(int systembar_color, int navigation_color) {
        _tint_manager.setStatusBarTintColor(systembar_color);
        _tint_manager.setNavigationBarTintColor(navigation_color);
    }

    public void setTintColor() {

    }

    public void setPaddingTop(int dimen) {
        //map.setPadding(0, config.getPixelInsetTop(), config.getPixelInsetRight(), config.getPixelInsetBottom());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        invalidateActionBar();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void push(int fragment_type, Bundle arguments) {
        invalidateActionBar();
    }

    public void invalidateActionBar() {
        /*ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(_stack_manager.canBePopped());
        }*/
    }

    public void checkLollipopFullTransparency() {
        //TODO UPDATE SystemBarTintManager to support the statusBarColor mode only for 5.0+
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
        mStatusBarHeight = 0;
    }

    private int getmStatusBarHeight() {
        if (mStatusBarHeight <= 0)
            mStatusBarHeight = getInternalDimensionSize(getResources(), STATUS_BAR_HEIGHT_RES_NAME);
        return mStatusBarHeight;
    }

    private int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected int getPaddingInsetTop(boolean with_actionbar) {
        return getPaddingInsetTop(with_actionbar, getSystemBarTintManager().getConfig());
    }

    protected int getPaddingInsetTop(boolean with_actionbar, SystemBarTintManager.SystemBarConfig config) {
        if (Build.VERSION.SDK_INT >= 21) {
            return getmStatusBarHeight();
        }
        return config.getPixelInsetTop(with_actionbar);
    }


    private void unsetInsetsToView(SystemBarTintManager.SystemBarConfig config, View view) {
        _parent.setPadding(0, config.getPixelInsetTop(false), 0, config.getPixelInsetBottom());
    }

    public void unsetInsets() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = _tint_manager.getConfig();
        unsetInsetsToView(config, _parent);
        //_toolbar.getBackground().setAlpha(0);
        _toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSystemColor(Color.TRANSPARENT, Color.TRANSPARENT);
    }

    public void unsetInsetsAndGoneAlsoForLeftMenu(View left_menu) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = _tint_manager.getConfig();
        unsetInsetsToView(config, _parent);
        if (left_menu != null)
            unsetInsetsToView(config, left_menu);
        _toolbar.setVisibility(View.GONE);
        //_toolbar.getBackground().setAlpha(0);
        _toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSystemColor(Color.TRANSPARENT, Color.TRANSPARENT);
    }

    public SystemBarTintManager.SystemBarConfig getSystemBarConfig() {
        return _tint_manager.getConfig();
    }

    public void unsetInsetsAndGone() {
        unsetInsetsAndGoneAlsoForLeftMenu(null);
    }

    public void showDashboard() {
        unsetInsetsAndGone();
        //if (mMenuView != null)
        //    mMenuView.setPaddingBottom(getSystemBarConfig().getPixelInsetBottom());
    }

    private void setInsets(int color, View to_be_padded, boolean visibility) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = _tint_manager.getConfig();
        if (to_be_padded != null) {
            to_be_padded.setPadding(0, getPaddingInsetTop(false, config),
                    config.getPixelInsetRight(), config.getPixelInsetBottom());
        }

        if (_toolbar.getBackground() == null
                || !(_toolbar.getBackground() instanceof ColorDrawable)
                || ((ColorDrawable) _toolbar.getBackground()).getColor() != color) {
            _toolbar.setBackgroundColor(color);
        }
        //_toolbar.getBackground().setAlpha(alpha);
        int visibility_view = visibility ? View.VISIBLE : View.INVISIBLE;
        if (visibility_view != _toolbar.getVisibility()) {
            _toolbar.setVisibility(visibility_view);
        }
    }

    public void setInsets(View to_be_padded) {
        setSystemColor(_primary_color_dark, Color.BLACK);
        setInsets(0, _parent, true);
    }

    public void setInsets() {
        setSystemColor(_primary_color_dark, Color.BLACK);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        SystemBarTintManager.SystemBarConfig config = _tint_manager.getConfig();
        _parent.setPadding(0, getPaddingInsetTop(false, config),
                config.getPixelInsetRight(), config.getPixelInsetBottom());
        _toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        _toolbar.setVisibility(View.VISIBLE);
        /*setSystemColor(_primary_color_dark, Color.BLACK);
        setInsets(getResources().getColor(R.color.colorPrimary), _parent, true);*/
    }

    public Toolbar getToolbar() {
        if (_toolbar == null)
            return (Toolbar) findViewById(R.id.toolbar);
        return _toolbar;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
