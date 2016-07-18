package com.mysampleapp.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.util.Set;

public class DemoNoSQLRecipeResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final RecipeDO result;

    DemoNoSQLRecipeResult(final RecipeDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getAdvice();
        result.setAdvice(DemoSampleDataGenerator.getRandomSampleString("Advice"));
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setAdvice(originalValue);
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView nameKeyTextView;
        final TextView nameValueTextView;
        final TextView difficultyKeyTextView;
        final TextView difficultyValueTextView;
        final TextView adviceKeyTextView;
        final TextView adviceValueTextView;
        final TextView amountKeyTextView;
        final TextView amountValueTextView;
        final TextView cookingTimeKeyTextView;
        final TextView cookingTimeValueTextView;
        final TextView countryKeyTextView;
        final TextView countryValueTextView;
        final TextView introductionKeyTextView;
        final TextView introductionValueTextView;
        final TextView preparationTimeKeyTextView;
        final TextView preparationTimeValueTextView;
        final TextView typeKeyTextView;
        final TextView typeValueTextView;
        final TextView vegetarianKeyTextView;
        final TextView vegetarianValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            nameKeyTextView = new TextView(context);
            nameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(nameKeyTextView, nameValueTextView);
            layout.addView(nameKeyTextView);
            layout.addView(nameValueTextView);

            difficultyKeyTextView = new TextView(context);
            difficultyValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(difficultyKeyTextView, difficultyValueTextView);
            layout.addView(difficultyKeyTextView);
            layout.addView(difficultyValueTextView);

            adviceKeyTextView = new TextView(context);
            adviceValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(adviceKeyTextView, adviceValueTextView);
            layout.addView(adviceKeyTextView);
            layout.addView(adviceValueTextView);

            amountKeyTextView = new TextView(context);
            amountValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(amountKeyTextView, amountValueTextView);
            layout.addView(amountKeyTextView);
            layout.addView(amountValueTextView);

            cookingTimeKeyTextView = new TextView(context);
            cookingTimeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(cookingTimeKeyTextView, cookingTimeValueTextView);
            layout.addView(cookingTimeKeyTextView);
            layout.addView(cookingTimeValueTextView);

            countryKeyTextView = new TextView(context);
            countryValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(countryKeyTextView, countryValueTextView);
            layout.addView(countryKeyTextView);
            layout.addView(countryValueTextView);

            introductionKeyTextView = new TextView(context);
            introductionValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(introductionKeyTextView, introductionValueTextView);
            layout.addView(introductionKeyTextView);
            layout.addView(introductionValueTextView);

            preparationTimeKeyTextView = new TextView(context);
            preparationTimeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(preparationTimeKeyTextView, preparationTimeValueTextView);
            layout.addView(preparationTimeKeyTextView);
            layout.addView(preparationTimeValueTextView);

            typeKeyTextView = new TextView(context);
            typeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(typeKeyTextView, typeValueTextView);
            layout.addView(typeKeyTextView);
            layout.addView(typeValueTextView);

            vegetarianKeyTextView = new TextView(context);
            vegetarianValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(vegetarianKeyTextView, vegetarianValueTextView);
            layout.addView(vegetarianKeyTextView);
            layout.addView(vegetarianValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            nameKeyTextView = (TextView) layout.getChildAt(1);
            nameValueTextView = (TextView) layout.getChildAt(2);

            difficultyKeyTextView = (TextView) layout.getChildAt(3);
            difficultyValueTextView = (TextView) layout.getChildAt(4);

            adviceKeyTextView = (TextView) layout.getChildAt(5);
            adviceValueTextView = (TextView) layout.getChildAt(6);

            amountKeyTextView = (TextView) layout.getChildAt(7);
            amountValueTextView = (TextView) layout.getChildAt(8);

            cookingTimeKeyTextView = (TextView) layout.getChildAt(9);
            cookingTimeValueTextView = (TextView) layout.getChildAt(10);

            countryKeyTextView = (TextView) layout.getChildAt(11);
            countryValueTextView = (TextView) layout.getChildAt(12);

            introductionKeyTextView = (TextView) layout.getChildAt(13);
            introductionValueTextView = (TextView) layout.getChildAt(14);

            preparationTimeKeyTextView = (TextView) layout.getChildAt(15);
            preparationTimeValueTextView = (TextView) layout.getChildAt(16);

            typeKeyTextView = (TextView) layout.getChildAt(17);
            typeValueTextView = (TextView) layout.getChildAt(18);

            vegetarianKeyTextView = (TextView) layout.getChildAt(19);
            vegetarianValueTextView = (TextView) layout.getChildAt(20);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        nameKeyTextView.setText("Name");
        nameValueTextView.setText(result.getName());
        difficultyKeyTextView.setText("Difficulty");
        difficultyValueTextView.setText("" + result.getDifficulty().longValue());
        adviceKeyTextView.setText("Advice");
        adviceValueTextView.setText(result.getAdvice());
        amountKeyTextView.setText("Amount");
        amountValueTextView.setText("" + result.getAmount().longValue());
        cookingTimeKeyTextView.setText("CookingTime");
        cookingTimeValueTextView.setText("" + result.getCookingTime().longValue());
        countryKeyTextView.setText("Country");
        countryValueTextView.setText(result.getCountry());
        introductionKeyTextView.setText("Introduction");
        introductionValueTextView.setText(result.getIntroduction());
        preparationTimeKeyTextView.setText("PreparationTime");
        preparationTimeValueTextView.setText("" + result.getPreparationTime().longValue());
        typeKeyTextView.setText("Type");
        typeValueTextView.setText(result.getType());
        vegetarianKeyTextView.setText("Vegetarian");
        vegetarianValueTextView.setText("" + result.getVegetarian());
        return layout;
    }
}
