package group19.employeetracker;

import android.os.Bundle;
import android.widget.FrameLayout;

public class testlist extends NavActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_main2, contentFrameLayout);
    }
}
