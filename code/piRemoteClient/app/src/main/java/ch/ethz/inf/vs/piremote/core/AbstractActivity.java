package ch.ethz.inf.vs.piremote.core;

import android.support.v7.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static AbstractApplication application;

    public static AbstractApplication getAbstractApplication() {
        return application;
    }

    public static void setApplication(AbstractApplication application) {
        AbstractActivity.application = application;
    }
}
