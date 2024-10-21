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

        // Set text sizes and styles
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        Paint labelPaint = new Paint();
        labelPaint.setTextSize(14);

        Paint dataPaint = new Paint();
        dataPaint.setTextSize(14);
        dataPaint.setFakeBoldText(true);

        // Paint for borders
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        // Draw title "Flight Details"
        int titleYPosition = 40; // Position for title
        canvas.drawText("Flight Details", 10, titleYPosition, titlePaint);

        // Starting position for the table
        int startX = 10;
        int labelColumnX = startX + 5;  // For labels
        int dataColumnX = startX + 150; // For corresponding data
        int rowHeight = 30; // Height for each row
        int yPosition = titleYPosition + 40; // Start the table after the title
        int endX = pageInfo.getPageWidth() - 10; // Right end for the border

        // Draw borders for the table (each row)
        String[] labels = {"From:", "To:", "Date:", "Time:", "Class:", "Price:", "Airline:", "Seat:"};
        String[] data = {
                flight.getFromShort(),
                flight.getToShort(),
                flight.getDate(),
                flight.getTime(),
                flight.getClassSeat(),
                "$" + flight.getPrice(),
                flight.getAirlineName(),
                flight.getPassenger()
        };

        for (int i = 0; i < labels.length; i++) {
            // Draw label column borders
            canvas.drawRect(startX, yPosition - rowHeight + 5, dataColumnX - 5, yPosition + 5, borderPaint);

            // Draw data column borders
            canvas.drawRect(dataColumnX, yPosition - rowHeight + 5, endX, yPosition + 5, borderPaint);

            // Draw label text
            canvas.drawText(labels[i], labelColumnX, yPosition, labelPaint);

            // Draw data text
            canvas.drawText(data[i], dataColumnX + 5, yPosition, dataPaint);

            yPosition += rowHeight;
        }

        // Adding spacing between the table and the QR code
        yPosition += 10;

        // Draw QR code
        if (qrCodeBitmap != null) {
            int qrCodeX = labelColumnX;
            canvas.drawBitmap(qrCodeBitmap, qrCodeX, yPosition, null);
        }

        // Add spacing between QR code and Barcode
        yPosition += 400; // Adjust based on QR code height

        // Draw Barcode
        if (barcodeBitmap != null) {
            int barcodeX = labelColumnX;
            canvas.drawBitmap(barcodeBitmap, barcodeX, yPosition, null);
        }

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