package com.example.splitbillapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // XML elemanlarını Java tarafında tanımlıyoruz
    EditText inputAmount;
    Spinner spinnerPeople, spinnerTip;
    Button btnCalculate;
    TextView txtResultPerPerson, txtTotalTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tanımladığımız değişkenleri XML'deki ID'leri ile eşleştiriyoruz
        inputAmount = findViewById(R.id.inputAmount);
        spinnerPeople = findViewById(R.id.spinnerPeople);
        spinnerTip = findViewById(R.id.spinnerTip);
        btnCalculate = findViewById(R.id.btnCalculate);
        txtResultPerPerson = findViewById(R.id.txtResultPerPerson);
        txtTotalTip = findViewById(R.id.txtTotalTip);

        // --- SPINNER (Açılır Liste) Verilerini Hazırlama ---

        // 1. Kişi Sayısı Listesi (1'den 20'ye kadar)
        List<String> peopleList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            peopleList.add(i + " Kişi");
        }
        // Adapter: Listeyi Spinner'a bağlayan köprü
        ArrayAdapter<String> peopleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, peopleList);
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeople.setAdapter(peopleAdapter);

        // 2. Bahşiş Oranı Listesi
        String[] tipRates = {"Bahşiş Yok", "%5", "%10", "%15", "%20", "%25"};
        ArrayAdapter<String> tipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipRates);
        tipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTip.setAdapter(tipAdapter);


        // --- BUTON TIKLAMA OLAYI ---
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hesapla();
            }
        });
    }

    private void hesapla() {
        // Tutar girilmemişse uyarı verip çıkıyoruz
        String amountString = inputAmount.getText().toString();
        if (amountString.isEmpty()) {
            Toast.makeText(this, "Lütfen bir tutar girin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 1. Girdileri al
            double totalAmount = Double.parseDouble(amountString);

            // Seçilen kişi sayısını al (Örn: "3 Kişi" -> Sadece '3' rakamını almalıyız)
            // String manipulation yapmamak için direkt index kullanabiliriz ama garanti olsun diye pozisyon + 1 yapıyoruz
            // Çünkü liste 1'den başlıyor ama index 0'dan başlar.
            int personCount = spinnerPeople.getSelectedItemPosition() + 1;

            // Seçilen bahşiş oranını al
            double tipAmount = getTipAmount(totalAmount);
            double grandTotal = totalAmount + tipAmount;
            double perPerson = grandTotal / personCount;

            // 3. Sonucu Formatla (Virgülden sonra 2 basamak)
            DecimalFormat df = new DecimalFormat("#.##");

            // 4. Ekrana Yazdır
            txtResultPerPerson.setText(df.format(perPerson) + " TL");
            txtTotalTip.setText("(Toplam Bahşiş: " + df.format(tipAmount) + " TL)");

        } catch (Exception e) {
            // Beklenmedik bir hata olursa
            Toast.makeText(this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private double getTipAmount(double totalAmount) {
        int tipPosition = spinnerTip.getSelectedItemPosition();
        // Listemiz: {"Yok", "%5", "%10", "%15", "%20", "%25"}
        // Position 0 -> %0
        // Position 1 -> %5
        // Position 2 -> %10 ... mantığıyla basit bir matematik:
        double tipPercent = 0;
        if (tipPosition > 0) {
            tipPercent = tipPosition * 5; // Basit mantık: index * 5 (1*5=5, 2*5=10...)
        }

        // 2. Matematiği Yap
        double tipAmount = totalAmount * (tipPercent / 100);
        return tipAmount;
    }
}