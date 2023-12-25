package com.ftel.demo;

public class Demo {
    public static void main(String[] args) {
        double originalNumber = 71.1;
        int roundingFactor = 10;

        // Làm tròn lên đến số nguyên gần nhất
        double roundedNumber = Math.ceil(originalNumber);

        // Làm cho kết quả là bội số của roundingFactor
        int result = (int) Math.ceil(originalNumber / roundingFactor) * roundingFactor;

        System.out.println("Original number: " + originalNumber);
        System.out.println("Rounded number: " + roundedNumber);
        System.out.println("Rounded to the nearest multiple of " + roundingFactor + ": " + result);
    }
}
