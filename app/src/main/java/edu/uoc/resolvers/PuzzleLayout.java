package edu.uoc.resolvers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    private PuzzleHelper ph;
    private int mDrawableId;
    private int numCortes;
    private int mHeight;
    private int mWidth;
    private int mItemWidth;
    private int mItemHeight;
    private OnCompleteCallback occ;

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
                if(mDrawableId != 0 && numCortes != 0){
                    createChildren();
                }
                return false;
            }
        });
        ph = new PuzzleHelper();

        vdh = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                int index = indexOfChild(child);
                return ph.obtenerPosicionDesplazamiento(index) != PuzzleHelper.POSICION_ACTUAL;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {

                int index = indexOfChild(child);
                int position = ph.obtenerPieza(index).posicion;
                int selfLeft = (position % numCortes) * mItemWidth;
                int leftEdge = selfLeft - mItemWidth;
                int rightEdge = selfLeft + mItemWidth;
                int direction = ph.obtenerPosicionDesplazamiento(index);
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
                Pieza model = ph.obtenerPieza(index);
                int position = model.posicion;

                int selfTop = (position / numCortes) * mItemHeight;
                int topEdge = selfTop - mItemHeight;
                int bottomEdge = selfTop + mItemHeight;
                int direction = ph.obtenerPosicionDesplazamiento(index);
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
                boolean isCompleted = ph.intercambiarPosicionConPiezaVacia(index);
                Pieza item =  ph.obtenerPieza(index);
                vdh.settleCapturedViewAt(item.phorizontal * mItemWidth, item.pvertical * mItemHeight);
                View invisibleView = getChildAt(0);
                ViewGroup.LayoutParams layoutParams = invisibleView.getLayoutParams();
                invisibleView.setLayoutParams(releasedChild.getLayoutParams());
                releasedChild.setLayoutParams(layoutParams);
                invalidate();
                if(isCompleted){
                    invisibleView.setVisibility(VISIBLE);
                    occ.onComplete();
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
        this.numCortes = squareRootNum;
        this.mDrawableId = drawableId;
        if(mWidth != 0 && mHeight != 0){
            createChildren();
        }
    }

    // Correspondencia uno a uno entre el índice de Vista hijo y el índice de modelos en mHelper.
    // El modelo actualiza la posición actual sincrónicamente cada vez que se intercambia la posición de vista secundaria.
    private void createChildren(){
        removeAllViews();
        ph.establecerNumeroCortes(numCortes);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = dm.densityDpi;

        Bitmap resource = BitmapFactory.decodeResource(getResources(), mDrawableId, options);
        Bitmap bitmap = escalarImagen(resource, mWidth, mHeight);
        resource.recycle();

        mItemWidth = mWidth / numCortes;
        mItemHeight = mHeight / numCortes;

        for (int i = 0; i < numCortes; i++){
            for (int j = 0; j < numCortes; j++){
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

    private Bitmap escalarImagen(Bitmap bm, int newWidth , int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public void randomOrder(){
        int num = numCortes * numCortes * 8;
        View invisibleView = getChildAt(0);
        View neighbor;
        for (int i = 0; i < num; i ++){
            int neighborPosition = ph.encontrarIndiceVecinoPiezaVacia();
            ViewGroup.LayoutParams invisibleLp = invisibleView.getLayoutParams();
            neighbor = getChildAt(neighborPosition);
            invisibleView.setLayoutParams(neighbor.getLayoutParams());
            neighbor.setLayoutParams(invisibleLp);
            ph.intercambiarPosicionConPiezaVacia(neighborPosition);
        }
        invisibleView.setVisibility(INVISIBLE);
    }

    public void setOnCompleteCallback(OnCompleteCallback onCompleteCallback){
        occ = onCompleteCallback;
    }

    public interface OnCompleteCallback{
        void onComplete();
    }
}
