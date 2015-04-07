package geot4android.geot4.esriitalia.com.geot4android.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import geot4android.geot4.esriitalia.com.geot4android.R;

/**
 * Created by giovannipalombi on 06/04/15.
 */
public class IdentifyAdapter extends BaseAdapter {

    public ArrayList<HashMap> list;
    Context context;
    Typeface typeface;

    public static String FIRST_COLUMN = "attribute_name";
    public static String SECOND_COLUMN = "attribute_value";

    public IdentifyAdapter(Context context, ArrayList<HashMap> list) {
        super();
        this.context = context;
        this.list = list;
        typeface = Typeface.createFromAsset(context.getAssets(),"fonts/VertigoPlusFLF.ttf");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =  ((Activity)context).getLayoutInflater();

        convertView = inflater.inflate(R.layout.identify_item, null);
        HashMap map = list.get(position);

        TextView txtname = (TextView) convertView.findViewById(R.id.txtname);
        txtname.setTypeface(typeface);
        txtname.setText(map.get(FIRST_COLUMN).toString());
        TextView txtvalue = (TextView) convertView.findViewById(R.id.txtvalue);
        txtvalue.setTypeface(typeface);
        txtvalue.setText(map.get(SECOND_COLUMN).toString());

        return convertView;
    }
}
