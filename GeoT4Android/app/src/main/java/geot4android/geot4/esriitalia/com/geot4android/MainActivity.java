package geot4android.geot4.esriitalia.com.geot4android;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.popup.Popup;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.runtime.LicenseLevel;
import com.esri.core.runtime.LicenseResult;
import com.esri.core.symbol.advanced.Message;
import com.esri.core.symbol.advanced.MessageGroupLayer;
import com.esri.core.symbol.advanced.MessageProcessor;
import com.esri.core.symbol.advanced.SymbolDictionary;
import com.esri.core.symbol.advanced.SymbolProperties;
import com.esri.core.tasks.identify.IdentifyParameters;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import org.xmlpull.v1.XmlPullParserException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import geot4android.geot4.esriitalia.com.geot4android.component.Compass;
import geot4android.geot4.esriitalia.com.geot4android.model.LayersParser;
import geot4android.geot4.esriitalia.com.geot4android.model.MessageParser;

public class MainActivity extends Activity {

    MapView mMapView;
    Typeface typeface;
    IdentifyParameters params = null;
    private String[] mFileList;
    private File mPath = new File(Environment.getExternalStorageDirectory() + "//Download//nvgfilestest//");
    private File mConfPath = new File(Environment.getExternalStorageDirectory() + "//Download//geot4configuration//");
    private String mConfFile = "layersconfig.xml";
    private String mChosenFile;
    private static final int DIALOG_LOAD_FILE = 1000;
    private MessageGroupLayer messageGrLayer;
    MessageProcessor mProcessor;
    boolean infoenabled = false;
    boolean playenabled = false;
    ArrayList<String> sics;
    ArrayList<String> names;
    public static ArrayList<Bitmap> bpms;
    public static ArrayList<Map<String, Object>> values;
    Thread player = null;
    udpMessages udpmessages;

    ArrayList<LayersParser.ServiceLayer> serviceLayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LicenseResult licenseResult = ArcGISRuntime.setClientId("nko2NvPdAVSzCMS3");
        LicenseLevel licenseLevel = ArcGISRuntime.License.getLicenseLevel();
        setContentView(R.layout.activity_main);

        typeface = Typeface.createFromAsset(getAssets(),"fonts/VertigoPlusFLF.ttf");

        serviceLayers = openLayers();

        mMapView = (MapView)findViewById(R.id.map);
        mMapView.setEsriLogoVisible(true);
        mMapView.enableWrapAround(true);
        mMapView.setAllowRotationByPinch(true);

        addBasemap();

        Compass mCompass = new Compass(this, null, mMapView);
        mMapView.addView(mCompass);

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (OnStatusChangedListener.STATUS.INITIALIZED == status) {
                    addLayers();
                    trackPrepare();
                }
            }
        });

        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float v, float v2) {
                if (infoenabled) viewTrackInfo(v,v2);
            }
        });

        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        FloatingActionButton centerLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlMap = new ImageView(this);
        final ImageView rlInfo = new ImageView(this);
        ImageView rlOpen = new ImageView(this);
        final ImageView rlPlay = new ImageView(this);
        ImageView rlSettings = new ImageView(this);
        ImageView rlLayers = new ImageView(this);

        FrameLayout.LayoutParams subContents = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.sub_action_menu_size), getResources().getDimensionPixelSize(R.dimen.sub_action_menu_size));
        FrameLayout.LayoutParams subMargins = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.sub_action_content_menu_size), getResources().getDimensionPixelSize(R.dimen.sub_action_content_menu_size));
        subMargins.setMargins(getResources().getDimensionPixelSize(R.dimen.sub_action_menu_margin),
                getResources().getDimensionPixelSize(R.dimen.sub_action_menu_margin),
                getResources().getDimensionPixelSize(R.dimen.sub_action_menu_margin),
                getResources().getDimensionPixelSize(R.dimen.sub_action_menu_margin));
        rLSubBuilder.setLayoutParams(subContents);

        rlMap.setImageDrawable(getResources().getDrawable(R.drawable.map));
        rlOpen.setImageDrawable(getResources().getDrawable(R.drawable.open));
        rlPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
        rlSettings.setImageDrawable(getResources().getDrawable(R.drawable.settings));
        rlInfo.setImageDrawable(getResources().getDrawable(R.drawable.info));
        rlLayers.setImageDrawable(getResources().getDrawable(R.drawable.layers));

        final FloatingActionMenu centerLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlMap, subMargins).build())
                .addSubActionView(rLSubBuilder.setContentView(rlOpen, subMargins).build())
                .addSubActionView(rLSubBuilder.setContentView(rlPlay, subMargins).build())
                .addSubActionView(rLSubBuilder.setContentView(rlLayers, subMargins).build())
                .addSubActionView(rLSubBuilder.setContentView(rlInfo, subMargins).build())
                .addSubActionView(rLSubBuilder.setContentView(rlSettings, subMargins).build())
                .attachTo(centerLowerButton)
                .setStartAngle(-170)
                .setEndAngle(-10)
                .build();

        centerLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        rlMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerLowerMenu.close(true);
                changeBasemap();
            }
        });

        rlSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerLowerMenu.close(true);
                settings();
            }
        });

        rlInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerLowerMenu.close(true);
                infoenabled=!infoenabled;
                if (infoenabled) rlInfo.setImageDrawable(getResources().getDrawable(R.drawable.infoon));
                else rlInfo.setImageDrawable(getResources().getDrawable(R.drawable.info));
            }
        });

        rlLayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerLowerMenu.close(true);
                changeLayers();
            }
        });

        rlOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerLowerMenu.close(true);
                if(mPath.exists()) {
                    FilenameFilter filter = new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String filename) {
                            File sel = new File(dir, filename);
                            return true;
                        }

                    };
                    mFileList = mPath.list(filter);
                }
                else {
                    mFileList= new String[0];
                }
                openFile();
            }
        });

        rlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerLowerMenu.close(true);
                playenabled = !playenabled;
                if (playenabled) {
                    if (player==null) {
                        udpmessages = new udpMessages();
                        player = new Thread(udpmessages);
                        player.start();
                    }
                    rlPlay.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                }
                else
                {
                    /*player.interrupt();
                    try{
                        player.join();
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    udpmessages = null;*/
                    rlPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }

            }
        });
    }

    private void trackPrepare()
    {
        try {
            SharedPreferences prefs = getSharedPreferences("geot4prefs", Context.MODE_PRIVATE);
            int symbol_type = prefs.getInt("symbol_type", 0);
            double symbolscale = prefs.getFloat("symbolscale", 1.5f);
            SymbolDictionary.DictionaryType dt = SymbolDictionary.DictionaryType.MIL2525C;
            if (symbol_type==0) dt = SymbolDictionary.DictionaryType.APP6B;
            messageGrLayer = new MessageGroupLayer(dt,symbolscale);
            messageGrLayer.setName("Tracks");
            mMapView.addLayer(messageGrLayer);
            mProcessor = messageGrLayer.getMessageProcessor();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }


        sics = new ArrayList<>();
        names = new ArrayList<>();

        List<SymbolProperties> symbols;
        try {
            symbols = mProcessor.getSymbolDictionary().findSymbols();
            String name = "";
            for (SymbolProperties props : symbols) {
                Map<String, String> symbVals = props.getValues();
                String sicCode = symbVals.get("SymbolID");
                name = props.getName();
                sics.add(sicCode);
                names.add(name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<LayersParser.ServiceLayer> openLayers() {
        File file = new File(mConfPath+"/"+mConfFile);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LayersParser parser = new LayersParser();

        List<LayersParser.ServiceLayer> serviceLayers = null;
        try {
            serviceLayers = parser.parse(is);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayList<LayersParser.ServiceLayer> result = new ArrayList<>();
        for (LayersParser.ServiceLayer layer : serviceLayers) {
            result.add(layer);
        }
        return result;
    }

    private Dialog changeLayers() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose layers");

        int i=0;
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Boolean> selects = new ArrayList<>();
        for (Layer slayer:mMapView.getLayers())
        {
            if (i>0) {
                items.add(slayer.getName());
                selects.add(slayer.getOpacity() > 0);
            }
            i++;
        }

        boolean[] sels = new boolean[selects.size()];
        String[] itms = new String[items.size()];
        for (int l=0; l<selects.size(); l++) sels[l]=selects.get(l);
        for (int l=0; l<items.size(); l++) itms[l]=items.get(l);

        builder.setMultiChoiceItems(itms,sels, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                float op = 0f;
                if (isChecked) op=1f;
                mMapView.getLayer(which+1).setOpacity(op);
            }
        });

        dialog = builder.show();
        return dialog;
    }

    void addBasemap()
    {
        int i=0;
        for (LayersParser.ServiceLayer layer : serviceLayers) {
            if (i<3 && layer.visibility)
            {
                addLayer(layer);
            }
            i++;
        }
    }

    void addLayers()
    {
        int i=0;
        for (LayersParser.ServiceLayer layer : serviceLayers) {
            if (i>2) addLayer(layer);
            i++;
        }
    }

    void addLayer(LayersParser.ServiceLayer layer)
    {
        if (layer.type.equals("TiledMapServiceLayer"))
        {
            ArcGISTiledMapServiceLayer tiled = new ArcGISTiledMapServiceLayer(layer.url);
            tiled.setName(layer.name);
            if (!layer.visibility) tiled.setOpacity(0f);
            mMapView.addLayer(tiled);
        }
        else if (layer.type.equals("DynamicMapServiceLayer"))
        {
            ArcGISDynamicMapServiceLayer dynamic = new ArcGISDynamicMapServiceLayer(layer.url);
            dynamic.setName(layer.name);
            if (!layer.visibility) dynamic.setOpacity(0f);
            mMapView.addLayer(dynamic);
        }
    }

    void addLayer(LayersParser.ServiceLayer layer, int index)
    {
        if (layer.type.equals("TiledMapServiceLayer"))
        {
            ArcGISTiledMapServiceLayer tiled = new ArcGISTiledMapServiceLayer(layer.url);
            tiled.setName(layer.name);
            tiled.setVisible(layer.visibility);
            mMapView.addLayer(tiled,index);
        }
        else if (layer.type.equals("DynamicMapServiceLayer"))
        {
            ArcGISDynamicMapServiceLayer dynamic = new ArcGISDynamicMapServiceLayer(layer.url);
            dynamic.setName(layer.name);
            dynamic.setVisible(layer.visibility);
            mMapView.addLayer(dynamic,index);
        }
    }

    void viewTrackInfo(float x, float y)
    {
        MessageProcessor mp = messageGrLayer.getMessageProcessor();
        SymbolDictionary symbDict = mp.getSymbolDictionary();
        boolean cicledone = false;
        bpms = new ArrayList<>();
        values = new ArrayList<>();
        for (Layer l:messageGrLayer.getLayers())
        {
            GraphicsLayer gl = (GraphicsLayer)l;
            for (int gid:gl.getGraphicIDs(x,y,10))
            {
                cicledone=true;
                Bitmap bmtrack = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
                Graphic g = gl.getGraphic(gid);
                String sic = g.getAttributeValue("sic").toString();
                if (sic.length()>10) sic=sic.substring(0, 10);
                boolean img = false;
                if (sics.indexOf(sic)!=-1) img = symbDict.getSymbolImage(names.get(sics.indexOf(sic)), bmtrack);
                bpms.add(bmtrack);
                Map<String, Object> vals = g.getAttributes();
                values.add(vals);
            }
        }

/*        Popup popup;
        ArrayList<Integer> layids = new ArrayList<>();
        for (Layer l:mMapView.getLayers()) {
            if (!l.getClass().equals(MessageGroupLayer.class))
            {
                Point identifyPoint = mMapView.toMapPoint(x, y);
                params = new IdentifyParameters();
                params.setTolerance(10);
                params.setDPI(98);
                params.setLayers(new int[] { 4 });
                params.setLayerMode(IdentifyParameters.ALL_LAYERS);
                params.setGeometry(identifyPoint);
                params.setSpatialReference(mMapView.getSpatialReference());
                params.setMapHeight(mMapView.getHeight());
                params.setMapWidth(mMapView.getWidth());
                params.setReturnGeometry(false);
                Envelope env = new Envelope();
                mMapView.getExtent().queryEnvelope(env);
                params.setMapExtent(env);

            }
        }*/

        if (cicledone)
        {
            Intent intent = new Intent(this, IdentifyActivity.class);
            startActivity(intent);
        }
    }

    class udpMessages implements Runnable {
        @Override
        public void run() {
            if (playenabled) {
                try {
                    String text;
                    SharedPreferences prefs = getSharedPreferences("geot4prefs", Context.MODE_PRIVATE);
                    int server_port = prefs.getInt("udp_port", 1234);
                    byte[] message = new byte[1500];
                    DatagramPacket p = new DatagramPacket(message, message.length);
                    DatagramSocket s = new DatagramSocket(server_port);
                    s.receive(p);
                    text = new String(message, 0, p.getLength());

                    InputStream is = new ByteArrayInputStream(text.getBytes());
                    MessageParser parser = new MessageParser();

                    List<MessageParser.GeoMessage> geoMessages = null;
                    try {
                        geoMessages = parser.parse(is);

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally { // make sure InputStream is closed after the app is
                        // finished
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    s.close();
                    for (MessageParser.GeoMessage geoMessage : geoMessages) {
                        Message msg = new Message();
                        msg.setID(geoMessage.id);
                        if (geoMessage.type != null) msg.setProperty("_type", geoMessage.type);
                        if (geoMessage.name != null) msg.setProperty("_name", geoMessage.name);
                        if (geoMessage.action != null)
                            msg.setProperty("_action", geoMessage.action);
                        if (geoMessage.id != null) msg.setProperty("_id", geoMessage.id);
                        if (geoMessage.wkid != null) msg.setProperty("_wkid", geoMessage.wkid);
                        if (geoMessage.controlpoints != null)
                            msg.setProperty("_control_points", geoMessage.controlpoints);
                        if (geoMessage.sic != null) msg.setProperty("sic", geoMessage.sic);
                        if (geoMessage.uniquedesignation != null)
                            msg.setProperty("uniquedesignation", geoMessage.uniquedesignation);
                        if (geoMessage.quantity != null)
                            msg.setProperty("quantity", geoMessage.quantity);
                        if (geoMessage.direction != null)
                            msg.setProperty("direction", geoMessage.direction);
                        if (geoMessage.type != null) msg.setProperty("type", geoMessage.type);
                        if (geoMessage.datetimevalid != null)
                            msg.setProperty("datetimevalid", geoMessage.datetimevalid);
                        if (geoMessage.speed != null) msg.setProperty("speed", geoMessage.speed);
                        if (geoMessage.owningunit != null)
                            msg.setProperty("owningunit", geoMessage.owningunit);
                        if (geoMessage.status911 != null)
                            msg.setProperty("status911", geoMessage.status911);
                        if (geoMessage.fuel_state != null)
                            msg.setProperty("fuel_state", geoMessage.fuel_state);
                        if (geoMessage.rel_info != null)
                            msg.setProperty("rel_info", geoMessage.rel_info);
                        mProcessor.processMessage(msg);
                    }
                    new Thread(new udpMessages()).start();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Dialog openFile() {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose file");
        if(mFileList == null) {
            dialog = builder.create();
            return dialog;
        }
        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mFileList[which];
                openTracks();
            }
        });

        dialog = builder.show();
        return dialog;
    }

    private void openTracks() {

        File file = new File(mPath+"/"+mChosenFile);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        MessageParser parser = new MessageParser();

        List<MessageParser.GeoMessage> geoMessages = null;
        try {
            geoMessages = parser.parse(is);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // make sure InputStream is closed after the app is
            // finished
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (MessageParser.GeoMessage geoMessage : geoMessages) {
            Message message = new Message();
            message.setID(geoMessage.id);
            if (geoMessage.type!=null) message.setProperty("_type", geoMessage.type);
            if (geoMessage.name!=null) message.setProperty("_name", geoMessage.name);
            if (geoMessage.action!=null) message.setProperty("_action", geoMessage.action);
            if (geoMessage.id!=null) message.setProperty("_id", geoMessage.id);
            if (geoMessage.wkid!=null) message.setProperty("_wkid", geoMessage.wkid);
            if (geoMessage.controlpoints!=null) message.setProperty("_control_points", geoMessage.controlpoints);
            if (geoMessage.sic!=null) message.setProperty("sic", geoMessage.sic);
            if (geoMessage.uniquedesignation!=null) message.setProperty("uniquedesignation", geoMessage.uniquedesignation);
            if (geoMessage.quantity!=null) message.setProperty("quantity", geoMessage.quantity);
            if (geoMessage.direction!=null) message.setProperty("direction", geoMessage.direction);
            if (geoMessage.type!=null) message.setProperty("type", geoMessage.type);
            if (geoMessage.datetimevalid!=null) message.setProperty("datetimevalid", geoMessage.datetimevalid);
            if (geoMessage.speed!=null) message.setProperty("speed", geoMessage.speed);
            if (geoMessage.owningunit!=null) message.setProperty("owningunit", geoMessage.owningunit);
            if (geoMessage.status911!=null) message.setProperty("status911", geoMessage.status911);
            if (geoMessage.fuel_state!=null) message.setProperty("fuel_state", geoMessage.fuel_state);
            if (geoMessage.rel_info!=null) message.setProperty("rel_info", geoMessage.rel_info);
            mProcessor.processMessage(message);
        }
    }

    private void settings() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.settings_dialog);

        TextView txtSymbology = (TextView) dialog.findViewById(R.id.txtSymbology);
        txtSymbology.setTypeface(typeface);
        TextView txtScale = (TextView) dialog.findViewById(R.id.txtScale);
        txtScale.setTypeface(typeface);
        TextView txtUDP = (TextView) dialog.findViewById(R.id.txtUDP);
        txtUDP.setTypeface(typeface);

        Spinner spnSymbology = (Spinner)dialog.findViewById(R.id.spnSymbology);
        String[] symbologies = new String[]{"APP6B","Mil2525c"};
        SpinnerAdapter adapter = new SpinnerAdapter(
                MainActivity.this,
                R.layout.view_spinner_item,
                Arrays.asList(symbologies)
        );
        spnSymbology.setAdapter(adapter);
        final SharedPreferences prefs = getSharedPreferences("geot4prefs", Context.MODE_PRIVATE);
        int symbol_type = prefs.getInt("symbol_type", 0);
        spnSymbology.setSelection(symbol_type);
        spnSymbology.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("symbol_type", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spnScale = (Spinner)dialog.findViewById(R.id.spnScale);
        ArrayList<String> scales = new ArrayList<>();
        for (int i=0; i<11; i++)
        {
            double scvalue = 1.5+(((double)i)*0.1d);
            DecimalFormat df = new DecimalFormat("#.#");
            scales.add(df.format(scvalue));
        }
        SpinnerAdapter scadapter = new SpinnerAdapter(
                MainActivity.this,
                R.layout.view_spinner_item,
                scales
        );
        spnScale.setAdapter(scadapter);
        double symbolscale = prefs.getFloat("symbolscale", 1.5f);
        int selindex = (int)((symbolscale-1.5d)/0.1d);
        spnScale.setSelection(selindex);
        spnScale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double selvalue = 1.5d+(0.1d*(double)position);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("symbolscale", (float) selvalue);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner spnUDP = (Spinner)dialog.findViewById(R.id.spnUDP);
        ArrayList<String> udps = new ArrayList<>();
        for (int i=0; i<1500; i++)
        {
            int scvalue = 1000+i;
            udps.add(scvalue+"");
        }
        SpinnerAdapter scudp = new SpinnerAdapter(
                MainActivity.this,
                R.layout.view_spinner_item,
                udps
        );
        spnUDP.setAdapter(scudp);
        int server_port = prefs.getInt("udp_port", 1234);
        spnUDP.setSelection(server_port-1000);
        spnUDP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int udpvalue = 1000+position;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("udp_port", udpvalue);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Settings");
                alertDialog.setMessage("Please, restart APP to confirm changes");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        dialog.show();
    }

    private class SpinnerAdapter extends ArrayAdapter {

        private SpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(typeface);
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(typeface);
            return view;
        }
    }

    private void changeBasemap() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.basemaps_dialog);
        ImageButton imgTopo = (ImageButton) dialog.findViewById(R.id.imgTopo);
        imgTopo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayer(serviceLayers.get(0),0);
                mMapView.removeLayer(1);
                dialog.dismiss();
            }
        });
        TextView txtTopo = (TextView) dialog.findViewById(R.id.txtTopo);
        txtTopo.setTypeface(typeface);

        ImageButton imgSatellite = (ImageButton) dialog.findViewById(R.id.imgSatellite);
        imgSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayer(serviceLayers.get(1),0);
                mMapView.removeLayer(1);
                dialog.dismiss();
            }
        });
        TextView txtSatellite = (TextView) dialog.findViewById(R.id.txtSatellite);
        txtSatellite.setTypeface(typeface);

        ImageButton imgStreets = (ImageButton) dialog.findViewById(R.id.imgStreets);
        imgStreets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayer(serviceLayers.get(2),0);
                mMapView.removeLayer(1);
                dialog.dismiss();
            }
        });
        TextView txtStreets = (TextView) dialog.findViewById(R.id.txtStreets);
        txtStreets.setTypeface(typeface);
/*
        ImageButton imgNatGeo = (ImageButton) dialog.findViewById(R.id.imgNatGeo);
        imgNatGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.setMapOptions(new MapOptions(MapOptions.MapType.NATIONAL_GEOGRAPHIC));
                dialog.dismiss();
            }
        });

        TextView txtNatGeo = (TextView) dialog.findViewById(R.id.txtNatGeo);
        txtNatGeo.setTypeface(typeface);

        ImageButton imgOceans = (ImageButton) dialog.findViewById(R.id.imgOceans);
        imgOceans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.setMapOptions(new MapOptions(MapOptions.MapType.OCEANS));
                dialog.dismiss();
            }
        });
        TextView txtOceans = (TextView) dialog.findViewById(R.id.txtOceans);
        txtOceans.setTypeface(typeface);

        ImageButton imgGray = (ImageButton) dialog.findViewById(R.id.imgGray);
        imgGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.setMapOptions(new MapOptions(MapOptions.MapType.GRAY));
                dialog.dismiss();
            }
        });
        TextView txtGray = (TextView) dialog.findViewById(R.id.txtGray);
        txtGray.setTypeface(typeface);
        */
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
