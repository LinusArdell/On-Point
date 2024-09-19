package com.test.onpoint.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.R;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<PointDataClass> dataList;

    public PointAdapter(@NonNull Context context, List<PointDataClass> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String tanggal = dataList.get(position).getDataDate();
        String waktu = dataList.get(position).getDataTime();
        String hari = tanggal + " , " + waktu;

        holder.vhQR.setText(dataList.get(position).getQrCode());
        holder.vhLokasi.setText(dataList.get(position).getLokasi());
        holder.vhUsername.setText(dataList.get(position).getUserName());
        holder.vhTanggal.setText(hari);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView vhQR, vhLokasi, vhUsername, vhTanggal, vhLatitude, vhLongitude;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        vhQR = itemView.findViewById(R.id.recQr);
        vhLokasi = itemView.findViewById(R.id.recLokasi);
        vhUsername = itemView.findViewById(R.id.recUser);
        vhTanggal = itemView.findViewById(R.id.recTanggal);
        vhLatitude = itemView.findViewById(R.id.recLatitude);
        vhLongitude = itemView.findViewById(R.id.recLongitude);
    }
}