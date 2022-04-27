package com.industio.ecigarette.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

public class DragTopLayout extends FrameLayout {

    private SetupWizard wizard;
    private ViewDragHelper dragHelper;
    private int dragRange;
    private View dragContentView;
    private View topView;

    private int contentTop;
    private int topViewHeight;
    private boolean isRefreshing;

    public static enum PanelState {
        EXPANDED,
        COLLAPSED,
        SLIDING
    }

    private PanelState panelState = PanelState.COLLAPSED;
    private boolean shouldIntercept = true;

    public DragTopLayout(Context context) {
        this(context, null);
    }

    public DragTopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dragHelper = ViewDragHelper.create(this, 1.0f, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new RuntimeException("Content view must contains two child view at least.");
        }
        topView = getChildAt(0);
        dragContentView = getChildAt(1);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dragRange = getHeight();

        // In case of sliding
        int contentTopTemp = contentTop;
        resetTopViewHeight();

        topView.layout(left, Math.min(0, contentTop - topViewHeight), right, contentTop);
        dragContentView.layout(
                left,
                contentTopTemp,
                right,
                contentTopTemp + dragContentView.getHeight());
    }

    private void resetTopViewHeight() {
        int newTopHeight = topView.getHeight();
        // Top layout is changed
        if (topViewHeight != newTopHeight) {
            if (panelState == PanelState.EXPANDED) {
                contentTop = newTopHeight;
                handleSlide(newTopHeight);
            }
            topViewHeight = newTopHeight;
        }
    }

    private void handleSlide(final int top) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dragHelper.smoothSlideViewTo(dragContentView, getPaddingLeft(), top);
                postInvalidate();
            }
        });
    }

    public void openTopView(boolean anim) {
        resetDragContent(anim, topViewHeight);
    }

    public void closeTopView(boolean anim) {
        resetDragContent(anim, 0);
    }

    public void toggleTopView() {
        toggleTopView(false);
    }

    public void toggleTopView(boolean touchMode) {
        switch (panelState) {
            case COLLAPSED:
                openTopView(true);
                if (touchMode) {
                    setTouchMode(true);
                }
                break;
            case EXPANDED:
                closeTopView(true);
                if (touchMode) {
                    setTouchMode(false);
                }
                break;
        }
    }

    private void resetDragContent(boolean anim, int top) {
        contentTop = top;
        if (anim) {
            dragHelper.smoothSlideViewTo(dragContentView, getPaddingLeft(), contentTop);
            postInvalidate();
        } else {
            requestLayout();
        }
    }

    public void setOverDrag(boolean overDrag){
        wizard.overDrag = overDrag;
    }

    public boolean isOverDrag(){
        return wizard.overDrag;
    }

    /**
     * Get refresh state
     * @return
     */
    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setRefreshing(boolean isRefreshing){
        this.isRefreshing = isRefreshing;
    }

    /**
     * Complete refresh and reset the refresh state.
     */
    public void onRefreshComplete() {
        isRefreshing = false;
    }

    private void calculateRadio(float top){
        if (wizard.panelListener != null) {
            // Calculate the radio while dragging.
            float radio = top / topViewHeight;
            wizard.panelListener.onSliding(radio);
            if (radio > wizard.refreshRadio && !isRefreshing) {
                wizard.panelListener.onRefresh();
                isRefreshing = true;
            }
        }
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == dragContentView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            contentTop = top;
            requestLayout();
            calculateRadio(contentTop);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return dragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (wizard.overDrag) {
                // Drag over the top view height.
                return Math.max(top, getPaddingTop());
            }else{
                return Math.min(topViewHeight, Math.max(top, getPaddingTop()));
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            // yvel > 0 Fling down || yvel < 0 Fling up
            int top;
            if (yvel > 0 || contentTop > topViewHeight) {
                top = topViewHeight + getPaddingTop();
            } else {
                top = getPaddingTop();
            }
            dragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            postInvalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            // 1 -> 2 -> 0
            if (state == ViewDragHelper.STATE_IDLE) {
                // Change the panel state while the drag content view is idle.
                if (contentTop > getPaddingTop()) {
                    panelState = PanelState.EXPANDED;
                } else {
                    panelState = PanelState.COLLAPSED;
                }
            }else{
                panelState = PanelState.SLIDING;
            }
            if (wizard.panelListener != null) {
                wizard.panelListener.onPanelStateChanged(panelState);
            }
            super.onViewDragStateChanged(state);
        }
    };


    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            // java.lang.NullPointerException: Attempt to read from null array
            // at android.support.v4.widget.ViewDragHelper.shouldInterceptTouchEvent(ViewDragHelper.java:1011)
            return shouldIntercept && dragHelper.shouldInterceptTouchEvent(ev);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }


    public void setTouchMode(boolean shouldIntercept) {
        this.shouldIntercept = shouldIntercept;
    }

    private void setupWizard(SetupWizard setupWizard) {
        this.wizard = setupWizard;

        if (wizard.panelListener != null){
            if (wizard.initOpen) {
                panelState = PanelState.EXPANDED;
                wizard.panelListener.onSliding(1.0f);
            }else{
                panelState = PanelState.COLLAPSED;
                wizard.panelListener.onSliding(0f);
            }
        }
    }

    public interface PanelListener {
        /**
         * Called while the panel state is changed.
         * @param panelState
         */
        public void onPanelStateChanged(PanelState panelState);

        /**
         * Called while dragging.
         * radio >= 0.
         * @param radio
         */
        public void onSliding(float radio);

        /**
         * Called while the radio over refreshRadio.
         */
        public void onRefresh();
    }

    public static class SimplePanelListener implements PanelListener {

        @Override
        public void onPanelStateChanged(PanelState panelState) {

        }

        @Override
        public void onSliding(float radio) {

        }

        @Override
        public void onRefresh() {

        }
    }


    // -----------------

    public static SetupWizard from(Context context) {
        return new SetupWizard(context);
    }

    public static final class SetupWizard {
        private Context context;
        private PanelListener panelListener;
        private boolean initOpen;
        private float refreshRadio = 1.5f;
        private boolean overDrag = true;

        public SetupWizard(Context context) {
            this.context = context;
        }

        /**
         * Setup the drag listener.
         * @return SetupWizard
         */
        public SetupWizard listener(PanelListener panelListener) {
            this.panelListener = panelListener;
            return this;
        }

        /**
         * Open the top view after the drag layout is created.
         * The default value is false.
         * @return SetupWizard
         */
        public SetupWizard open() {
            initOpen = true;
            return this;
        }

        /**
         * Set the refresh position while dragging you want.
         * The default value is 1.5f.
         * @return SetupWizard
         */
        public SetupWizard setRefreshRadio(float radio) {
            this.refreshRadio = radio;
            return this;
        }

        public SetupWizard setOverDrag(boolean overDrag) {
            this.overDrag = overDrag;
            return this;
        }

        public void setup(DragTopLayout dragTopLayout) {
            dragTopLayout.setupWizard(this);
        }
    }


}
