package com.shamnas.ticket_booking.activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.shamnas.ticket_booking.databinding.ActivityTicketDetailBinding;
import com.shamnas.ticket_booking.model.Flight;

import java.io.FileOutputStream;
import java.io.IOException;


public class TicketDetailActivity extends BaseActivity {
    private ActivityTicketDetailBinding binding;
    private Flight flight;
    private Bitmap qrCodeBitmap;
    private Bitmap barcodeBitmap;
    private static final int CREATE_PDF_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        getIntentExtra();
        setVariable();

        binding.downloadTicketBtn.setOnClickListener(v -> createPdfRequest());
    }

    private void createPdfRequest() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "TicketDetails.pdf");

//        Launch the intent directly without using MediaStore.createWriteRequest
        startActivityForResult(intent, CREATE_PDF_REQUEST_CODE);
    }


    // Override onActivityResult to handle the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                createPdf(uri);
            } else {
                Toast.makeText(this, "Error getting PDF URI", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPdf(Uri uri) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);

        // Starting Y position for drawing text
        int yPosition = 25;

        // Draw the ticket details line by line
        canvas.drawText("Flight Details:", 10, yPosition, paint);
        yPosition += 20; // Move down for the next line

        canvas.drawText("From: " + flight.getFromShort(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("To: " + flight.getToShort(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("Date: " + flight.getDate(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("Time: " + flight.getTime(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("Class: " + flight.getClassSeat(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("Price: $" + flight.getPrice(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("Airline: " + flight.getAirlineName(), 10, yPosition, paint);
        yPosition += 20;

        canvas.drawText("Seat: " + flight.getPassenger(), 10, yPosition, paint);
        yPosition += 20;

        // Finish the page
        pdfDocument.finishPage(page);

        // Save the document
        try (FileOutputStream fos = (FileOutputStream) getContentResolver().openOutputStream(uri)) {
            pdfDocument.writeTo(fos);
            Toast.makeText(this, "PDF downloaded successfully", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

    private void setVariable() {
        binding.backBtnTicket.setOnClickListener(v -> finish());
        binding.fromTxt.setText(flight.getFromShort());
        binding.fromSmallTxt.setText(flight.getFrom());
        binding.toTxt.setText(flight.getTo());
        binding.toShortTxt.setText(flight.getToShort());
        binding.toSmallTxt.setText(flight.getTo());
        binding.dateTxt.setText(flight.getDate());
        binding.timeTxt.setText(flight.getTime());
        binding.arrivalTxt.setText(flight.getArriveTime());
        binding.classTxtTicket.setText(flight.getClassSeat());
        binding.priceTxtTicket.setText("$"+flight.getPrice());
        binding.airlinesTxt.setText(flight.getAirlineName());
        binding.seatTxt.setText(flight.getPassenger());

        Glide.with(TicketDetailActivity.this)
                .load(flight.getAirlineLogo())
                .into(binding.logo);

        // Display QR code and barcode images
        ImageView qrCodeImageView = binding.qrCodeImageView;
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        ImageView barcodeImageView = binding.barcodeImageView;
        barcodeImageView.setImageBitmap(barcodeBitmap);
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        flight=(Flight) getIntent().getSerializableExtra("flight");
        qrCodeBitmap = generateQRCode(flight.getAirlineName());
        barcodeBitmap = generateBarcode(flight.getAirlineName());
    }

    private Bitmap generateQRCode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap generateBarcode(String data) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, 400, 100);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}