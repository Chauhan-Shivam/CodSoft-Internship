import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CurrencyConverter {

    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your API key
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java CurrencyConverter <amount> <base_currency> <target_currency>");
            return;
        }

        double amount = Double.parseDouble(args[0]);
        String baseCurrency = args[1].toUpperCase();
        String targetCurrency = args[2].toUpperCase();

        convertCurrency(amount, baseCurrency, targetCurrency);
    }

    private static void convertCurrency(double amount, String baseCurrency, String targetCurrency) {
        String url = BASE_URL + baseCurrency;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.err.println("Response not successful: " + response.message());
                    return;
                }

                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONObject rates = jsonObject.getJSONObject("conversion_rates");

                    if (rates.has(targetCurrency)) {
                        double rate = rates.getDouble(targetCurrency);
                        double convertedAmount = amount * rate;
                        System.out.printf("%.2f %s = %.2f %s%n", amount, baseCurrency, convertedAmount, targetCurrency);
                    } else {
                        System.out.println("Target currency not available.");
                    }
                } catch (JSONException e) {
                    System.err.println("JSON parsing error: " + e.getMessage());
                }
            }
        });
    }
}
