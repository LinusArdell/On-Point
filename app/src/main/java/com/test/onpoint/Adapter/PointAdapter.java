package com.test.onpoint.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

        PointDataClass data = dataList.get(position);

        String tanggal = data.getDataDate();
        String waktu = data.getDataTime();
        String hari = tanggal + " , " + waktu;

        holder.vhQR.setText(data.getQrCode());
        holder.vhLokasi.setText(data.getLokasi());
        holder.vhUsername.setText(data.getUserName());
        holder.vhTanggal.setText(hari);

        holder.vhLatitude.setText(String.valueOf(data.getLatitude()));
        holder.vhLongitude.setText(String.valueOf(data.getLongitude()));

        boolean isExpanded = data.isExpanded();
        if (isExpanded) {
            expandView(holder.vhExpandableCard);
            holder.vhDetail.setText("Tampilkan lebih sedikit...");
        } else {
            collapseView(holder.vhExpandableCard);
        }

        holder.vhDetail.setOnClickListener(v -> {
            data.setExpanded(!data.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void expandView(View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.0f);
        view.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(null);
    }

    private void collapseView(View view) {
        view.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView vhQR, vhLokasi, vhUsername, vhTanggal, vhLatitude, vhLongitude, vhDetail, vhTextview;
    ConstraintLayout vhExpandableCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        vhQR = itemView.findViewById(R.id.recQr);
        vhLokasi = itemView.findViewById(R.id.recLokasi);
        vhUsername = itemView.findViewById(R.id.recUser);
        vhTanggal = itemView.findViewById(R.id.recTanggal);
        vhLatitude = itemView.findViewById(R.id.recLatitude);
        vhLongitude = itemView.findViewById(R.id.recLongitude);
        vhTextview = itemView.findViewById(R.id.textView);

        vhExpandableCard = itemView.findViewById(R.id.expandableCard);
        vhDetail = itemView.findViewById(R.id.recDetail);
    }
}