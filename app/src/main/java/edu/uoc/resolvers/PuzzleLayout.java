package edu.uoc.resolvers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.customview.widget.ViewDragHelper;

public class PuzzleLayout extends RelativeLayout {
    private ViewDragHelper vdh;
    private PuzzleHelper mHelper;
    private int mDrawableId;
    private int mSquareRootNum;
    private int mHeight;
    private int mWidth;
    private int mItemWidth;
    private int mItemHeight;
    private OnCompleteCallback mOnCompleteCallback;

    public PuzzleLayout(Context context) {
        super(context);
        init();
    }

    public PuzzleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PuzzleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mHeight = getHeight();
                mWidth = getWidth();
                getViewTreeObserver().removeOnPreDrawListener(this);
                if(mDrawableId != 0 && mSquareRootNum != 0){
                    createChildren();
                }
                return false;
            }
        });
        mHelper = new PuzzleHelper();

        vdh = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                int index = indexOfChild(child);
                return mHelper.obtenerPosicionDesplazamiento(index) != PuzzleHelper.POSICION_ACTUAL;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {

                int index = indexOfChild(child);
                int position = mHelper.obtenerPieza(index).posicion;
                int selfLeft = (position % mSquareRootNum) * mItemWidth;
                int leftEdge = selfLeft - mItemWidth;
                int rightEdge = selfLeft + mItemWidth;
                int direction = mHelper.obtenerPosicionDesplazamiento(index);
                switch (direction){
                    case PuzzleHelper.IZQUIERDA:
                        if(left <= leftEdge)
                            return leftEdge;
                        else if(left >= selfLeft)
                            return selfLeft;
                        else
                            return left;

                    case PuzzleHelper.DERECHA:
                        if(left >= rightEdge)
                            return rightEdge;
                        else if (left <= selfLeft)
                            return selfLeft;
                        else
                            return left;
                    default:
                        return selfLeft;
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                int index = indexOfChild(child);
                Pieza model = mHelper.obtenerPieza(index);
                int position = model.posicion;

                int selfTop = (position / mSquareRootNum) * mItemHeight;
                int topEdge = selfTop - mItemHeight;
                int bottomEdge = selfTop + mItemHeight;
                int direction = mHelper.obtenerPosicionDesplazamiento(index);
                //Log.d(TAG, "top " + top + " index " + index + " direction " + direction);
                switch (direction){
                    case PuzzleHelper.ARRIBA:
                        if(top <= topEdge)
                            return topEdge;
                        else if (top >= selfTop)
                            return selfTop;
                        else
                            return top;
                    case PuzzleHelper.ABAJO:
                        if(top >= bottomEdge)
                            return bottomEdge;
                        else if (top <= selfTop)
                            return selfTop;
                        else
                            return top;
                    default:
                        return selfTop;
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int index = indexOfChild(releasedChild);
                boolean isCompleted = mHelper.intercambiarPosicionConPiezaVacia(index);
                Pieza item =  mHelper.obtenerPieza(index);
                vdh.settleCapturedViewAt(item.phorizontal * mItemWidth, item.pvertical * mItemHeight);
                View invisibleView = getChildAt(0);
                ViewGroup.LayoutParams layoutParams = invisibleView.getLayoutParams();
                invisibleView.setLayoutParams(releasedChild.getLayoutParams());
                releasedChild.setLayoutParams(layoutParams);
                invalidate();
                if(isCompleted){
                    invisibleView.setVisibility(VISIBLE);
                    mOnCompleteCallback.onComplete();
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return vdh.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        vdh.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if(vdh.continueSettling(true)) {
            invalidate();
        }
    }

    public void setImage(int drawableId, int squareRootNum){
        this.mSquareRootNum = squareRootNum;
        this.mDrawableId = drawableId;
        if(mWidth != 0 && mHeight != 0){
            createChildren();
        }
    }

    // Correspondencia uno a uno entre el índice de Vista hijo y el índice de modelos en mHelper.
    // El modelo actualiza la posición actual sincrónicamente cada vez que se intercambia la posición de vista secundaria.
    private void createChildren(){
        removeAllViews();
        mHelper.establecerNumeroCortes(mSquareRootNum);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = dm.densityDpi;

        Bitmap resource = BitmapFactory.decodeResource(getResources(), mDrawableId, options);
        Bitmap bitmap = BitmapUtil.zoomImg(resource, mWidth, mHeight);
        resource.recycle();

        mItemWidth = mWidth / mSquareRootNum;
        mItemHeight = mHeight / mSquareRootNum;

        for (int i = 0; i < mSquareRootNum; i++){
            for (int j = 0; j < mSquareRootNum; j++){
                ImageView iv = new ImageView(getContext());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = j * mItemWidth;
                lp.topMargin = i * mItemHeight;
                iv.setLayoutParams(lp);
                Bitmap b = Bitmap.createBitmap(bitmap, lp.leftMargin, lp.topMargin, mItemWidth, mItemHeight);
                iv.setImageBitmap(b);
                addView(iv);
            }
        }
        randomOrder();
    }

    public void randomOrder(){
        int num = mSquareRootNum * mSquareRootNum * 8;
        View invisibleView = getChildAt(0);
        View neighbor;
        for (int i = 0; i < num; i ++){
            int neighborPosition = mHelper.encontrarIndiceVecinoPiezaVacia();
            ViewGroup.LayoutParams invisibleLp = invisibleView.getLayoutParams();
            neighbor = getChildAt(neighborPosition);
            invisibleView.setLayoutParams(neighbor.getLayoutParams());
            neighbor.setLayoutParams(invisibleLp);
            mHelper.intercambiarPosicionConPiezaVacia(neighborPosition);
        }
        invisibleView.setVisibility(INVISIBLE);
    }

    public void setOnCompleteCallback(OnCompleteCallback onCompleteCallback){
        mOnCompleteCallback = onCompleteCallback;
    }

    public interface OnCompleteCallback{
        void onComplete();
    }
}
