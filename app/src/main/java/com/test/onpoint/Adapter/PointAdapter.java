package com.test.onpoint.Adapter;

import android.animation.Animator;
import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.test.onpoint.Activity.EditActivity;
import com.test.onpoint.Activity.RiwayatActivity;
import com.test.onpoint.Class.PointDataClass;
import com.test.onpoint.Class.UserClass;
import com.test.onpoint.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PointAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Context context;
    private List<PointDataClass> dataList;
    FirebaseAuth userAuth;
    DatabaseReference userDatabase, riwayatDatabase, reference;
    public boolean success = false;

    public PointAdapter(@NonNull Context context, List<PointDataClass> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    public void updateData(List<PointDataClass> newData) {
        this.dataList = newData;
        notifyDataSetChanged();
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
        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser currentUser = userAuth.getCurrentUser();

        String tanggal = data.getDataDate();
        String waktu = data.getDataTime();
        String hari;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        try {
            Date dataDate = dateFormat.parse(tanggal);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);
            yesterday.set(Calendar.HOUR_OF_DAY, 0);
            yesterday.set(Calendar.MINUTE, 0);
            yesterday.set(Calendar.SECOND, 0);
            yesterday.set(Calendar.MILLISECOND, 0);

            if (dataDate.equals(today.getTime())) {
                hari = "Hari ini, " + waktu;
            } else if (dataDate.equals(yesterday.getTime())) {
                hari = "Kemarin, " + waktu;
            } else {
                hari = tanggal + " , " + waktu;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            hari = tanggal + " , " + waktu;
        }

        if (data.isPeriksa()) {
            holder.vhStatus.setText("Aman");
        } else {
            holder.vhStatus.setText("Belum di periksa");
            holder.vhStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

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

        holder.vhRelativeHistory.setOnClickListener(v -> {
            Intent i = new Intent(context, RiwayatActivity.class);

            i.putExtra("KodeQR", data.getQrCode());
            i.putExtra("Lokasi", data.getLokasi());
            i.putExtra("Latitude", data.getLatitude());
            i.putExtra("Longitude", data.getLongitude());
            i.putExtra("User", data.getUserName());
            i.putExtra("Hari", data.getDataDate());
            i.putExtra("Waktu", data.getDataTime());
            i.putExtra("Key", data.getKey());
            i.putExtra("Keterangan", data.getKeterangan());
            i.putExtra("Status", data.isPeriksa());

            context.startActivity(i);
        });

        holder.vhRelativeEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, EditActivity.class);

            i.putExtra("KodeQR", data.getQrCode());
            i.putExtra("Lokasi", data.getLokasi());
            i.putExtra("Latitude", data.getLatitude());
            i.putExtra("Longitude", data.getLongitude());
            i.putExtra("User", data.getUserName());
            i.putExtra("Hari", data.getDataDate());
            i.putExtra("Waktu", data.getDataTime());
            i.putExtra("Key", data.getKey());
            i.putExtra("Keterangan", data.getKeterangan());
            i.putExtra("Status", data.isPeriksa());

            context.startActivity(i);
        });

        String[] userRole = {""};

        if (currentUser != null) {
            String userId = currentUser.getUid();

            userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserClass userData = snapshot.getValue(UserClass.class);

                        userRole[0] = userData.getRole();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Gagal mengambil data user", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "Autentikasi gagal, silahkan login kembali", Toast.LENGTH_SHORT).show();
        }

        holder.cardView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_card_item, null);
            builder.setView(dialogView);

            MaterialButton btnDownloadQR = dialogView.findViewById(R.id.btnDownloadQR);
            MaterialButton btnHapus = dialogView.findViewById(R.id.btnHapus);

            AlertDialog dialog = builder.create();

            btnDownloadQR.setOnClickListener(view -> {
                File folder = new File(Environment.getExternalStorageDirectory()
                        +"/Documents/"+ context.getString(R.string.folder_name));
                String fileName = "QR_" + data.getQrCode() + ".png";

                try {
                        downloadQRCode(data.getQrCode());

                } catch (Exception e){
                    e.printStackTrace();
                    success = false;
                }
            });

            btnHapus.setOnClickListener(view -> {
                if (userRole[0].equals("Admin")) {
                    new AlertDialog.Builder(context)
                            .setTitle("Konfirmasi Penghapusan")
                            .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                            .setPositiveButton("Ya", (dialogInterface, which) -> {
                                deleteData(data.getKey());
                                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Tidak", (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                            })
                            .show();
                } else {
                    Toast.makeText(context, "Hanya admin yang dapat menghapus data", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });

            dialog.show();
            return true;
        });

        holder.vhMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitude = data.getLatitude();
                Double longitude = data.getLongitude();

                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                view.getContext().startActivity(mapIntent);
            }
        });
    }

    private void downloadQRCode(String qrCode) {
        int size = 500;

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCode, BarcodeFormat.QR_CODE, size, size);

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            Bitmap qrWithTextBitmap = addTextToQRCode(bitmap, qrCode);

            File savedFile = saveBitmap(qrWithTextBitmap, qrCode);

            if (savedFile != null) {
                scanMedia(savedFile);

            } else {
                Toast.makeText(context, "Gagal menyimpan QR code", Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(context, "Gagal membuat QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap addTextToQRCode(Bitmap qrBitmap, String qrCode) {
        int width = qrBitmap.getWidth();
        int height = qrBitmap.getHeight();

        Bitmap bitmapWithText = Bitmap.createBitmap(width, height + 100, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmapWithText);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(qrBitmap, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);

        int xPos = canvas.getWidth() / 2;
        int yPos = height + 50;

        canvas.drawText(qrCode, xPos, yPos, paint);

        return bitmapWithText;
    }

    private File saveBitmap(Bitmap bitmap, String qrCode) {
        String fileName = "QR_" + qrCode + ".png";

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "QR_Code");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();

            Toast.makeText(context, "QR code disimpan di: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void scanMedia(File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, (path, uri) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        });
    }

    private void deleteData(String key) {
        reference = FirebaseDatabase.getInstance().getReference("Check Point");
        riwayatDatabase = FirebaseDatabase.getInstance().getReference("Riwayat");

        reference.child(key).removeValue();
        riwayatDatabase.child(key).removeValue();

        Toast.makeText(context, "Data dengan key: " + key + " dihapus", Toast.LENGTH_SHORT).show();
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

    TextView vhLokasi, vhUsername, vhTanggal, vhLatitude, vhLongitude, vhDetail, vhTextview, vhStatus, dtKodeQR, dtLokasi, dtUser;
    ConstraintLayout vhExpandableCard;
    LinearLayout vhMap;
    RelativeLayout vhRelativeEdit, vhRelativeHistory;
    CardView cardView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        vhLokasi = itemView.findViewById(R.id.tvLokasi);
        vhUsername = itemView.findViewById(R.id.tvUser);
        vhTanggal = itemView.findViewById(R.id.tvTanggal);
        vhLatitude = itemView.findViewById(R.id.tvLatitude);
        vhLongitude = itemView.findViewById(R.id.tvLongitude);
        vhTextview = itemView.findViewById(R.id.textView);
        cardView = itemView.findViewById(R.id.recycler_card);
        vhExpandableCard = itemView.findViewById(R.id.expandableCard);
        vhDetail = itemView.findViewById(R.id.recDetail);
        vhMap = itemView.findViewById(R.id.linearLayoutMap);
        vhRelativeEdit = itemView.findViewById(R.id.relativeLayout);
        vhRelativeHistory = itemView.findViewById(R.id.relativeLayout2);
        vhStatus = itemView.findViewById(R.id.tvStatus);

        dtKodeQR = itemView.findViewById(R.id.recDetailQR);
        dtLokasi = itemView.findViewById(R.id.detailLokasi);
        dtUser = itemView.findViewById(R.id.detailPemeriksa);
    }
}