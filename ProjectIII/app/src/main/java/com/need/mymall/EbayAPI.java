package com.need.mymall;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class EbayAPI {

    private static final String appID = "TunPhm-MyApp-PRD-ee65479ab-79e4e039";
    private static final String certID = "PRD-e65479abf069-7eec-44fa-942e-e546";
    private static final String re_Name = "Tu_n_Ph_m-TunPhm-MyApp-PR-wwqpoogbw";

    public String token = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getToken () {
        String url = "https://api.ebay.com/identity/v1/oauth2/token";

        GetToken asyncTask = new GetToken();
        asyncTask.execute(url);

        try {
            String result = asyncTask.get();
            JSONObject jsonObject = new JSONObject(result);
            token = jsonObject.getString("access_token");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ProductDatabase getItem (String itemid) {
        String url = "https://api.ebay.com/buy/browse/v1/item/" + itemid;

        GetItem asyncTask = new GetItem(this.getToken());
        asyncTask.execute(url);

        ProductDatabase item = new ProductDatabase();

        try {
            String result = asyncTask.get();
            JSONObject jsonObject = new JSONObject(result);

            item.setItemId(jsonObject.getString("itemId"));
            item.setTitle(jsonObject.getString("title"));
            item.setPrice(jsonObject.getJSONObject("price").getString("value"));
            if (jsonObject.has("shortDescription")) {
                item.setShortDescription(jsonObject.getString("shortDescription"));
            } else {
                item.setShortDescription("Không có mô tả ngắn");
            }
            String description = jsonObject.getString("description");
            item.setDescription(description);

            if (jsonObject.has("itemWebUrl")) {
                String webUrl = jsonObject.getString("itemWebUrl");
                int index = webUrl.lastIndexOf("/itm/") + 4;

                int lastIndex = index;
                for (int i = index + 1; i < webUrl.length(); i++) {
                    if (webUrl.charAt(i) != '/') {
                        lastIndex = i;
                        continue;
                    }
                    break;
                }
                String name1 = webUrl.substring(index + 1, lastIndex + 1);
                item.setName(name1);
            }

            ArrayList<String> imagesURL = new ArrayList<>();
            imagesURL.add(jsonObject.getJSONObject("image").getString("imageUrl"));
            item.setImagesUrl(imagesURL);
            if (jsonObject.has("additionalImages")) {
                JSONArray additionalImages = jsonObject.getJSONArray("additionalImages");
                for (int i = 0 ;i<additionalImages.length();i++) {
                    JSONObject image = additionalImages.getJSONObject(i);
                    imagesURL.add(image.getString("imageUrl"));
                }
                item.setImagesUrl(imagesURL);
            }
            ArrayList<ProductSpecificationModel> productSpecificationModels = new ArrayList<>();
            if (jsonObject.has("localizedAspects")) {
                JSONArray localizedAspects = jsonObject.getJSONArray("localizedAspects");
                for (int i = 0;i<localizedAspects.length();i++) {
                    JSONObject feature = localizedAspects.getJSONObject(i);
                    String name = feature.getString("name");
                    String value = feature.getString("value");
                    productSpecificationModels.add(new ProductSpecificationModel(name,value));
                }
                item.setLocalizedAspects(productSpecificationModels);
            }

            item.setReviewCount(jsonObject.getJSONObject("primaryProductReviewRating").getInt("reviewCount"));
            item.setAverageRating(jsonObject.getJSONObject("primaryProductReviewRating").getString("averageRating"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ProductDatabase getItemByEpid (String epid) {
        ProductDatabase item = new ProductDatabase();

        ProductDatabase result = new ProductDatabase();

        String url = "https://api.ebay.com/buy/browse/v1/item_summary/search?epid=" + epid + "&limit=1";

        getItemByEpid asyncTask = new getItemByEpid(this.getToken());
        asyncTask.execute(url);

        try {
            String rs = asyncTask.get();
            JSONObject jsonObject = new JSONObject(rs);

            JSONArray itemSummaries = jsonObject.getJSONArray("itemSummaries");

            for (int i = 0;i<itemSummaries.length();i++) {
                JSONObject object = itemSummaries.getJSONObject(i);
                item.setItemId(object.getString("itemId"));
                JSONArray categories = object.getJSONArray("categories");
                ArrayList<String> productCategoties = new ArrayList<>();
                for (int j=0;j<categories.length();j++) {
                    JSONObject categoryId = categories.getJSONObject(j);
                    productCategoties.add(categoryId.getString("categoryId"));
                }
                item.setCategories(productCategoties);
            }

            result = this.getItem(item.getItemId());
            result.setCategories(item.getCategories());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ProductDatabase getItemByName (String name, String link) {

        ProductDatabase item = new ProductDatabase();
        ProductDatabase result = new ProductDatabase();

        String url = "https://api.ebay.com/buy/browse/v1/item_summary/search?q=" + name + "&limit=1";

        getItemByEpid asyncTask = new getItemByEpid(this.getToken());
        asyncTask.execute(url);

        try {
            String rs = asyncTask.get();
            JSONObject jsonObject = new JSONObject(rs);

            int total = jsonObject.getInt("total");
            if (total>0) {
                JSONArray itemSummaries = jsonObject.getJSONArray("itemSummaries");

                for (int i = 0;i<itemSummaries.length();i++) {
                    JSONObject object = itemSummaries.getJSONObject(i);
                    item.setItemId(object.getString("itemId"));
                    JSONArray categories = object.getJSONArray("categories");
                    ArrayList<String> productCategoties = new ArrayList<>();
                    for (int j=0;j<categories.length();j++) {
                        JSONObject categoryId = categories.getJSONObject(j);
                        productCategoties.add(categoryId.getString("categoryId"));
                    }
                    item.setCategories(productCategoties);
                }
                result = this.getItem(item.getItemId());
                result.setCategories(item.getCategories());

                return result;
            } else {
                return null;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<ProductDatabase> search (String name, Dialog loadingdialog) {

        ArrayList<ProductDatabase> result = new ArrayList<>();

        loadingdialog.show();

        String url = "https://api.ebay.com/buy/browse/v1/item_summary/search?q=" + name + "&limit=5";

        getItemByEpid asyncTask = new getItemByEpid(this.getToken());
        asyncTask.execute(url);

        try {
            String rs = asyncTask.get();
            JSONObject jsonObject = new JSONObject(rs);

            int total = jsonObject.getInt("total");
            if (total>0) {
                JSONArray itemSummaries = jsonObject.getJSONArray("itemSummaries");

                for (int i = 0;i<itemSummaries.length();i++) {
                    ProductDatabase item = new ProductDatabase();
                    JSONObject object = itemSummaries.getJSONObject(i);
                    item.setItemId(object.getString("itemId"));
                    JSONArray categories = object.getJSONArray("categories");
                    ArrayList<String> productCategoties = new ArrayList<>();
                    for (int j=0;j<categories.length();j++) {
                        JSONObject categoryId = categories.getJSONObject(j);
                        productCategoties.add(categoryId.getString("categoryId"));
                    }
                    item = getItem(item.getItemId());
                    item.setCategories(productCategoties);
                    result.add(item);
                }
                loadingdialog.dismiss();
                return result;
            } else {
                return null;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    public class GetToken extends AsyncTask<String,Void,String> {

        String rs = null;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            try {
                url = new URL(strings[0]);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String authHeaderData = appID + ":" + certID;
                String encodedAuthHeader = Base64.getEncoder().encodeToString(authHeaderData.getBytes());
                //System.out.println(encodedAuthHeader);
                con.addRequestProperty("Authorization", "Basic " + encodedAuthHeader);
                Map<String, String> arguments = new HashMap<>();
                arguments.put("grant_type", "client_credentials");
                arguments.put("redirect_uri", re_Name);
                arguments.put("scope", "https://api.ebay.com/oauth/api_scope");
                StringJoiner sj = new StringJoiner("&");
                for (Map.Entry<String, String> entry : arguments.entrySet())
                    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));
                byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                int length = out.length;

                con.setFixedLengthStreamingMode(length);

                //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                con.connect();
                try (OutputStream os = con.getOutputStream()) {
                    os.write(out);
                }
                int status = con.getResponseCode();
                InputStream is = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while ((s = reader.readLine()) != null) {
                    sb.append(s);
                }
                rs = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rs;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class GetItem extends AsyncTask<String,Void,String> {

        private String token;
        String rs = null;

        public GetItem(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpsURLConnection uRLConnection = (HttpsURLConnection) url.openConnection();
                uRLConnection.setRequestMethod("GET");
                uRLConnection.addRequestProperty("X-EBAY-C-MARKETPLACE-ID","EBAY_US");
                uRLConnection.addRequestProperty("Authorization","Bearer " + this.token);
                uRLConnection.addRequestProperty("Content-Type", "application/json");
                uRLConnection.addRequestProperty("X-EBAY-C-ENDUSERCTX","contextualLocation=country=<2_character_country_code>,zip=<zip_code>,affiliateCampaignId=<ePNCampaignId>,affiliateReferenceId=<referenceId>");

                InputStream is = uRLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while((s = reader.readLine())!=null) {
                    sb.append(s);
                }
                 rs = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return rs;
        }
    }

    public class getItemByEpid extends AsyncTask <String,Void,String> {

        private String token;
        String rs = null;

        public getItemByEpid(String token) {
            this.token = token;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpsURLConnection uRLConnection = (HttpsURLConnection) url.openConnection();
                uRLConnection.setRequestMethod("GET");
                uRLConnection.addRequestProperty("X-EBAY-C-MARKETPLACE-ID","EBAY_US");
                uRLConnection.addRequestProperty("Authorization","Bearer " + this.token);
                uRLConnection.addRequestProperty("Content-Type", "application/json");
                uRLConnection.addRequestProperty("X-EBAY-C-ENDUSERCTX","affiliateCampaignId=<ePNCampaignId>,affiliateReferenceId=<referenceId>");

                InputStream is = uRLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while((s = reader.readLine())!=null) {
                    sb.append(s);
                }
                rs = sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return rs;
        }
    }
}
