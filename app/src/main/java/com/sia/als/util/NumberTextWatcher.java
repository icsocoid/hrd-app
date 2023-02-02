package com.sia.als.util;

import java.text.DecimalFormat;
import java.text.ParseException;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class NumberTextWatcher implements TextWatcher {

    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private EditText editText;

    public NumberTextWatcher(EditText et)
    {
        this.editText = et;

    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int cursorPosition, int before,
                              int count)
    {
        if (before == 0 && count == 1) {  //Entering values

            String val = s.toString();
            String a = "";
            String b = "";
            String c = "";
            String d = "";
            String e = "";
            String f = "";
            if (val != null && val.length() > 0) {
                val = val.replace(".", "");
                if (val.length() >= 2) {
                    a = val.substring(0, 2);
                }
                else if (val.length() < 2) {
                    a = val.substring(0, val.length());
                }
                if (val.length() > 2 && val.length() < 5) {
                    b = val.substring(2, 5);
                }
                else if(val.length() > 5 && val.length() < 8)
                {
                    c = val.substring(5, 8);
                }
                else if(val.length() > 8 && val.length() < 10)
                {
                    d = val.substring(8, 9);
                }
                StringBuffer stringBuffer = new StringBuffer();
                if (a != null && a.length() > 0) {
                    stringBuffer.append(a);

                }
                if (b != null && b.length() > 0) {
                    stringBuffer.append(".");
                    stringBuffer.append(b);

                }
                if (c != null && c.length() > 0) {
                    stringBuffer.append(".");
                    stringBuffer.append(c);
                }
                editText.removeTextChangedListener(this);
                editText.setText(stringBuffer.toString());
                if (cursorPosition == 3 || cursorPosition == 7) {
                    cursorPosition = cursorPosition + 2;
                } else {
                    cursorPosition = cursorPosition + 1;
                }
                if (cursorPosition <= editText.getText().toString().length()) {
                    editText.setSelection(cursorPosition);
                } else {
                    editText.setSelection(editText.getText().toString().length());
                }
                editText.addTextChangedListener(this);
            }
            else {
                editText.removeTextChangedListener(this);
                editText.setText("");
                editText.addTextChangedListener(this);
            }

        }

        if (before == 1 && count == 0) {  //Deleting values

            String val = s.toString();
            String a = "";
            String b = "";
            String c = "";
            String d = "";
            String e = "";
            String f = "";

            if (val != null && val.length() > 0) {
                val = val.replace("-", "");
                if (cursorPosition == 3) {
                    val = removeCharAt(val, cursorPosition - 1, s.toString().length() - 1);
                } else if (cursorPosition == 7) {
                    val = removeCharAt(val, cursorPosition - 2, s.toString().length() - 2);
                }
                if (val.length() >= 2) {
                    a = val.substring(0, 2);
                }
                else if (val.length() < 2) {
                    a = val.substring(0, val.length());
                }
                if (val.length() > 2 && val.length() < 5) {
                    b = val.substring(2, 5);
                }
                else if(val.length() > 5 && val.length() < 8)
                {
                    c = val.substring(5, 8);
                }
                else if(val.length() > 8 && val.length() < 10)
                {
                    d = val.substring(8, 9);
                }
                StringBuffer stringBuffer = new StringBuffer();
                if (a != null && a.length() > 0) {
                    stringBuffer.append(a);

                }
                if (b != null && b.length() > 0) {
                    stringBuffer.append(".");
                    stringBuffer.append(b);

                }
                if (c != null && c.length() > 0) {
                    stringBuffer.append(".");
                    stringBuffer.append(c);
                }
                editText.removeTextChangedListener(this);
                editText.setText(stringBuffer.toString());
                if (cursorPosition == 3 || cursorPosition == 7) {
                    cursorPosition = cursorPosition - 1;
                }
                if (cursorPosition <= editText.getText().toString().length()) {
                    editText.setSelection(cursorPosition);
                } else {
                    editText.setSelection(editText.getText().toString().length());
                }
                editText.addTextChangedListener(this);
            } else {
                editText.removeTextChangedListener(this);
                editText.setText("");
                editText.addTextChangedListener(this);
            }

        }
    }
    public static String removeCharAt(String s, int pos, int length) {

        String value = "";
        if (length > pos) {
            value = s.substring(pos + 1);
        }
        return s.substring(0, pos) + value;
    }


}
