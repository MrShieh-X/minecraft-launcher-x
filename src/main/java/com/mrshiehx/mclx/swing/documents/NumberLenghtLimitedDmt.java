package com.mrshiehx.mclx.swing.documents;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumberLenghtLimitedDmt extends PlainDocument {

    private final int limit;

    public NumberLenghtLimitedDmt(int limit) {
        super();
        this.limit = limit;
    }

    public void insertString
            (int offset, String str, AttributeSet attr)
            throws BadLocationException {
        if (str == null) {
            return;
        }
        if ((getLength() + str.length()) <= limit) {

            char[] upper = str.toCharArray();
            int length = 0;
            for (int i = 0; i < upper.length; i++) {
                if (upper[i] >= '0' && upper[i] <= '9') {
                    upper[length++] = upper[i];
                }
            }
            super.insertString(offset, new String(upper, 0, length), attr);
        }
    }
}