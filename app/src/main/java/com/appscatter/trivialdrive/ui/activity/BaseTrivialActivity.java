package com.appscatter.trivialdrive.ui.activity;

import com.appscatter.iab.core.ASIab;
import com.appscatter.iab.core.api.AdvancedIabHelper;
import com.appscatter.iab.core.billing.BillingProvider;
import com.appscatter.iab.core.listener.OnSetupListener;
import com.appscatter.iab.core.model.event.SetupResponse;
import com.appscatter.iab.core.model.event.SetupStartedEvent;
import com.appscatter.trivialdrive.Helper;
import com.appscatter.trivialdrive.OnProviderPickerListener;
import com.appscatter.trivialdrive.Provider;
import com.appscatter.trivialdrive.R;
import com.appscatter.trivialdrive.TrivialBilling;
import com.appscatter.trivialdrive.ui.fragment.ProviderPickerDialogFragment;
import com.makeramen.dragsortadapter.DragSortAdapter;
import com.makeramen.dragsortadapter.DragSortShadowBuilder;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public abstract class BaseTrivialActivity extends AppCompatActivity
        implements OnProviderPickerListener {

    private static final String FRAGMENT_PROVIDER_PICKER = "provider_picker";


    private AdvancedIabHelper iabHelper;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private BaseTrivialActivity.Adapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iabHelper = ASIab.getAdvancedHelper();
        iabHelper.register();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final ProviderPickerDialogFragment fragment = (ProviderPickerDialogFragment) fragmentManager
                .findFragmentByTag(FRAGMENT_PROVIDER_PICKER);
        if (fragment != null) {
            fragment.setListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        iabHelper.unregister();
        if (drawerLayout != null) {
            drawerLayout.removeDrawerListener(drawerToggle);
        }
        super.onDestroy();
    }

    @Override
    public void setContentView(final int layoutResID) {
        super.setContentView(R.layout.activity_trivial);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        final View view = getLayoutInflater().inflate(layoutResID, drawerLayout, false);
        drawerLayout.addView(view, 0);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        adapter = new BaseTrivialActivity.Adapter(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        if (savedInstanceState == null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onProviderPicked(final Provider provider) {
        adapter.addItem(provider);
    }

    protected abstract void onFortumoButtonClicked(HeaderViewHolder headerViewHolder);
    protected abstract void onInitButtonClicked(HeaderViewHolder headerViewHolder);

    private class Adapter extends DragSortAdapter<DragSortAdapter.ViewHolder> {

        private static final int TYPE_HEADER = -1;
        private static final int TYPE_FOOTER = -2;
        private static final int TYPE_ITEM = 0;

        private final int headerCount = 1;

        private final List<Provider> items;
        private final LayoutInflater inflater;

        public Adapter(final RecyclerView recyclerView) {
            super(recyclerView);
            inflater = getLayoutInflater();
            items = new ArrayList<>(TrivialBilling.getProviders());
        }

        private boolean showFooter() {
            return items.size() < Provider.values().length;
        }

        private void addItem(final Provider provider) {
            if (items.contains(provider)) {
                return;
            }
            items.add(provider);
            TrivialBilling.setProviders(items);
            notifyItemInserted(headerCount + items.size());
        }

        private void deleteItem(final Provider provider) {
            final int index;
            if ((index = items.indexOf(provider)) < 0) {
                return;
            }
            items.remove(index);
            TrivialBilling.setProviders(items);
            notifyItemRemoved(headerCount + index);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent,
                final int viewType) {
            final View view;
            switch (viewType) {
                case TYPE_HEADER:
                    view = inflater.inflate(R.layout.item_drawer_header, parent, false);
                    return new BaseTrivialActivity.HeaderViewHolder(this, view);
                case TYPE_FOOTER:
                    view = inflater.inflate(R.layout.item_drawer_footer, parent, false);
                    return new BaseTrivialActivity.FooterViewHolder(this, view);
                case TYPE_ITEM:
                    view = inflater.inflate(R.layout.item_drawer, parent, false);
                    return new BaseTrivialActivity.ItemViewHolder(this, view);
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (getItemViewType(position) == TYPE_ITEM) {
                final BaseTrivialActivity.ItemViewHolder viewHolder = (BaseTrivialActivity.ItemViewHolder) holder;
                final Provider provider = items.get(position - headerCount);
                viewHolder.setProvider(provider);
                final boolean isDragged = getDraggingId() == getItemId(position);
                viewHolder.itemView.setVisibility(isDragged ? INVISIBLE : VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return headerCount + items.size() + (showFooter() ? 1 : 0);
        }

        @Override
        public long getItemId(final int position) {
            switch (getItemViewType(position)) {
                case TYPE_HEADER:
                    return TYPE_HEADER;
                case TYPE_FOOTER:
                    return TYPE_FOOTER;
                default:
                    return items.get(position - headerCount).ordinal();
            }
        }

        @Override
        public int getItemViewType(final int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (position >= headerCount + items.size()) {
                return TYPE_FOOTER;
            }
            return TYPE_ITEM;
        }

        @Override
        public int getPositionForId(final long l) {
            switch ((int) l) {
                case TYPE_HEADER:
                    return 0;
                case TYPE_FOOTER:
                    return items.size();
                default:
                    return headerCount + items.indexOf(Provider.values()[(int) l]);
            }
        }

        @Override
        public boolean move(final int from, final int to) {
            if (getItemViewType(to) != TYPE_ITEM) {
                return false;
            }
            Collections.swap(items, from - headerCount, to - headerCount);
            TrivialBilling.setProviders(items);
            return true;
        }
    }

    protected class HeaderViewHolder extends DragSortAdapter.ViewHolder
            implements View.OnClickListener, OnSetupListener, AdapterView.OnItemSelectedListener {

        private final Spinner spinHelper;
        private final TextView tvSetupStatus;
        private final TextView tvSetupProvider;
        private final ProgressBar pbSetup;
        private final Button btnFortumo;
        private final Button btnInit;
        private final Button btnSetup;
        private final CheckedTextView ctvAutoRecover;

        public HeaderViewHolder(final DragSortAdapter<?> dragSortAdapter, final View itemView) {
            super(dragSortAdapter, itemView);
            spinHelper = (Spinner) itemView.findViewById(R.id.spin_helper);
            tvSetupStatus = (TextView) itemView.findViewById(R.id.tv_setup_status);
            tvSetupProvider = (TextView) itemView.findViewById(R.id.tv_setup_provider);
            pbSetup = (ProgressBar) itemView.findViewById(R.id.pb_setup);
            btnFortumo = (Button) itemView.findViewById(R.id.btn_fortumo);
            btnInit = (Button) itemView.findViewById(R.id.btn_init);
            btnSetup = (Button) itemView.findViewById(R.id.btn_setup);
            ctvAutoRecover = (CheckedTextView) itemView.findViewById(R.id.ctv_auto_recover);

            final BaseTrivialActivity.HelpersAdapter adapter = new BaseTrivialActivity.HelpersAdapter();
            spinHelper.setAdapter(adapter);
            spinHelper.setSelection(adapter.getPosition(TrivialBilling.getHelper()));
            spinHelper.setOnItemSelectedListener(this);

            btnFortumo.setOnClickListener(this);
            btnInit.setOnClickListener(this);
            btnSetup.setOnClickListener(this);
            ctvAutoRecover.setChecked(TrivialBilling.isAutoRecover());
            ctvAutoRecover.setOnClickListener(this);

            iabHelper.addSetupListener(this);
        }


        protected void setSetupResponse(final SetupResponse setupResponse) {
            pbSetup.setVisibility(INVISIBLE);
            tvSetupProvider.setVisibility(VISIBLE);
            final boolean setupSuccessful = setupResponse != null && setupResponse.isSuccessful();
            final int visibility = setupSuccessful ? VISIBLE : INVISIBLE;
            tvSetupStatus.setVisibility(visibility);
            if (setupSuccessful) {
                final BillingProvider billingProvider = setupResponse.getBillingProvider();
                //noinspection ConstantConditions
                final String name = billingProvider.getName();
                final Provider provider = Provider.getByName(name);
                tvSetupProvider.setText(provider == null ? name : getString(provider.getNameId()));
                tvSetupStatus.setText(setupResponse.getStatus().toString());
            } else {
                tvSetupProvider.setText(R.string.setup_no_provider);
            }
        }

        @Override
        public void onItemSelected(final AdapterView<?> parent, final View view, final int position,
                final long id) {
            final Helper helper = (Helper) parent.getItemAtPosition(position);
            if (helper != TrivialBilling.getHelper()) {
                TrivialBilling.setHelper(helper);
                startActivity(new Intent(BaseTrivialActivity.this, LauncherActivity.class));
                finish();
            }
        }

        @Override
        public void onNothingSelected(final AdapterView<?> parent) { }

        @Override
        public void onClick(final View v) {
            if (v == btnFortumo) {
                onFortumoButtonClicked(this);

            } else if (v == btnInit) {
                onInitButtonClicked(this);

            } else if (v == btnSetup) {
                ASIab.setup();
            } else if (v == ctvAutoRecover) {
                ctvAutoRecover.toggle();
                TrivialBilling.setAutoRecover(ctvAutoRecover.isChecked());
            }
        }

        @Override
        public void onSetupStarted(@NonNull final SetupStartedEvent setupStartedEvent) {
            pbSetup.setVisibility(VISIBLE);
            tvSetupStatus.setVisibility(INVISIBLE);
            tvSetupProvider.setVisibility(INVISIBLE);
        }

        @Override
        public void onSetupResponse(@NonNull final SetupResponse setupResponse) {
            setSetupResponse(setupResponse);
        }
    }

    private class FooterViewHolder extends DragSortAdapter.ViewHolder
            implements View.OnClickListener {

        private final View btnAdd;

        public FooterViewHolder(final DragSortAdapter<?> dragSortAdapter, final View itemView) {
            super(dragSortAdapter, itemView);
            btnAdd = itemView.findViewById(R.id.btn_add);
            btnAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(FRAGMENT_PROVIDER_PICKER) == null) {
                final Collection<Provider> providers = new ArrayList<>(
                        Arrays.asList(Provider.values()));
                providers.removeAll(adapter.items);
                final ProviderPickerDialogFragment dialogFragment = ProviderPickerDialogFragment
                        .getInstance(providers);
                dialogFragment.setListener(BaseTrivialActivity.this);
                dialogFragment.show(fragmentManager, FRAGMENT_PROVIDER_PICKER);
            }
        }
    }

    private class ItemViewHolder extends DragSortAdapter.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final View btnDelete;
        private final TextView tvProvider;
        private Provider provider;

        public ItemViewHolder(final DragSortAdapter<?> dragSortAdapter, final View itemView) {
            super(dragSortAdapter, itemView);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvProvider = (TextView) itemView.findViewById(R.id.tv_provider);
            btnDelete.setOnClickListener(this);
            tvProvider.setOnLongClickListener(this);
        }

        public void setProvider(final Provider provider) {
            this.provider = provider;
            tvProvider.setText(provider.getNameId());
        }

        @Override
        public void onClick(final View v) {
            adapter.deleteItem(provider);
        }

        @Override
        public boolean onLongClick(final View v) {
            startDrag();
            return true;
        }

        @Override
        public View.DragShadowBuilder getShadowBuilder(final View itemView,
                final Point touchPoint) {
            return new DragSortShadowBuilder(itemView, touchPoint);
        }
    }

    private class HelpersAdapter extends ArrayAdapter<Helper> {

        private final LayoutInflater inflater;

        public HelpersAdapter() {
            super(BaseTrivialActivity.this, -1, Helper.values());
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            inflater = getLayoutInflater();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,
                        false);
            }
            final TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position).getNameId());
            return view;
        }
    }


}
