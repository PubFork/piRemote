package ch.ethz.inf.vs.piremote.core;

import android.support.v7.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static AbstractApplication application;

    public AbstractApplication getAbstractApplication() {
        return application;
    }

    public void setApplication(AbstractApplication application) {
        AbstractActivity.application = application;
    }
}
