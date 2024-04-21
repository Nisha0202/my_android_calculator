//can perform calculation like 4/2 = 2
//can perform calculation like 5+2/2 = 1
// calculate following 'BODMAS'
// shows error in wrong expression
// saved calculation history
package com.example.mycalculator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Stack;
public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView result, input;
    Button add, subtract, multiply, divide;
    Button  button3, button2, button1, button0,  button7, button8, button9, button4, button5, button6;
    Button buttonDecimal, buttonDelete, equal, menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SharedPreferences and editor
        prefs = getSharedPreferences("CalculatorHistory", MODE_PRIVATE);
        editor = prefs.edit();
        //initialize buttons
        equal = findViewById(R.id.buttonEquals);
        result = findViewById(R.id.result);
        input = findViewById(R.id.input);
        add = findViewById(R.id.buttonPlus);
        subtract = findViewById(R.id.buttonSubtract);
        multiply = findViewById(R.id.buttonMultiply);
        divide = findViewById(R.id.buttonDivide);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button3 = findViewById(R.id.button3);
        button2 = findViewById(R.id.button2);
        button1 = findViewById(R.id.button1);
        button0 = findViewById(R.id.buttonZero);
        buttonDecimal = findViewById(R.id.buttonDecimal);
        buttonDelete = findViewById(R.id.buttonDelete);
        menu = findViewById(R.id.button_menu);
        //taking value of button
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                input.append(b.getText().toString());
            }
        };
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        button0.setOnClickListener(listener);
        button7.setOnClickListener(listener);
        button8.setOnClickListener(listener);
        button9.setOnClickListener(listener);
        button4.setOnClickListener(listener);
        button5.setOnClickListener(listener);
        button6.setOnClickListener(listener);
        multiply.setOnClickListener(listener);
        add.setOnClickListener(listener);
        subtract.setOnClickListener(listener);
        divide.setOnClickListener(listener);
        buttonDecimal.setOnClickListener(listener);
        //delete
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = input.getText().toString();
                if (!currentText.isEmpty()) {
                    String updatedText = currentText.substring(0, currentText.length() - 1);
                    input.setText(updatedText);
                }
            }
        });
        //result
        equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Calculate the result
                    double calculationResult = calculate(input.getText().toString());
                    result.setText(String.valueOf(calculationResult));
                    // Save
                    saveEq(input.getText().toString(), String.valueOf(calculationResult));
                } catch (Exception e) {
                    result.setText(e.getMessage());
                    saveEq(input.getText().toString(), e.getMessage());
                }
            }
        });
//storing and seeing history
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayHis();
            }
        });
    }
    private double calculate(String expression) {
        // Checking for invalid sequences
        if (expression.contains(" ") || expression.contains("++") || expression.contains("--") || expression.contains("+-") ||
                expression.contains("**") || expression.contains("//") || expression.contains("+/")
                || expression.contains("/*") || expression.contains("*/") || expression.contains("/-")
                || expression.contains("-/") || expression.contains("+*") || expression.contains("*+")
                ||expression.contains("*-") || expression.contains("-*") || expression.contains("-+") ||
                expression.contains("..") || expression.contains("-.") || expression.contains(".+")
                || expression.contains(".-") || expression.contains("+.") || expression.contains("*.") || expression.contains(".*")
                || expression.contains("/.") || expression.contains("./")) {
                           throw new IllegalArgumentException("Invalid expression");
        }
        //stacks for numbers and operators
        Stack<Double> numStack = new Stack<>();
        Stack<Character> opStack = new Stack<>();
        int numStart = -1;
        boolean point = false; //checking decimal point occurrence
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            // If current character is a digit
            if (Character.isDigit(c) || c == '.') {
                if (c == '.') {
                    if (point) {
                        throw new IllegalArgumentException("Invalid number with multiple decimal points");
                    } else {
                        point = true;
                    }
                }}
                else {
                // If current character is an operator
                if (numStart != -1) {
                    numStack.push(Double.parseDouble(expression.substring(numStart, i)));
                    numStart = -1;
                }
                while (!opStack.isEmpty() && priority(c) <= priority(opStack.peek())) {
                    processOp(numStack, opStack);
                }
                opStack.push(c);
            }
        }
        // Perform remaining operations
        while (!opStack.isEmpty()) {
            processOp(numStack, opStack);
        }
        return numStack.pop();
    }
    private void processOp(Stack<Double> numStack, Stack<Character> opStack) {
        char op = opStack.pop();
        double right = numStack.pop();
        double left = numStack.pop();
        switch(op) {
            case '+':
                numStack.push(left + right);
                break;
            case '-':
                numStack.push(left - right);
                break;
            case '*':
                numStack.push(left * right);
                break;
            case '/':
                if (right == 0)
                    throw new IllegalArgumentException("Can not divide by zero");
                numStack.push(left / right);
                break;
        }
    }
    private int priority(char op) {
        switch(op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return -1;
        }
    }
    // Save the equation
    private void saveEq(String equation, String result) {
        //current history
        String history = prefs.getString("history", "");
        history = equation + " = " + result + ";" + history;
        //storing only last 10 equations
        String[] historyArray = history.split(";");
        if (historyArray.length > 10) {
            history = TextUtils.join(";", Arrays.copyOfRange(historyArray, 0, 10));
        }
        editor.putString("history", history);
        editor.apply();
    }
    private void displayHis() {
        // Getting history from SharedPreferences
        String history = prefs.getString("history", "");
        String[] historyArray = history.split(";");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calculation History");
        ListView listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyArray);
        listView.setAdapter(adapter);
        builder.setView(listView);
        //clicking an item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedEquation = (String) parent.getItemAtPosition(position);
                //Setting the selected equation
                String[] parts = selectedEquation.split(" = ");
                if (parts.length >= 2) {
                    input.setText(parts[0]); // equation
                    result.setText(parts[1]); // result
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Invalid format in history", Toast.LENGTH_SHORT).show();}
            }
        });
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }
}
