package mx.org.bamx.sigo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static mx.org.bamx.sigo.R.id.families;

/**
 * FamilyActivity is used for displaying the available families for a specific community
 */
public class FamilyActivity extends Sigo implements AdapterView.OnItemClickListener, PropertyChangeListener {

    private final DBHelper db = new DBHelper(FamilyActivity.this);
    private ArrayAdapter<FamilyItem> adapter;
    private ListView lv;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.addChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(families);
        lv.setOnItemClickListener(this);

        adapter = new ArrayAdapter<FamilyItem>(this,
                android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.addFamily);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FamilyActivity.this, AddEditEntryActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.fade_out);
            }
        });
    }

    @Override
    protected void onStart() {
        setTitle(getCurrentCommunity());
        updatefamiliesList();
        if (isSyncing()) {
            rotateSyncItem(menu);
        } else {
            stopRotateSyncItem(menu);
        }
        super.onStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FamilyItem familyItem = (FamilyItem) lv.getItemAtPosition(position);
        Intent i = new Intent(FamilyActivity.this, AddEditEntryActivity.class);
        i.putExtra(DB_ID, familyItem.getId());
        startActivity(i);
        overridePendingTransition(R.anim.push_left_in, R.anim.fade_out);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_communities, menu);
        this.menu = menu;
        if (isSyncing()) {
            rotateSyncItem(menu);
        } else {
            stopRotateSyncItem(menu);
        }
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!((Boolean) evt.getOldValue()) && (Boolean) evt.getNewValue()) {
            rotateSyncItem(menu);
        } else if ((Boolean) evt.getOldValue() && !((Boolean) evt.getNewValue())) {
            stopRotateSyncItem(menu);
        }
    }

    /**
     * Updates the current list of families displayed in the activity.
     */
    public void updatefamiliesList() {
        adapter.clear();
        for (FamilyItem family : db.getFamiliesForCommunity(getCurrentCommunity())) {
            adapter.add(family);
        }
    }
}
