package com.test.onpoint.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.onpoint.Adapter.PointAdapter;
import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    SearchView searchView;
    RecyclerView recyclerView;
    PointAdapter adapter;
    List<PointDataClass> dataList;
    DatabaseReference databaseReferences;
    ValueEventListener eventListener;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initializeUiComponent(view);

        return view;
    }

    public void initializeUiComponent(View view){
        searchView = view.findViewById(R.id.search);
        recyclerView = view.findViewById(R.id.recyclerSearch);

        dataList = new ArrayList<>();
        adapter = new PointAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        databaseReferences = FirebaseDatabase.getInstance().getReference("Check Point");

        eventListener = databaseReferences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();

                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    PointDataClass dataClass = itemSnapshot.getValue(PointDataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    searchList(newText);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    public void searchList(String text) {
        ArrayList<PointDataClass> searchList = new ArrayList<>();
        for (PointDataClass dataClass : dataList) {
            if (dataClass.getLokasi().toLowerCase().contains(text.toLowerCase())
                || dataClass.getQrCode().toLowerCase().contains(text.toLowerCase())
                || dataClass.getUserName().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            }
        }

        adapter.updateData(searchList);
    }
}