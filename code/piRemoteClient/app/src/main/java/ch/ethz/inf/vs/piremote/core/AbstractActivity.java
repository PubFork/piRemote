package ch.ethz.inf.vs.piremote.core;

import android.support.v7.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static AbstractClientApplication application;

    public AbstractClientApplication getAbstractApplication() {
        return application;
    }

    public void setApplication(AbstractClientApplication application) {
        AbstractActivity.application = application;
    }
}
