package mx.org.bamx.sigo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static mx.org.bamx.sigo.R.id.communities;

/**
 * CommunityActivity is used for displaying the available communities
 */
public class CommunityActivity extends Sigo implements AdapterView.OnItemClickListener, PropertyChangeListener{

    private final DBHelper db = new DBHelper(CommunityActivity.this);
    private ArrayAdapter<String> adapter;
    private ListView lv;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.addChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(communities);
        lv.setOnItemClickListener(this);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);

        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CommunityActivity.this, AddEditEntryActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.push_left_in, R.anim.fade_out);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        setCurrentCommunity(lv.getItemAtPosition(position).toString());
        Intent i = new Intent(CommunityActivity.this, FamilyActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.push_left_in, R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        setCurrentCommunity(null);
        updateCommunitiesList();
        setTitle(R.string.communities);
        if (isSyncing()) {
            rotateSyncItem(menu);
        } else {
            stopRotateSyncItem(menu);
        }
        super.onStart();
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
     * Updates the current list of communities displayed in the activity.
     */
    public void updateCommunitiesList() {
        adapter.clear();
        for (String community : db.getCommunities()) {
            adapter.add(community);
        }
    }

}
