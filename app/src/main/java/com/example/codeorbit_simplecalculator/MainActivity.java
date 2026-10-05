package com.example.codeorbit_simplecalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay, tvHistory;
    private double firstOperand = 0;
    private String currentOperator = "";
    private boolean isOperatorPressed = false;
    private boolean isResultCalculated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tvDisplay);
        tvHistory = findViewById(R.id.tvHistory);

        // Numeric button bindings
        int[] numButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };

        for (int id : numButtonIds) {
            findViewById(id).setOnClickListener(v -> {
                Button btn = (Button) v;
                onNumberClick(btn.getText().toString());
            });
        }

        // Decimal point
        findViewById(R.id.btnDot).setOnClickListener(v -> onDotClick());

        // Operations
        findViewById(R.id.btnAdd).setOnClickListener(v -> onOperatorClick("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> onOperatorClick("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorClick("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorClick("÷"));

        // Equals & Controls
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
        findViewById(R.id.btnClear).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> handleBackspace());
    }

    private void onNumberClick(String digit) {
        if (isOperatorPressed || isResultCalculated || tvDisplay.getText().toString().equals("0")) {
            tvDisplay.setText(digit);
            isOperatorPressed = false;
            isResultCalculated = false;
        } else {
            tvDisplay.append(digit);
        }
    }

    private void onDotClick() {
        if (isOperatorPressed || isResultCalculated) {
            tvDisplay.setText("0.");
            isOperatorPressed = false;
            isResultCalculated = false;
            return;
        }
        if (!tvDisplay.getText().toString().contains(".")) {
            tvDisplay.append(".");
        }
    }

    private void onOperatorClick(String op) {
        if (!currentOperator.isEmpty() && !isOperatorPressed && !isResultCalculated) {
            calculateResult();
        }
        try {
            firstOperand = Double.parseDouble(tvDisplay.getText().toString());
            currentOperator = op;
            tvHistory.setText(formatNumber(firstOperand) + " " + currentOperator);
            isOperatorPressed = true;
            isResultCalculated = false;
        } catch (NumberFormatException ignored) {}
    }

    private void calculateResult() {
        if (currentOperator.isEmpty() || isOperatorPressed) return;

        double secondOperand;
        try {
            secondOperand = Double.parseDouble(tvDisplay.getText().toString());
        } catch (NumberFormatException e) {
            return;
        }

        double result = 0;
        switch (currentOperator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "×":
                result = firstOperand * secondOperand;
                break;
            case "÷":
                if (secondOperand == 0) {
                    Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                    tvDisplay.setText("Error");
                    tvHistory.setText("");
                    currentOperator = "";
                    isResultCalculated = true;
                    return;
                }
                result = firstOperand / secondOperand;
                break;
        }

        tvHistory.setText(formatNumber(firstOperand) + " " + currentOperator + " " + formatNumber(secondOperand) + " =");
        tvDisplay.setText(formatNumber(result));
        firstOperand = result;
        currentOperator = "";
        isResultCalculated = true;
    }

    private void clearAll() {
        tvDisplay.setText("0");
        tvHistory.setText("");
        firstOperand = 0;
        currentOperator = "";
        isOperatorPressed = false;
        isResultCalculated = false;
    }

    private void handleBackspace() {
        String text = tvDisplay.getText().toString();
        if (isResultCalculated || text.equals("Error")) {
            clearAll();
            return;
        }
        if (text.length() > 1) {
            tvDisplay.setText(text.substring(0, text.length() - 1));
        } else {
            tvDisplay.setText("0");
        }
    }

    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }
        return String.format("%.6f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}