package ch.ethz.inf.vs.piremote.core;

import android.support.v7.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    protected AbstractApplication application;

    public AbstractApplication getAbstractApplication() {
        return application;
    }

    public void setApplication(AbstractApplication application) {
        this.application = application;
    }
}
