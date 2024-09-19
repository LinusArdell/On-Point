package com.test.onpoint.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.onpoint.Adapter.PointAdapter;
import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.R;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class FragmentBeranda extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReferences, mDatabase;
    PointAdapter adapter;
    List<PointDataClass> dataList;
    GridLayoutManager gridLayoutManager;
    ValueEventListener eventListener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda, container, false);

        recyclerView = view.findViewById(R.id.berandaRecyclerView);

        setDatabaseReferences();

        return view;
    }

    private void setDatabaseReferences(){
        dataList = new ArrayList<>();

        adapter = new PointAdapter(getContext(), dataList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

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
    }
}