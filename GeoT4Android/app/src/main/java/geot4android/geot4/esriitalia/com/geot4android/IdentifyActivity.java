package geot4android.geot4.esriitalia.com.geot4android;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import geot4android.geot4.esriitalia.com.geot4android.component.IdentifyAdapter;

public class IdentifyActivity extends Activity {

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        openValues();

        ImageButton imgprevious = (ImageButton) findViewById(R.id.imgprevious);
        ImageButton imgnext = (ImageButton) findViewById(R.id.imgnext);
        imgprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    void openValues()
    {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/VertigoPlusFLF.ttf");

        ImageView imgSymbol = (ImageView)findViewById(R.id.imgSymbol);
        imgSymbol.setImageBitmap(MainActivity.bpms.get(position));

        ArrayList<HashMap> list = new ArrayList<>();
        Map<String, Object> vals = MainActivity.values.get(position);
        for (String key: vals.keySet()) {
            HashMap<String,String> rowvalue = new HashMap<>();
            rowvalue.put(IdentifyAdapter.FIRST_COLUMN,key);
            rowvalue.put(IdentifyAdapter.SECOND_COLUMN,vals.get(key).toString());
            list.add(rowvalue);
        }

        IdentifyAdapter listAdapter = new IdentifyAdapter(IdentifyActivity.this,list);
        ListView lstValues = (ListView) findViewById(R.id.lstValues);
        lstValues.setAdapter(listAdapter);

        ImageButton imgprevious = (ImageButton) findViewById(R.id.imgprevious);
        ImageButton imgnext = (ImageButton) findViewById(R.id.imgnext);

        if (position==0) imgprevious.setVisibility(View.INVISIBLE);
        else imgprevious.setVisibility(View.VISIBLE);
        if (position==(MainActivity.bpms.size()-1)) imgnext.setVisibility(View.INVISIBLE);
        else imgnext.setVisibility(View.VISIBLE);

        TextView txtPageNumber = (TextView) findViewById(R.id.txtPageNumber);
        txtPageNumber.setTypeface(typeface);
        txtPageNumber.setText("Track "+(position+1)+" of "+MainActivity.bpms.size());
    }

    void next()
    {
        position++;
        if (position>=MainActivity.bpms.size()) position=MainActivity.bpms.size()-1;
        openValues();
    }

    void previous()
    {
        position--;
        if (position<0) position=0;
        openValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_identify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
