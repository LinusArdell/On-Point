package com.test.onpoint.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.Class.RiwayatDataClass;
import com.test.onpoint.R;

import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatHolder>{

    private Context context;
    private List<RiwayatDataClass> dataList;

    public RiwayatAdapter(@NonNull Context context, List<RiwayatDataClass> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RiwayatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_history, parent, false);
        return new RiwayatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatHolder holder, int position) {
        RiwayatDataClass data = dataList.get(position);

        String tanggal = data.getDataDate();
        String waktu = data.getDataTime();
        String hari = tanggal + " , " + waktu;

        if (data.isPeriksa() == true){
            holder.vhStatus.setText("Checked");
        } else {
            holder.vhStatus.setText("Not Checked");
            holder.vhStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.vhQR.setText(data.getQrCode());
        holder.vhLokasi.setText(data.getLokasi());
        holder.vhUsername.setText(data.getUserName());
        holder.vhTanggal.setText(hari);
        holder.dtKodeQR.setText(data.getQrCode());
        holder.dtLokasi.setText(data.getLokasi());
        holder.dtUser.setText(data.getUserName());

        holder.vhLatitude.setText(String.valueOf(data.getLatitude()));
        holder.vhLongitude.setText(String.valueOf(data.getLongitude()));

        boolean isExpanded = data.isExpanded();
        if (isExpanded) {
            expandView(holder.vhExpandableCard);
            holder.vhDetail.setText("Tampilkan Lebih sedikit...");
        } else {
            collapseView(holder.vhExpandableCard);
        }

        holder.vhDetail.setOnClickListener(v -> {
            data.setExpanded(!data.isExpanded());
            notifyItemChanged(position);
        });
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

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class RiwayatHolder extends RecyclerView.ViewHolder {

    TextView vhQR, vhLokasi, vhUsername, vhTanggal, vhLatitude, vhLongitude, vhDetail, vhTextview, vhStatus, dtKodeQR, dtLokasi, dtUser;
    ConstraintLayout vhExpandableCard;

    RelativeLayout vhRelativeEdit, vhRelativeHistory;

    public RiwayatHolder(@NonNull View itemView) {
        super(itemView);

        vhQR = itemView.findViewById(R.id.recQr);
        vhLokasi = itemView.findViewById(R.id.tvLokasi);
        vhUsername = itemView.findViewById(R.id.tvUser);
        vhTanggal = itemView.findViewById(R.id.tvTanggal);
        vhLatitude = itemView.findViewById(R.id.tvLatitude);
        vhLongitude = itemView.findViewById(R.id.tvLongitude);
        vhTextview = itemView.findViewById(R.id.textView);

        vhExpandableCard = itemView.findViewById(R.id.expandableCard);
        vhDetail = itemView.findViewById(R.id.recDetail);

        vhRelativeEdit = itemView.findViewById(R.id.relativeLayout);
        vhRelativeHistory = itemView.findViewById(R.id.relativeLayout2);
        vhStatus = itemView.findViewById(R.id.tvStatus);

        dtKodeQR = itemView.findViewById(R.id.recDetailQR);
        dtLokasi = itemView.findViewById(R.id.detailLokasi);
        dtUser = itemView.findViewById(R.id.detailPemeriksa);
    }
}