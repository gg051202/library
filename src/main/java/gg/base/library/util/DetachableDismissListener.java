package gg.base.library.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.ViewTreeObserver;
import android.view.Window;

/**
 * dialog内存泄漏相关
 */
public final class DetachableDismissListener implements DialogInterface.OnDismissListener {

    public static DetachableDismissListener wrap(DialogInterface.OnDismissListener delegate) {
        return new DetachableDismissListener(delegate);
    }

    private DialogInterface.OnDismissListener delegateOrNull;

    private DetachableDismissListener(DialogInterface.OnDismissListener delegate) {
        this.delegateOrNull = delegate;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (delegateOrNull != null) {
            delegateOrNull.onDismiss(dialog);
        }
    }

    public void clearOnDetach(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.getDecorView()
                        .getViewTreeObserver()
                        .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
                            @Override
                            public void onWindowAttached() {
                            }

                            @Override
                            public void onWindowDetached() {
                                DetachableDismissListener.this.delegateOrNull = null;
                            }
                        });
            }
        }
    }
}